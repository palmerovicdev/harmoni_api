package com.palmerodev.harmoni_api.model.response.emotionApi;

import java.util.List;

public record EmotionRecordResponse(List<PredictionResponse> predictions, String success) {

}
