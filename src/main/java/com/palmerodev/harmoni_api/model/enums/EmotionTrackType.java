package com.palmerodev.harmoni_api.model.enums;

public enum EmotionTrackType {
    VOICE,
    IMAGES,
    VOICE_AND_IMAGES;

    public static EmotionTrackType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or blank");
        }
        try {
            return EmotionTrackType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmotionTrackType: " + type, e);
        }
    }
}
