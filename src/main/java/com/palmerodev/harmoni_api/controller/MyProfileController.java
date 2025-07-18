package com.palmerodev.harmoni_api.controller;

import com.palmerodev.harmoni_api.model.request.SettingsRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.Response;
import com.palmerodev.harmoni_api.model.response.SettingsResponse;
import com.palmerodev.harmoni_api.model.response.UserInfoResponse;
import com.palmerodev.harmoni_api.model.response.ValidationResponse;
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
        return ResponseEntity.ok(myProfileService.saveSettingsForUser(request));
    }

    @GetMapping("/getSettingsForUser")
    public ResponseEntity<SettingsResponse> getSettingsForUser() {
        return ResponseEntity.ok(myProfileService.getSettingsForUser());
    }

    @GetMapping("/getUserProfile")
    public ResponseEntity<UserInfoResponse> getUserProfile() {
        return ResponseEntity.ok(myProfileService.getUserProfile());
    }

    @PostMapping("/update")
    public ResponseEntity<Response> updateUserProfile(@RequestBody UserInfoRequest userInfo) {
        return ResponseEntity.ok(myProfileService.updateUserProfile(userInfo));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteUserProfile() {
        return ResponseEntity.ok(myProfileService.deleteUserProfile());
    }

    @GetMapping("/validateEmail/{email}")
    public ResponseEntity<ValidationResponse> validateEmail(@PathVariable String email) {
        var isValid = myProfileService.validateEmail(email);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/validateName/{name}")
    public ResponseEntity<ValidationResponse> validateName(@PathVariable String name) {
        var isValid = myProfileService.validateName(name);
        return ResponseEntity.ok(isValid);
    }

}
