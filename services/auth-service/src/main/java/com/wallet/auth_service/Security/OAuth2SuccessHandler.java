package com.wallet.auth_service.Security;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.auth_service.dtos.AuthResponse;
import com.wallet.auth_service.model.User;
import com.wallet.auth_service.service.JwtService;
import com.wallet.auth_service.service.userservice;

import java.io.IOException; 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final userservice userservice;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
  

    public OAuth2SuccessHandler(@Lazy userservice userservice ,JwtService JwtService, ObjectMapper objectMapper) {
        this.userservice = userservice;
        this.jwtService = JwtService;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub"); // Google’s unique ID

        // Check if user exists or register new Google user
        User user = userservice.findByEmail(email)
                .orElseGet(() -> userservice.registerGoogleUser(email, name, googleId));

        // Generate JWT
        String token = jwtService.generateToken(user, user.getRoles());

        // Create AuthResponse DTO
        AuthResponse authResponse = new AuthResponse(token,user.getId(),user.getName(),user.getRoles());

        // Write DTO as JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }
}
