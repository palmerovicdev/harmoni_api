package com.palmerodev.harmoni_api.core.exceptions;

public class PrimaryException extends RuntimeException {

    private final String jsonAdditionalData;

    public PrimaryException(String message, String jsonAdditionalData) {
        super(message);
        this.jsonAdditionalData = jsonAdditionalData;
    }


    public String getJsonAdditionalData() {
        return jsonAdditionalData;
    }

}
