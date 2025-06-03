package com.palmerodev.harmoni_api.model.request;

import java.util.List;

public record ActivityListRequest(
        List<ActivityRequest> activities
) {
}
