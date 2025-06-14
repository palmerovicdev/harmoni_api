package com.palmerodev.harmoni_api.helper;

import com.palmerodev.harmoni_api.model.response.emotionApi.EmotionRecordResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmotionApiClientImpl implements EmotionApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public EmotionApiClientImpl(@Value("${emotion.api.base-url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public void testAnalyzeAudioBlockFromFile() {
        File testFile = new File("/Volumes/TuMaletin/Victor/00-Tesis/emotion_recognition_API/test_data/Anger_C_A_abajo.wav");

        EmotionApiClientImpl client = new EmotionApiClientImpl("http://localhost:8000");

        try {
            List<EmotionRecordResponse> responses = client.analyzeAudioBlocks(List.of(testFile));

            responses.forEach(response -> System.out.println("Respuesta: " + response));
        } catch (Exception e) {
        }
    }

    @Override
    public List<EmotionRecordResponse> analyzeAudioBlocks(List<File> audioBlocks) {
        List<EmotionRecordResponse> emotionRecords = new ArrayList<>(); // To store results

        for (File audio : audioBlocks) {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio", new FileSystemResource(audio));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<EmotionRecordResponse> response = restTemplate.postForEntity(
                        baseUrl + "/predict_audio",
                        requestEntity,
                        EmotionRecordResponse.class
                                                                                           );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    emotionRecords.add(response.getBody());
                } else {
                    System.err.println("API call returned non-success status: " + response.getStatusCode());
                }
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                System.err.println("Client Error: " + e.getStatusCode());
                System.err.println("Response Body: " + e.getResponseBodyAsString());
                throw e;
            } catch (org.springframework.web.client.HttpServerErrorException e) {
                System.err.println("Server Error: " + e.getStatusCode());
                System.err.println("Response Body: " + e.getResponseBodyAsString());
                throw e;
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to analyze audio block with emotion API", e);
            }
        }
        return emotionRecords;
    }

}