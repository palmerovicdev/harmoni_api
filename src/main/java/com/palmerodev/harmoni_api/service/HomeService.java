package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.model.request.ActivityListRequest;
import com.palmerodev.harmoni_api.model.request.EmotionTrackMultipleRequest;
import com.palmerodev.harmoni_api.model.response.ActivityResponse;
import com.palmerodev.harmoni_api.model.response.EmotionTrackResponse;

import java.util.List;

public interface HomeService {

    ActivityResponse getActivities();

    ActivityResponse createActivities(ActivityListRequest activityRequest);

    ActivityResponse deleteActivity(Long activityId);

    EmotionTrackResponse createEmotionTrack(EmotionTrackMultipleRequest emotionTrackRequest);

    List<EmotionTrackResponse> getEmotionTracksByActivity(Long userId, Long activityId);

    List<EmotionTrackResponse> getEmotionTracks(Long userId);

}
