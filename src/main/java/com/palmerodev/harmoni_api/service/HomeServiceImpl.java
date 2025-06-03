package com.palmerodev.harmoni_api.service;

import com.palmerodev.harmoni_api.core.exceptions.ActivityNotFoundException;
import com.palmerodev.harmoni_api.core.exceptions.SegmentationException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.helper.EmotionApiClient;
import com.palmerodev.harmoni_api.helper.VideoSegmenter;
import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import com.palmerodev.harmoni_api.model.enums.EmotionTrackType;
import com.palmerodev.harmoni_api.model.enums.EmotionType;
import com.palmerodev.harmoni_api.model.request.ActivityListRequest;
import com.palmerodev.harmoni_api.model.request.EmotionTrackMultipleRequest;
import com.palmerodev.harmoni_api.model.response.ActivityResponse;
import com.palmerodev.harmoni_api.model.response.EmotionTrackResponse;
import com.palmerodev.harmoni_api.repository.ActivityEntityRepository;
import com.palmerodev.harmoni_api.repository.EmotionTrackEntityRepository;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ActivityEntityRepository activityEntityRepository;
    private final EmotionTrackEntityRepository emotionTrackEntityRepository;
    private final UserInfoRepository userInfoRepository;
    private final VideoSegmenter videoSegmenter;
    private final EmotionApiClient emotionApiClient;
    private final JwtService jwtService;

    @Override
    public ActivityResponse getActivities() {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));
        return new ActivityResponse("success", activityEntityRepository.findAllByUserInfo(userInfo));
    }

    @Override
    public ActivityResponse createActivities(ActivityListRequest activityRequest) {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

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
                                                 userInfo))
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

        var userInfo = userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));

        var activity = activityEntityRepository.findById(emotionTrackRequest.activityId())
                                               .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));

        List<File> audioBlocks;
        try {
            audioBlocks = videoSegmenter.extractAudioBlocks(emotionTrackRequest.videoFile());
        } catch (Exception e) {
            throw new SegmentationException("Error extracting audio from video");
        }

        var emotionRecords = emotionApiClient.analyzeAudioBlocks(audioBlocks);

        String label = "";
        double maxProbability = 0.0;
        for (var emotion : emotionRecords) {
            for (var prediction : emotion.predictions()) {
                if (prediction.probability() > maxProbability) {
                    maxProbability = prediction.probability();
                    label = prediction.label();
                }
            }
        }


        var emotionTrack = emotionTrackEntityRepository.saveAndFlush(
                new com.palmerodev.harmoni_api.model.entity.EmotionTrackEntity(
                        null,
                        maxProbability,
                        EmotionTrackType.VOICE_AND_IMAGES,
                        EmotionType.fromString(label),
                        userInfo,
                        activity,
                        null,
                        null
                )
                                                                    );

        var finalEmotionTrack = emotionTrackEntityRepository.saveAndFlush(emotionTrack);

        return new EmotionTrackResponse(
                maxProbability,
                EmotionTrackType.VOICE_AND_IMAGES,
                EmotionType.fromString(label),
                finalEmotionTrack.getCreatedAt().toString()
        );
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracksByActivity(Long activityId) {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));


        var activity = activityEntityRepository.findById(activityId)
                                               .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));

        return emotionTrackEntityRepository.findByUserInfoAndActivity(userInfo, activity).stream()
                                           .map(emotionTrack -> new EmotionTrackResponse(
                                                   emotionTrack.getPercentage(),
                                                   emotionTrack.getEmotionTrackType(),
                                                   emotionTrack.getEmotionType(),
                                                   emotionTrack.getCreatedAt().toString()))
                                           .toList();
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracks() {
        var userInfo = userInfoRepository.findByName(jwtService.extractUsername()).orElseThrow(() -> new UserNotFoundException("User not found", "Username: " + jwtService.extractUsername()));


        return emotionTrackEntityRepository.findByUserInfo(userInfo)
                                           .stream()
                                           .map(emotionTrack -> new EmotionTrackResponse(
                                                   emotionTrack.getPercentage(),
                                                   emotionTrack.getEmotionTrackType(),
                                                   emotionTrack.getEmotionType(),
                                                   emotionTrack.getCreatedAt().toString()))
                                           .toList();
    }

}
