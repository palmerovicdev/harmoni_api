package com.palmerodev.harmoni_api.model.request;

import org.springframework.web.multipart.MultipartFile;

public record EmotionTrackMultipleRequest(MultipartFile videoFile, Long userId, Long activityId) {

    public EmotionTrackMultipleRequest {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file cannot be null or empty");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (activityId == null || activityId <= 0) {
            throw new IllegalArgumentException("Activity ID must be a positive number");
        }
    }
}
