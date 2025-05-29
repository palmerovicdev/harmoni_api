package com.palmerodev.harmoni_api.model.request;

import java.util.Map;

public record SettingsRequest(
        Long userId,
        Map<String, Object> settings
) {
}
