package com.palmerodev.harmoni_api.model.response;

public record SettingsResponse(
        String status,
        String settings,
        String message
) {

    public SettingsResponse(String status, String message) {
        this(status, null, message);
    }

    public SettingsResponse(String status) {
        this(status, null, null);
    }

}
