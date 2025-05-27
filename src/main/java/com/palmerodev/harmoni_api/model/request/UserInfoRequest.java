package com.palmerodev.harmoni_api.model.request;

import com.palmerodev.harmoni_api.model.enums.Role;

public record UserInfoRequest(Integer id,
                              String name,
                              String email,
                              String password,
                              Role role) {

}
