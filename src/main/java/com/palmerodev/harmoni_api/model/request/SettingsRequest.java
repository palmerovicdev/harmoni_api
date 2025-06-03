package com.palmerodev.harmoni_api.model.request;

import java.util.Map;

public record SettingsRequest(
        Map<String, Object> settings
) {
}
