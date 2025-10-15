package com.wallet.wallet_service.Client;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.wallet.wallet_service.dtos.UserInfo;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String authBaseUrl = "http://localhost:8081/api/users"; // AuthService URL

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserInfo getUserInfoById(Long userId) {
        String url = authBaseUrl + "/" + userId;
        return restTemplate.getForObject(url, UserInfo.class);
    }
}