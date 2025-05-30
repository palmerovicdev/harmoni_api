package com.palmerodev.harmoni_api.helper;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public interface VideoSegmenter {
    List<BufferedImage> extractFrames(MultipartFile file) throws Exception;
    File convertToTempFile(MultipartFile multipartFile) throws Exception;
    List<byte[]> extractAudioBlocks(MultipartFile file) throws Exception;
}
