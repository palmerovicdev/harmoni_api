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

import java.awt.image.BufferedImage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ActivityEntityRepository activityEntityRepository;
    private final EmotionTrackEntityRepository emotionTrackEntityRepository;
    private final UserInfoRepository userInfoRepository;
    private final VideoSegmenter videoSegmenter;
    private final EmotionApiClient emotionApiClient;

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

        var user = userInfoRepository.findById(emotionTrackRequest.userId())
                                     .orElseThrow(() -> new UserNotFoundException("User not found", "" + emotionTrackRequest.userId()));
        var activity = activityEntityRepository.findById(emotionTrackRequest.activityId())
                                               .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));


        List<BufferedImage> frames;
        try {
            frames = videoSegmenter.extractFrames(emotionTrackRequest.videoFile());
        } catch (Exception e) {
            throw new SegmentationException("Error extracting frames from video");
        }

        List<byte[]> audioBlocks;
        try {
            audioBlocks = videoSegmenter.extractAudioBlocks(emotionTrackRequest.videoFile());
        } catch (Exception e) {
            throw new SegmentationException("Error extracting audio from video");
        }

        if (frames.isEmpty() || audioBlocks.isEmpty()) {
            throw new SegmentationException("No frames or audio blocks extracted from video");
        }

        // Call endpoint to process frames

        // If we have more than 1 audio then we need to call the endpoint for each audio block and select the result with the highest percentage in the data returned
        var emotionRecords = emotionApiClient.analyzeAudioBlocks(audioBlocks);
        for (var record : emotionRecords) {
            if (record.predictions().isEmpty()) {
                throw new ActivityNotFoundException("No predictions returned from emotion API");
            }
        }

        // select the prediction with the highest percentage
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

        //TODO 5/30/25 palmerodev : add the code to get the label and the max probability from the emotionImages and compare it with the audio blocks ones

        var emotionTrack = emotionTrackEntityRepository.saveAndFlush(
                new com.palmerodev.harmoni_api.model.entity.EmotionTrackEntity(
                        null,
                        maxProbability,
                        EmotionTrackType.VOICE_AND_IMAGES,
                        EmotionType.fromString(label),
                        user,
                        activity,
                        null,
                        null
                )
                                                                    );

        // Save the emotion track to the database
        var finalEmotionTrack = emotionTrackEntityRepository.saveAndFlush(emotionTrack);

        return new EmotionTrackResponse(
                maxProbability,
                EmotionTrackType.VOICE_AND_IMAGES,
                EmotionType.fromString(label),
                finalEmotionTrack.getCreatedAt().toString()
        );
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracksByActivity(Long userId, Long activityId) {
        var user = userInfoRepository.findById(userId)
                                     .orElseThrow(() -> new UserNotFoundException("User not found", "" + userId));
        var activity = activityEntityRepository.findById(activityId)
                                               .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));
        return emotionTrackEntityRepository.findByUserInfoAndActivity(user, activity).stream()
                                           .map(emotionTrack -> new EmotionTrackResponse(
                                                   emotionTrack.getPercentage(),
                                                   emotionTrack.getEmotionTrackType(),
                                                   emotionTrack.getEmotionType(),
                                                   emotionTrack.getCreatedAt().toString()))
                                           .toList();
    }

    @Override
    public List<EmotionTrackResponse> getEmotionTracks(Long userId) {
        var user = userInfoRepository.findById(userId)
                                     .orElseThrow(() -> new UserNotFoundException("User not found", "" + userId));
        return emotionTrackEntityRepository.findByUserInfo(user)
                                           .stream()
                                           .map(emotionTrack -> new EmotionTrackResponse(
                                                   emotionTrack.getPercentage(),
                                                   emotionTrack.getEmotionTrackType(),
                                                   emotionTrack.getEmotionType(),
                                                   emotionTrack.getCreatedAt().toString()))
                                           .toList();
    }

}
