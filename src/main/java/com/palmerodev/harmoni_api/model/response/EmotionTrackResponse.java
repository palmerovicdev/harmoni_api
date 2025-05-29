package com.palmerodev.harmoni_api.model.response;

import com.palmerodev.harmoni_api.model.enums.EmotionTrackType;
import com.palmerodev.harmoni_api.model.enums.EmotionType;

public record EmotionTrackResponse(
        Double percentage,
        EmotionTrackType trackType,
        EmotionType emotionType,
        String createdAt
) {
}
