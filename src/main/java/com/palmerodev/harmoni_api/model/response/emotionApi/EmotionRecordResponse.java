package com.palmerodev.harmoni_api.model.response.emotionApi;

import java.util.List;

public record EmotionRecordResponse(List<PredictionResponse> predictions, String success) {

    public EmotionRecordResponse(List<PredictionResponse> predictions) {
        this(predictions, "success");
    }

    public EmotionRecordResponse(String success) {
        this(List.of(), success);
    }

}
