package com.palmerodev.harmoni_api.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ActivityListRequest(
        List<ActivityRequest> activities
) {
}
