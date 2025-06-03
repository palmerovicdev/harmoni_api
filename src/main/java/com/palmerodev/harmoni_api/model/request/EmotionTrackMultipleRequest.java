package com.palmerodev.harmoni_api.model.request;

import org.springframework.web.multipart.MultipartFile;

public record EmotionTrackMultipleRequest(MultipartFile videoFile, Long activityId) {

    public EmotionTrackMultipleRequest {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file cannot be null or empty");
        }
        if (activityId == null || activityId <= 0) {
            throw new IllegalArgumentException("Activity ID must be a positive number");
        }
    }
}
