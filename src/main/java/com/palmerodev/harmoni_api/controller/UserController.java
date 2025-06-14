package com.palmerodev.harmoni_api.controller;

import com.palmerodev.harmoni_api.model.request.AuthRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.AuthResponse;
import com.palmerodev.harmoni_api.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService service;

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> signUp(@RequestBody UserInfoRequest userInfo) {
        return ResponseEntity.ok(service.signUp(userInfo));
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(service.login(authRequest));
    }

}