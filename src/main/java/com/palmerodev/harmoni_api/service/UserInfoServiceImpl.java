package com.palmerodev.harmoni_api.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmerodev.harmoni_api.core.exceptions.UserAlreadyExistException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import com.palmerodev.harmoni_api.model.request.AuthRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository repository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByName(username)
                         .orElseThrow(() -> new UserNotFoundException("User not found: " + username, ""));
    }

    @Override
    public String signUp(UserInfoRequest userInfo) {
        if (repository.findByName(userInfo.email()).isPresent()) {
            try {
                throw new UserAlreadyExistException("User already exists", objectMapper.writeValueAsString(userInfo));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        repository.save(UserInfo.builder()
                                .name(userInfo.name())
                                .email(userInfo.email())
                                .password(encoder.encode(userInfo.password()))
                                .role(userInfo.role())
                                .build());

        return this.login(new AuthRequest(userInfo.name(), userInfo.password()));
    }

    @Override
    public String login(AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
                                                               );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.username());
        } else {
            try {
                throw new UserNotFoundException("Invalid user request!", objectMapper.writeValueAsString(authRequest));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}