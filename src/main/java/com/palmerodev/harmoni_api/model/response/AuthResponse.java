package com.palmerodev.harmoni_api.model.response;

public record AuthResponse(
        String status,
        String message,
        String token,
        Long id
) {

}
