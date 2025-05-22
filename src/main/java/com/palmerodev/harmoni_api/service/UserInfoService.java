package com.palmerodev.harmoni_api.service;


import com.palmerodev.harmoni_api.model.entity.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserInfoService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    String addUser(UserInfo userInfo);

}