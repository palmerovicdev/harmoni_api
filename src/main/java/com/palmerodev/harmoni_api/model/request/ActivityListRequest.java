package com.palmerodev.harmoni_api.model.request;

import java.util.List;

public record ActivityListRequest(
        Long userId,
        List<ActivityRequest> activities
) {
}
