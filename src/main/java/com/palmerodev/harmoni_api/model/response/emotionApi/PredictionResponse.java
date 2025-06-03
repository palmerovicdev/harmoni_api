package com.palmerodev.harmoni_api.model.response.emotionApi;

public record PredictionResponse(String label, Double probability) {

    public PredictionResponse {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Label cannot be null or blank");
        }
    }

}
