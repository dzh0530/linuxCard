package com.linuxdo.cdkdistribution.controller;

import com.linuxdo.cdkdistribution.dto.ApiResponse;
import com.linuxdo.cdkdistribution.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<String>> login() {
        String authUrl = authService.getAuthorizationUrl();
        return ResponseEntity.ok(ApiResponse.success("Authorization URL generated", authUrl));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        try {
            String token = authService.handleOAuthCallback(code);
            
            // Set JWT token as cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1 day
            response.addCookie(cookie);
            
            // Redirect to home page
            response.sendRedirect("/");
        } catch (Exception e) {
            // Redirect to error page with message
            response.sendRedirect("/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        // Clear the token cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
