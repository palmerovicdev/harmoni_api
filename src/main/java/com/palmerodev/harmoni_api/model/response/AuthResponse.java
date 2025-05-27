package com.palmerodev.harmoni_api.model.response;

public record AuthResponse(String status, String message, String token) {

    public AuthResponse(String status, String message) {
        this(status, message, null);
    }

    public AuthResponse(String status) {
        this(status, null, null);
    }

}
