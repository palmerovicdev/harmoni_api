package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.model.response.UserInfoResponse;
import com.palmerodev.harmoni_api.model.response.ValidationResponse;

public interface MyProfileService {

    SettingsResponse getSettingsForUser();

    SettingsResponse saveSettingsForUser(SettingsRequest request);

    UserInfoResponse getUserProfile();

    ValidationResponse validateEmail(String email);

    ValidationResponse validateName(String name);

    UserInfoResponse updateUserProfile(UserInfoRequest userInfo);

    boolean deleteUserProfile();

}
