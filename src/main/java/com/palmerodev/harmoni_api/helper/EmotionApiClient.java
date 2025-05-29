package com.palmerodev.harmoni_api.helper;

import com.palmerodev.harmoni_api.model.response.emotionApi.EmotionRecordResponse;

import java.io.File;
import java.util.List;

public interface EmotionApiClient {

    List<EmotionRecordResponse> analyzeAudioBlocks(List<File> audioBlocks);

    void testAnalyzeAudioBlockFromFile();

}
