package com.palmerodev.harmoni_api.controller;

import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.service.MyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/myProfile")
@RequiredArgsConstructor
public class MyProfileController {

    private final MyProfileService myProfileService;

    @PostMapping("/saveSettings")
    public ResponseEntity<SettingsResponse> saveSettings(@RequestBody SettingsRequest request) {
        try {
            return ResponseEntity.ok(myProfileService.saveSettingsForUser(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new SettingsResponse("error", null, "Failed to save settings: " + e.getMessage())
                                                   );
        }
    }

    @GetMapping("/getSettingsForUser/{id}")
    public ResponseEntity<SettingsResponse> getSettingsForUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(myProfileService.getSettingsForUser(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
