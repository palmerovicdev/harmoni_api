package com.palmerodev.harmoni_api.service;


import com.palmerodev.harmoni_api.core.exceptions.UserAlreadyExistException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import com.palmerodev.harmoni_api.model.entity.UserInfoDetails;
import com.palmerodev.harmoni_api.model.request.AuthRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository repository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userDetail = repository.findByEmail(username);

        return userDetail.map(UserInfoDetails::new)
                         .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    public String signUp(UserInfoRequest userInfo) {
        var userInfoData = new UserInfo(
                Integer.parseInt(userInfo.getId()),
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getPassword(),
                userInfo.getRoles()
        );
        if (repository.findByEmail(userInfoData.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("User already exists");
        }
        userInfoData.setPassword(encoder.encode(userInfoData.getPassword()));
        repository.save(userInfoData);
        return this.login(new AuthRequest() {
            @Override
            public String getPassword() {
                return userInfo.getPassword();
            }

            @Override
            public String getUsername() {
                return userInfo.getEmail();
            }
        });
    }

    @Override
    public String login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
                                                                          );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UserNotFoundException("Invalid user request!");
        }
    }

}