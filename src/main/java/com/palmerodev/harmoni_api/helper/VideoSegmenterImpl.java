package com.palmerodev.harmoni_api.helper;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoSegmenterImpl implements VideoSegmenter {

    @Override
    public List<BufferedImage> extractFrames(MultipartFile file) throws Exception {
        List<BufferedImage> frames = new ArrayList<>();

        File tempFile = convertToTempFile(file);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile)) {
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
        tempFile.delete();

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
    public List<byte[]> extractAudioBlocks(MultipartFile file) throws Exception {
        List<byte[]> audioBlocks = new ArrayList<>();
        File tempFile = convertToTempFile(file);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile)) {
            grabber.start();

            int sampleRate = grabber.getSampleRate();
            int channels = grabber.getAudioChannels();
            int samplesPer2Sec = sampleRate * 2 * channels;

            ByteArrayOutputStream currentBlock = new ByteArrayOutputStream();
            int samplesInBlock = 0;

            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                if (frame.samples != null) {
                    ShortBuffer audioBuffer = (ShortBuffer) frame.samples[0];
                    while (audioBuffer.hasRemaining()) {
                        short sample = audioBuffer.get();
                        currentBlock.write(sample & 0xff);
                        currentBlock.write((sample >> 8) & 0xff);
                        samplesInBlock++;
                        if (samplesInBlock == samplesPer2Sec) {
                            audioBlocks.add(currentBlock.toByteArray());
                            currentBlock.reset();
                            samplesInBlock = 0;
                        }
                    }
                }
            }
            if (currentBlock.size() > 0) {
                audioBlocks.add(currentBlock.toByteArray());
            }

            grabber.stop();
        } finally {
            tempFile.delete();
        }

        return audioBlocks;
    }

}