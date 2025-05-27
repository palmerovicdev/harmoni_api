package com.palmerodev.harmoni_api.service;


import com.palmerodev.harmoni_api.model.request.AuthRequest;
import com.palmerodev.harmoni_api.model.request.UserInfoRequest;
import com.palmerodev.harmoni_api.model.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserInfoService extends UserDetailsService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    AuthResponse signUp(UserInfoRequest userInfo);

    AuthResponse login(AuthRequest authRequest);

}