package com.palmerodev.harmoni_api.controller;

import com.palmerodev.harmoni_api.model.request.ActivityListRequest;
import com.palmerodev.harmoni_api.model.request.EmotionTrackMultipleRequest;
import com.palmerodev.harmoni_api.model.response.ActivityResponse;
import com.palmerodev.harmoni_api.model.response.EmotionTrackResponse;
import com.palmerodev.harmoni_api.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/activities")
    public ResponseEntity<ActivityResponse> activities() {
        return ResponseEntity.ok(homeService.getActivities());
    }

    @PostMapping("/createActivity")
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityListRequest request) {
        return ResponseEntity.ok(homeService.createActivities(request));
    }

    @DeleteMapping("/deleteActivity/{id}")
    public ResponseEntity<ActivityResponse> deleteActivity(@PathVariable Long id) {
        return ResponseEntity.ok(homeService.deleteActivity(id));
    }

    @PostMapping("/trackEmotion")
    public ResponseEntity<EmotionTrackResponse> trackEmotion(@RequestBody EmotionTrackMultipleRequest request) {
        return ResponseEntity.ok(homeService.createEmotionTrack(request));
    }

    @GetMapping("/emotionsByActivity/{userId}/{activityId}")
    public ResponseEntity<List<EmotionTrackResponse>> emotionsByActivity(@PathVariable Long userId, @PathVariable Long activityId) {
        return ResponseEntity.ok(homeService.getEmotionTracksByActivity(userId, activityId));
    }

    @GetMapping("/emotions/{userId}")
    public ResponseEntity<List<EmotionTrackResponse>> emotions(@PathVariable Long userId) {
        return ResponseEntity.ok(homeService.getEmotionTracks(userId));
    }
}
