package com.palmerodev.harmoni_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.palmerodev.harmoni_api.model.enums.Role;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserInfoRequest(
        Long id,
        String name,
        String email,
        String password,
        Integer age,
        String avatar,
        String settings,
        Role role,
        String gender) {

}
