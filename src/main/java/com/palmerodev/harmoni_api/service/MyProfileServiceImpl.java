package com.palmerodev.harmoni_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmerodev.harmoni_api.core.exceptions.SettingsNotFoundException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.repository.SettingsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyProfileServiceImpl implements MyProfileService {

    private final SettingsEntityRepository settingsEntityRepository;

    @Override
    public SettingsResponse getSettingsForUser(Long userId) {
        return settingsEntityRepository.findById(userId)
                                       .map(settingsEntity -> new SettingsResponse("success", settingsEntity.getSettingsJson(), "Settings retrieved successfully"))
                                       .orElseThrow(() -> new SettingsNotFoundException("Settings not found for user ID: " + userId));
    }

    @Override
    public SettingsResponse saveSettingsForUser(SettingsRequest request) {
        String settingsJson;
        try {
            settingsJson = new ObjectMapper().writeValueAsString(request.settings());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return settingsEntityRepository.findById(request.userId())
                                       .map(settingsEntity -> {
                                           settingsEntity.setSettingsJson(settingsJson);
                                           settingsEntityRepository.save(settingsEntity);
                                           return new SettingsResponse("success", settingsJson, "Settings updated successfully");
                                       })
                                       .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.userId(), settingsJson));
    }

}
