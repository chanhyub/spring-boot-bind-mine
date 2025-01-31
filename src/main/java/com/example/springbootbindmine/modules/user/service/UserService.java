package com.example.springbootbindmine.modules.user.service;

import com.example.springbootbindmine.common.security.dto.CustomOAuthUser;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<UserEntity> getUserByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByUserName(authentication.getName());
    }

    private Optional<UserEntity> getUserByUserName(String id) {
        return userRepository.findByUserNameAndDeleteDateNull(id);
    }
}
