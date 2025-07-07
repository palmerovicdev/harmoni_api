package com.palmerodev.harmoni_api.helper;

import com.palmerodev.harmoni_api.model.response.emotionApi.EmotionRecordResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
        this.baseUrl = baseUrl;
        this.restTemplate = createConfiguredRestTemplate();
    }

    private RestTemplate createConfiguredRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(300000);
        
        factory.setBufferRequestBody(false);
        
        return new RestTemplate(factory);
    }

    public void testAnalyzeAudioBlockFromFile() {
        File testFile = new File("/Volumes/TuMaletin/Victor/00-Tesis/emotion_recognition_API/test_data/Anger_C_A_abajo.wav");

        EmotionApiClientImpl client = new EmotionApiClientImpl("http://localhost:8000");

        try {
            List<EmotionRecordResponse> responses = client.analyzeAudioBlocks(List.of(testFile));

            responses.forEach(response -> System.out.println("Respuesta: " + response));
        } catch (Exception e) {
            System.err.println("Error en test: " + e.getMessage());
        }
    }

    @Override
    public List<EmotionRecordResponse> analyzeAudioBlocks(List<File> audioBlocks) {
        List<EmotionRecordResponse> emotionRecords = new ArrayList<>();

        for (File audio : audioBlocks) {
            try {
                if (!audio.exists()) {
                    throw new RuntimeException("El archivo no existe: " + audio.getAbsolutePath());
                }
                
                if (!audio.canRead()) {
                    throw new RuntimeException("No se puede leer el archivo: " + audio.getAbsolutePath());
                }

                long fileSizeInMB = audio.length() / (1024 * 1024);
                if (fileSizeInMB > 50) {
                    throw new RuntimeException("El archivo es muy grande (" + fileSizeInMB + "MB). Máximo 50MB permitido.");
                }

                System.out.println("Enviando archivo: " + audio.getName() + " (" + fileSizeInMB + "MB)");

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("audio", new FileSystemResource(audio));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                System.out.println("Enviando petición a: " + baseUrl + "/predict_audio");

                ResponseEntity<EmotionRecordResponse> response = restTemplate.postForEntity(
                        baseUrl + "/predict_audio",
                        requestEntity,
                        EmotionRecordResponse.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    emotionRecords.add(response.getBody());
                    System.out.println("Archivo procesado exitosamente: " + audio.getName());
                } else {
                    System.err.println("API devolvió estado no exitoso: " + response.getStatusCode());
                }
                
            } catch (org.springframework.web.client.ResourceAccessException e) {
                System.err.println("Error de conexión con la API:");
                System.err.println("- Verifica que el servidor esté corriendo en: " + baseUrl);
                System.err.println("- Verifica la conectividad de red");
                System.err.println("- Error: " + e.getMessage());
                throw new RuntimeException("No se pudo conectar con la API de emociones en " + baseUrl, e);
                
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                System.err.println("Error del cliente HTTP: " + e.getStatusCode());
                System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());
                throw new RuntimeException("Error del cliente al comunicarse con la API de emociones", e);
                
            } catch (org.springframework.web.client.HttpServerErrorException e) {
                System.err.println("Error del servidor: " + e.getStatusCode());
                System.err.println("Respuesta del servidor: " + e.getResponseBodyAsString());
                throw new RuntimeException("Error del servidor de la API de emociones", e);
                
            } catch (Exception e) {
                System.err.println("Error inesperado al procesar archivo: " + audio.getName());
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al analizar el archivo de audio: " + audio.getName(), e);
            }
        }
        
        return emotionRecords;
    }
}