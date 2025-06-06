package com.palmerodev.harmoni_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmerodev.harmoni_api.core.exceptions.SettingsNotFoundException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.model.response.UserInfoResponse;
import com.palmerodev.harmoni_api.model.response.ValidationResponse;
import com.palmerodev.harmoni_api.repository.SettingsEntityRepository;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyProfileServiceImpl implements MyProfileService {

    private final SettingsEntityRepository settingsEntityRepository;
    private final UserInfoRepository userInfoRepository;
    private final JwtService jwtService;

    @Override
    public SettingsResponse getSettingsForUser() {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername())
                                         .orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        return settingsEntityRepository.findById(userInfo.getId())
                                       .map(settingsEntity -> new SettingsResponse("success", settingsEntity.getSettingsJson(), "Settings retrieved successfully"))
                                       .orElseThrow(() -> new SettingsNotFoundException("Settings not found for user ID: " + userInfo.getId()));
    }

    @Override
    public SettingsResponse saveSettingsForUser(SettingsRequest request) {
        String settingsJson;
        try {
            settingsJson = new ObjectMapper().writeValueAsString(request.settings());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername())
                                         .orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        return settingsEntityRepository.findById(userInfo.getId())
                                       .map(settingsEntity -> {
                                           settingsEntity.setSettingsJson(settingsJson);
                                           settingsEntityRepository.save(settingsEntity);
                                           return new SettingsResponse("success", settingsJson, "Settings updated successfully");
                                       })
                                       .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userInfo.getId(), settingsJson));
    }

    @Override
    public UserInfoResponse getUserProfile() {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername())
                                         .orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        return new UserInfoResponse(
                userInfo.getId(), userInfo.getName(), userInfo.getEmail(), userInfo.getRole(),
                userInfo.getGender());
    }

    @Override
    public ValidationResponse validateEmail(String email) {
        return new ValidationResponse(userInfoRepository.existsByEmail(email));
    }

    @Override
    public ValidationResponse validateName(String name) {
        return new ValidationResponse(userInfoRepository.existsByName(name));
    }

    @Override
    public UserInfoResponse updateUserProfile(UserInfoResponse userInfo) {
        var existingUser = userInfoRepository.findById(userInfo.id())
                                             .orElseThrow(() -> new UserNotFoundException("User not found", "ID: " + userInfo.id()));

        existingUser.setName(userInfo.name());
        existingUser.setEmail(userInfo.email());
        existingUser.setGender(userInfo.gender());
        existingUser.setRole(userInfo.role());
        userInfoRepository.save(existingUser);
        return new UserInfoResponse(
                existingUser.getId(), existingUser.getName(), existingUser.getEmail(),
                existingUser.getRole(), existingUser.getGender());
    }

}
