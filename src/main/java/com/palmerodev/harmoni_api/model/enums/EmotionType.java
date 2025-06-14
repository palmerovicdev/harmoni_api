package com.palmerodev.harmoni_api.model.enums;

public enum EmotionType {
    ANGRY,
    FEARFUL,
    HAPPY,
    SAD,
    NEUTRAL,
    DISGUSTED,
    SURPRISED,
    OTHER;

    public static EmotionType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or blank");
        }
        try {
            return EmotionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmotionType: " + type, e);
        }
    }
}
