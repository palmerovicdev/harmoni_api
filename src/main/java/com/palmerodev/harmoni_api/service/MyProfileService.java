package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.model.response.UserInfoResponse;

public interface MyProfileService {

    SettingsResponse getSettingsForUser();

    SettingsResponse saveSettingsForUser(SettingsRequest request);

    UserInfoResponse getUserProfile();

}
