package com.palmerodev.harmoni_api.controller;

import com.palmerodev.harmoni_api.model.entity.SettingsEntity;
import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/myProfile")
@RequiredArgsConstructor
public class MyProfileController {

    @PostMapping("/saveSettings")
    public ResponseEntity<?> saveSettings(@RequestBody SettingsRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/getSettingsForUser/{id}")
    public ResponseEntity<SettingsEntity> getSettingsForUser(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
