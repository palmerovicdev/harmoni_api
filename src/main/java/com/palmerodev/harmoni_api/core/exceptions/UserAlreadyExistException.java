package com.palmerodev.harmoni_api.core.exceptions;

public class UserAlreadyExistException extends PrimaryException {

    public UserAlreadyExistException(String message, String jsonAdditionalData) {
        super(message, jsonAdditionalData);
    }

}
