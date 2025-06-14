package com.palmerodev.harmoni_api.core.exceptions;

public class SettingsNotFoundException extends PrimaryException {

    public SettingsNotFoundException(String message) {
        super(message, "");
    }

}
