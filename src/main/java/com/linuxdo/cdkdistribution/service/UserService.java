package com.linuxdo.cdkdistribution.service;

import com.linuxdo.cdkdistribution.dto.UserDto;
import com.linuxdo.cdkdistribution.model.User;

import java.util.Optional;

public interface UserService {
    
    /**
     * Find a user by their Linux.do ID
     *
     * @param linuxDoId The Linux.do user ID
     * @return Optional containing the user if found
     */
    Optional<User> findByLinuxDoId(String linuxDoId);
    
    /**
     * Create or update a user based on OAuth2 user info
     *
     * @param linuxDoId The Linux.do user ID
     * @param username The username
     * @param avatar The avatar URL
     * @param trustLevel The trust level
     * @return The created or updated user
     */
    User createOrUpdateUser(String linuxDoId, String username, String avatar, Integer trustLevel);
    
    /**
     * Convert a User entity to UserDto
     *
     * @param user The user entity
     * @return The user DTO
     */
    UserDto convertToDto(User user);
    
    /**
     * Get the current authenticated user
     *
     * @return The current user
     */
    User getCurrentUser();
    
    /**
     * Check if the current user has sufficient trust level
     *
     * @param requiredLevel The required trust level
     * @return True if the user has sufficient trust level
     */
    boolean hasMinimumTrustLevel(Integer requiredLevel);
}
