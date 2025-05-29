package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import com.palmerodev.harmoni_api.model.request.ActivityListRequest;
import com.palmerodev.harmoni_api.model.request.EmotionTrackMultipleRequest;
import com.palmerodev.harmoni_api.model.response.ActivityResponse;
import com.palmerodev.harmoni_api.model.response.EmotionTrackResponse;
import com.palmerodev.harmoni_api.repository.ActivityEntityRepository;
import com.palmerodev.harmoni_api.repository.EmotionTrackEntityRepository;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ActivityEntityRepository activityEntityRepository;
    private final EmotionTrackEntityRepository emotionTrackEntityRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public ActivityResponse getActivities() {
        return new ActivityResponse("success", activityEntityRepository.findAll());
    }

    @Override
    public ActivityResponse createActivities(ActivityListRequest activityRequest) {
        var user = userInfoRepository.findById(activityRequest.userId()).orElseThrow(() -> new UserNotFoundException("User not found", "" + activityRequest.userId()));
        activityEntityRepository
                .saveAllAndFlush(activityRequest
                                         .activities()
                                         .stream()
                                         .map(act -> new ActivityEntity(
                                                 null,
                                                 act.name(),
                                                 act.color(),
                                                 null,
                                                 null,
                                                 user))
                                         .toList());
        return new ActivityResponse(
                "success",
                null);
    }

    @Override
    public ActivityResponse deleteActivity(Long activityId) {
        activityEntityRepository.deleteById(activityId);
        return new ActivityResponse("success", null);
    }

    @Override
    public EmotionTrackResponse createEmotionTrack(EmotionTrackMultipleRequest emotionTrackRequest) {
        return null;
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracksByActivity(Long userId, Long activityId) {
        return List.of();
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracks(Long userId) {
        return List.of();
    }

}
