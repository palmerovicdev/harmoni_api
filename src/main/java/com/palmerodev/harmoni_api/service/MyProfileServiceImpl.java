package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.core.exceptions.SettingsNotFoundException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.entity.SettingsEntity;
import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.Response;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.model.response.UserInfoResponse;
import com.palmerodev.harmoni_api.model.response.ValidationResponse;
import com.palmerodev.harmoni_api.repository.SettingsEntityRepository;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
                                       .map(settingsEntity -> new SettingsResponse("success", settingsEntity.getSettingsJson().toString(), "Settings retrieved successfully"))
                                       .orElseThrow(() -> new SettingsNotFoundException("Settings not found for user ID: " + userInfo.getId()));
    }

    @Override
    public SettingsResponse saveSettingsForUser(SettingsRequest request) {

        var userInfo = userInfoRepository.findByName(jwtService.extractUsername())
                                         .orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        return settingsEntityRepository.findById(userInfo.getId())
                                       .map(settingsEntity -> {
                                           settingsEntity.setSettingsJson(request.settings());
                                           settingsEntityRepository.save(settingsEntity);
                                           return new SettingsResponse("success", request.settings().toString(), "Settings updated successfully");
                                       })
                                       .orElseGet(() -> {
                                           var settingsEntity = new SettingsEntity(null, request.settings(), userInfo, null, null);
                                           settingsEntityRepository.save(settingsEntity);
                                           return new SettingsResponse("success", request.settings().toString(), "Settings updated successfully");
                                       });

    }

    @Override
    public UserInfoResponse getUserProfile() {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername())
                                         .orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        return new UserInfoResponse(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getRole(),
                userInfo.getAge(),
                userInfo.getAvatar(),
                userInfo.getGender());
    }

    @Override
    public ValidationResponse validateEmail(String email) {
        return new ValidationResponse(!userInfoRepository.existsByEmail(email));
    }

    @Override
    public ValidationResponse validateName(String name) {
        return new ValidationResponse(!userInfoRepository.existsByName(name));
    }

    @Override
    public Response updateUserProfile(UserInfoRequest userInfo) {
        var existingUser = userInfoRepository.findById(userInfo.id())
                                             .orElseThrow(() -> new UserNotFoundException("User not found", "ID: " + userInfo.id()));

        existingUser.setName(userInfo.name());
        existingUser.setEmail(userInfo.email());
        existingUser.setGender(userInfo.gender());
        existingUser.setAge(userInfo.age());
        existingUser.setAvatar(userInfo.avatar());
        if (userInfo.password() != null && !userInfo.password().isEmpty()) {
            existingUser.setPassword(userInfo.password());
        }
        existingUser.setRole(userInfo.role());
        userInfoRepository.save(existingUser);
        return new Response<Map<String, Object>>(
                200, "User profile updated successfully", new HashMap<>(
                Map.of(
                        "token", jwtService.generateToken(existingUser.getName()),
                        "user", new UserInfoResponse(
                                existingUser.getId(),
                                existingUser.getName(),
                                existingUser.getEmail(),
                                existingUser.getRole(),
                                existingUser.getAge(),
                                existingUser.getAvatar(),
                                existingUser.getGender())
                      )
        ));
    }

    @Override
    public boolean deleteUserProfile() {
        userInfoRepository.delete(userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "")));
        return true;
    }

}
