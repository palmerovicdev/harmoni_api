package com.palmerodev.harmoni_api.helper;

import com.palmerodev.harmoni_api.model.response.emotionApi.EmotionRecordResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class EmotionApiClientImpl implements EmotionApiClient {

    private final WebClient webClient;

    public EmotionApiClientImpl(@Value("${emotion.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                                  .baseUrl(baseUrl)
                                  .build();
    }

    @Override
    public List<EmotionRecordResponse> analyzeAudioBlocks(List<byte[]> audioBlocks) {
        return Flux.fromIterable(audioBlocks)
                   .flatMap(audio -> {
                       MultipartBodyBuilder builder = new MultipartBodyBuilder();
                       builder.part(
                               "audio", new ByteArrayResource(audio) {
                                   @Override
                                   public String getFilename() {
                                       return "audio.wav";
                                   }
                               }).contentType(MediaType.APPLICATION_OCTET_STREAM);

                       return webClient.post()
                                       .uri("/predict_audio")
                                       .contentType(MediaType.MULTIPART_FORM_DATA)
                                       .bodyValue(builder.build())
                                       .retrieve()
                                       .bodyToMono(EmotionRecordResponse.class);
                   }).collectList().block();
    }

}