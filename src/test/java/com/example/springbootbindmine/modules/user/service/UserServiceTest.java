package com.example.springbootbindmine.modules.user.service;

import com.example.springbootbindmine.common.security.config.SecurityConfig;
import com.example.springbootbindmine.common.security.dto.CustomOAuthUser;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserService 테스트")
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인한 유저의 정보를 반환한다.")
    @Test
    @WithMockUser(username = "kakao_1", roles = "USER")
    void getUserByAuthentication() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // when
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserNameAndDeleteDateNull(authentication.getName());

        // then
        assertTrue(optionalUserEntity.isPresent());
        assertEquals(optionalUserEntity.get().getName(), "테스트 유저1");
        assertEquals(optionalUserEntity.get().getEmail(), "test1@test.com");
        assertEquals(optionalUserEntity.get().getImageFileLink(), "https://example.com/image1.jpg");
        assertEquals(optionalUserEntity.get().getUserName(), "kakao_1");
        assertEquals(optionalUserEntity.get().getRole(), Role.USER);
    }

    @DisplayName("로그인 한 유저의 정보가 없으면 빈 Optional을 반환한다.")
    @Test
    @WithMockUser(username = "userNotFound", roles = "USER")
    void getUserByAuthenticationOrNull() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // when
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserNameAndDeleteDateNull(authentication.getName());

        // then
        assertTrue(optionalUserEntity.isEmpty());
    }
}
