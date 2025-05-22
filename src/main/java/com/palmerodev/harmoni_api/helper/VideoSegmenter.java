package com.palmerodev.harmoni_api.helper;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoSegmenter {

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

        return frames;
    }

    private File convertToTempFile(MultipartFile multipartFile) throws Exception {
        File tempFile = File.createTempFile("uploaded", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    public List<byte[]> extractAudioBlocks(MultipartFile file) throws Exception {
        List<byte[]> audioBlocks = new ArrayList<>();

        File tempFile = convertToTempFile(file);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile)) {
            grabber.start();

            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                if (frame.samples != null) {
                    ShortBuffer audioBuffer = (ShortBuffer) frame.samples[0];
                    byte[] block = new byte[audioBuffer.remaining() * 2]; // 2 bytes por short
                    for (int i = 0; i < block.length / 2; i++) {
                        short sample = audioBuffer.get();
                        block[i * 2] = (byte) (sample & 0xff);
                        block[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
                    }
                    audioBlocks.add(block);
                }
            }

            grabber.stop();
        }

        tempFile.delete();
        return audioBlocks;
    }

}