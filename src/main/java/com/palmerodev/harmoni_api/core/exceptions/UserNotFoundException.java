package com.palmerodev.harmoni_api.core.exceptions;

public class UserNotFoundException extends PrimaryException {

    public UserNotFoundException(String message, String jsonAdditionalData) {
        super(message, jsonAdditionalData);
    }

}
