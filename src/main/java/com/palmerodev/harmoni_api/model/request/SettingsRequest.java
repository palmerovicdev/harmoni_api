package com.palmerodev.harmoni_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SettingsRequest(
        Map<String, Object> settings
) {
}
