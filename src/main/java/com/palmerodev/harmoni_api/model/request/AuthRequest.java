package com.palmerodev.harmoni_api.model.request;

public record AuthRequest(
        String username,
        String password
) {
}
