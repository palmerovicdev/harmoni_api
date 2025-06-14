package com.palmerodev.harmoni_api.helper;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class VideoSegmenterImpl implements VideoSegmenter {

    private static final String TEMP_PROCESSING_DIR = "temp_service_files/";
    private static final String FFMPEG_PATH = "ffmpeg";

    public VideoSegmenterImpl() {
        try {
            Files.createDirectories(Paths.get(TEMP_PROCESSING_DIR));
        } catch (IOException e) {
            System.err.println("Error al crear el directorio temporal para el servicio: " + TEMP_PROCESSING_DIR);
            e.printStackTrace();
        }
    }

    @Override
    public List<BufferedImage> extractFrames(MultipartFile file) throws Exception {
        List<BufferedImage> frames = new ArrayList<>();
        var originalFile = File.createTempFile("uploaded_", file.getOriginalFilename());
        file.transferTo(originalFile);

        var repairedFile = repairMp4(originalFile);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(repairedFile)) {
            grabber.setOption("fflags", "+genpts");
            grabber.start();

            Frame frame;
            try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                while ((frame = grabber.grabImage()) != null) {
                    BufferedImage img = converter.convert(frame);
                    frames.add(img);
                }
            }

            grabber.stop();
        }
        originalFile.delete();
        repairedFile.delete();

        var isMultipleOfFive = frames.size() % 5 == 0;
        if (!isMultipleOfFive) {
            int framesToRemove = frames.size() % 5;
            for (int i = 0; i < framesToRemove; i++) {
                frames.remove(frames.size() - 1);
            }
        }
        var framesToKeep = frames.size() / 5;
        List<BufferedImage> framesToReturn = new ArrayList<>();
        for (int i = 0; i < frames.size(); i += framesToKeep) {
            framesToReturn.add(frames.get(i));
        }

        return framesToReturn;
    }

    @Override
    public File convertToTempFile(MultipartFile multipartFile) throws Exception {
        File tempFile = File.createTempFile("uploaded", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    @Override
    public List<File> extractAudioBlocks(MultipartFile file) throws Exception {
        return List.of(extractAudioToWav(file));
    }

    public File extractAudioToWav(MultipartFile videoFile) throws IOException, InterruptedException {
        String uniqueId = UUID.randomUUID().toString();
        String originalFileName = videoFile.getOriginalFilename();
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        Path inputFilePath = Paths.get(TEMP_PROCESSING_DIR, uniqueId + "_original" + fileExtension);
        String outputFileName = uniqueId + "_extracted_audio.wav";
        Path outputFilePath = Paths.get(TEMP_PROCESSING_DIR, outputFileName);

        try {
            Files.copy(videoFile.getInputStream(), inputFilePath, StandardCopyOption.REPLACE_EXISTING);

            List<String> command = Arrays.asList(
                    FFMPEG_PATH,
                    "-i", inputFilePath.toAbsolutePath().toString(),
                    "-vn",
                    "-acodec", "pcm_s24le",
                    "-ar", "48000",
                    "-ac", "1",
                    "-y",
                    outputFilePath.toAbsolutePath().toString()
                                                );

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(5, TimeUnit.MINUTES);

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("FFmpeg exceeded timeout when extracting audio.");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String output = new String(process.getInputStream().readAllBytes());
                System.err.println("FFmpeg output: \n" + output);
                throw new RuntimeException("FFmpeg failed with exit code: " + exitCode + ". Output: " + output);
            }

            File outputFile = outputFilePath.toFile();
            if (!outputFile.exists() || !outputFile.canRead()) {
                throw new IOException("Output audio file not found or unreadable after processing: " + outputFile.getAbsolutePath());
            }
            return outputFile;

        } finally {
            try {
                Files.deleteIfExists(inputFilePath);
            } catch (IOException e) {
                System.err.println("Error deleting temporary input service file: " + inputFilePath + " - " + e.getMessage());
            }
        }
    }

    private File repairMp4(File uploadedFile) throws IOException {
        File fixedFile = File.createTempFile("fixed_", ".mp4");
        try {
            Movie movie = MovieCreator.build(uploadedFile.getAbsolutePath());
            DefaultMp4Builder builder = new DefaultMp4Builder();
            var container = builder.build(movie);
            try (FileOutputStream fos = new FileOutputStream(fixedFile);
                 FileChannel fc = fos.getChannel()) {
                container.writeContainer(fc);
            }
        } catch (Exception e) {
            throw new IOException("Failed to repair MP4", e);
        }
        return fixedFile;
    }

}