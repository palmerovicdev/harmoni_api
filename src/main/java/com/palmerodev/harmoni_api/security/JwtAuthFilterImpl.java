package com.palmerodev.harmoni_api.security;

import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import com.palmerodev.harmoni_api.repository.UserInfoRepository;
import com.palmerodev.harmoni_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthFilterImpl extends JwtAuthFilter {

    private final JwtService jwtService;
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public JwtAuthFilterImpl(JwtService jwtService, UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        String token = null;
        String username;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername();
        } else {username = null;}

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userInfoRepository.findByName(username).orElseThrow(() -> new UserNotFoundException("User not found: " + username, ""));
            if (jwtService.validateToken(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}