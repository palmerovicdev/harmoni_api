package com.palmerodev.harmoni_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthRequest(
        String name,
        String password
) {
}
