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
        var response = homeService.getActivities();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createActivity")
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityListRequest request) {
        var response = homeService.createActivities(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteActivity/{id}")
    public ResponseEntity<ActivityResponse> deleteActivity(@PathVariable Long id) {
        var response = homeService.deleteActivity(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/trackEmotion", consumes = {"multipart/form-data", "multipart/form-data;charset=UTF-8"})
    public ResponseEntity<EmotionTrackResponse> trackEmotion(@ModelAttribute EmotionTrackMultipleRequest request) {
        var response = homeService.createEmotionTrack(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emotionsByActivity/{activityId}")
    public ResponseEntity<List<EmotionTrackResponse>> emotionsByActivity(@PathVariable Long activityId) {
        var response = homeService.getEmotionTracksByActivity(activityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emotions")
    public ResponseEntity<List<EmotionTrackResponse>> emotions() {
        var response = homeService.getEmotionTracks();
        return ResponseEntity.ok(response);
    }

}
