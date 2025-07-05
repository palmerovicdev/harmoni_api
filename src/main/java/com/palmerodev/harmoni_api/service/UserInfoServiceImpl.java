package com.palmerodev.harmoni_api.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmerodev.harmoni_api.core.exceptions.AuthLogicException;
import com.palmerodev.harmoni_api.core.exceptions.UserAlreadyExistException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import com.palmerodev.harmoni_api.model.request.ActivityListRequest;
import com.palmerodev.harmoni_api.model.request.ActivityRequest;
import com.palmerodev.harmoni_api.model.request.AuthRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.AuthResponse;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository repository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper;

    private final HomeService homeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByName(username)
                         .orElseThrow(() -> new UserNotFoundException("User not found: " + username, ""));
    }

    @Override
    public AuthResponse signUp(UserInfoRequest userInfo) {
        if (repository.findByName(userInfo.email()).isPresent()) {
            try {
                throw new UserAlreadyExistException("User already exists", objectMapper.writeValueAsString(userInfo));
            } catch (JsonProcessingException e) {
                throw new AuthLogicException("Error processing user info", e.toString());
            }
        }

        UserInfo savedUser = repository.save(UserInfo.builder()
                                .name(userInfo.name())
                                .email(userInfo.email())
                                .gender(userInfo.gender())
                                .age(userInfo.age())
                                .avatar(userInfo.avatar())
                                .password(encoder.encode(userInfo.password()))
                                .role(userInfo.role())
                                .build());
        
        // Crear 5 actividades por defecto para el nuevo usuario
        createDefaultActivities(savedUser);

        return this.login(new AuthRequest(userInfo.name(), userInfo.password(), userInfo.email()));
    }

    /**
     * Crea 5 actividades por defecto para un nuevo usuario
     * @param user El usuario para el cual crear las actividades
     */
    private void createDefaultActivities(UserInfo user) {
        var defaultActivities = List.of(
            new ActivityRequest("Trabajo", "#FF6B6B"),
            new ActivityRequest("Estudio", "#4ECDC4"),
            new ActivityRequest("Ejercicio", "#45B7D1"),
            new ActivityRequest("Social", "#96CEB4"),
            new ActivityRequest("Descanso", "#FFEAA7")
        );
        
        var activityListRequest = new ActivityListRequest(defaultActivities);
        homeService.createActivities(activityListRequest);
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        var user = repository.findByEmail(authRequest.email())
                             .orElseThrow(() -> new UserNotFoundException("User not found: " + authRequest.name(), ""));
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getName(), authRequest.password())
                                                               );

        } catch (BadCredentialsException e) {
            return  new AuthResponse(
                    "error",
                    "Invalid credentials",
                    null,
                    null
            );
        }

        if (authentication.isAuthenticated()) {
            return new AuthResponse(
                    "success",
                    "User authenticated successfully",
                    jwtService.generateToken(user.getName()),
                    repository.findByName(user.getName())
                              .orElseThrow(() -> new UserNotFoundException("User not found: " + authRequest.name(), ""))
                              .getId()
            );
        } else {
            try {
                throw new UserNotFoundException("Invalid user request!", objectMapper.writeValueAsString(authRequest));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}