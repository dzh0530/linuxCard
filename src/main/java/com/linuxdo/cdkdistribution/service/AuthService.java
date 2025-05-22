package com.linuxdo.cdkdistribution.service;

/**
 * Service for handling authentication with Linux.do OAuth2
 */
public interface AuthService {
    
    /**
     * Handle the OAuth2 callback from Linux.do
     *
     * @param code The authorization code
     * @return JWT token for the authenticated user
     * @throws Exception If authentication fails
     */
    String handleOAuthCallback(String code) throws Exception;
    
    /**
     * Generate the Linux.do OAuth2 authorization URL
     *
     * @return The authorization URL
     */
    String getAuthorizationUrl();
}
