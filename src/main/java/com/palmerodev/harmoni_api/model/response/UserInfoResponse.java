package com.palmerodev.harmoni_api.model.response;

import com.palmerodev.harmoni_api.model.enums.Role;

public record UserInfoResponse(Long id,
                               String name,
                               String email,
                               Role role,
                               String gender) {
}
