package com.palmerodev.harmoni_api.model.response;

public record ErrorResponse(String status, String message) {

    public ErrorResponse(String status) {
        this(status, null);
    }

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
