package com.linuxdo.cdkdistribution.service.impl;

import com.linuxdo.cdkdistribution.dto.UserDto;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.repository.UserRepository;
import com.linuxdo.cdkdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByLinuxDoId(String linuxDoId) {
        return userRepository.findByLinuxDoId(linuxDoId);
    }

    @Override
    @Transactional
    public User createOrUpdateUser(String linuxDoId, String username, String avatar, Integer trustLevel) {
        Optional<User> existingUser = userRepository.findByLinuxDoId(linuxDoId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(username);
            user.setAvatar(avatar);
            user.setTrustLevel(trustLevel);
            return userRepository.save(user);
        } else {
            User newUser = User.builder()
                    .linuxDoId(linuxDoId)
                    .username(username)
                    .avatar(avatar)
                    .trustLevel(trustLevel)
                    .build();
            return userRepository.save(newUser);
        }
    }

    @Override
    public UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .trustLevel(user.getTrustLevel())
                .build();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String linuxDoId = authentication.getName();
        return userRepository.findByLinuxDoId(linuxDoId).orElse(null);
    }

    @Override
    public boolean hasMinimumTrustLevel(Integer requiredLevel) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return currentUser.getTrustLevel() >= requiredLevel;
    }
}
