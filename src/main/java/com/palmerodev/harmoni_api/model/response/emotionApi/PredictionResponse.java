package com.palmerodev.harmoni_api.model.response.emotionApi;

public record PredictionResponse(String label, double probability) {

    public PredictionResponse {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Label cannot be null or blank");
        }
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be between 0 and 1");
        }
    }

}
