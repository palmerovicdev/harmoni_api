package com.palmerodev.harmoni_api.model.response;

public record ActivityResponse(
        String status,
        String message,
        Object data
) {

    public ActivityResponse(String status, Object data) {
        this(status, null, data);
    }

}
