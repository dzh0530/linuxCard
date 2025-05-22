package com.linuxdo.cdkdistribution.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.security.JwtTokenProvider;
import com.linuxdo.cdkdistribution.service.AuthService;
import com.linuxdo.cdkdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${oauth2.linux-do.client-id}")
    private String clientId;

    @Value("${oauth2.linux-do.client-secret}")
    private String clientSecret;

    @Value("${oauth2.linux-do.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.linux-do.authorize-url}")
    private String authorizeUrl;

    @Value("${oauth2.linux-do.token-url}")
    private String tokenUrl;

    @Value("${oauth2.linux-do.user-info-url}")
    private String userInfoUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            UserService userService,
            JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String handleOAuthCallback(String code) throws Exception {
        // 1. Exchange authorization code for access token
        String accessToken = getAccessToken(code);
        
        // 2. Get user info using the access token
        JsonNode userInfo = getUserInfo(accessToken);
        
        // 3. Extract user details
        String linuxDoId = userInfo.get("sub").asText();
        String username = userInfo.get("preferred_username").asText();
        String avatar = userInfo.has("picture") ? userInfo.get("picture").asText() : null;
        Integer trustLevel = userInfo.has("trust_level") ? userInfo.get("trust_level").asInt() : 1;
        
        // 4. Create or update user
        User user = userService.createOrUpdateUser(linuxDoId, username, avatar, trustLevel);
        
        // 5. Generate JWT token
        return jwtTokenProvider.generateToken(user);
    }

    @Override
    public String getAuthorizationUrl() {
        return authorizeUrl + "?client_id=" + clientId + 
               "&scope=openid&response_type=code&redirect_uri=" + redirectUri;
    }

    private String getAccessToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        
        return root.get("access_token").asText();
    }

    private JsonNode getUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, String.class);
        
        return objectMapper.readTree(response.getBody());
    }
}
