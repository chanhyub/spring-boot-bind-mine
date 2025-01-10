package com.example.springbootbindmine.modules.common.oauth.service;

import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.common.security.dto.OAuthDTO;
import com.example.springbootbindmine.modules.common.oauth.response.OAuthLoginResponse;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JWTUtil jwtUtil;

    public OAuthLoginService(
            UserRepository userRepository,
            RedisService redisService,
            JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ResponseEntity<?> login(OAuthDTO oAuthDTO) {
        String username = oAuthDTO.provider() + "_" + oAuthDTO.providerId();
        Optional<UserEntity> optionalUser = userRepository.findByUserName(username);

        if (optionalUser.isEmpty()) {
            UserEntity newUserEntity = UserEntity.builder()
                    .name(oAuthDTO.name())
                    .email(oAuthDTO.email())
                    .imageFileLink(oAuthDTO.profileImage())
                    .userName(username)
                    .role(Role.USER)
                    .createDate(LocalDateTime.now())
                    .build();

            userRepository.save(newUserEntity);

            return getResponseEntity(newUserEntity);
        }
        else{
            return getResponseEntity(optionalUser.get());
        }

    }

    private ResponseEntity<?> getResponseEntity(UserEntity userEntity) {
        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt("access", userEntity.getUserName(), userEntity.getRole().name(), 60000L);
        String refreshToken = jwtUtil.createJwt("refresh", userEntity.getUserName(), userEntity.getRole().name(), 259200000L);

        // redis에 insert (key = username / value = refreshToken)
        redisService.setValues(userEntity.getUserName(), refreshToken, Duration.ofMillis(259200000L));

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3*24*60*60)
                //TODO : 배포 시 변경
                .domain("localhost")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(
                        OAuthLoginResponse.builder()
                                .accessToken(accessToken)
                                .build()
                );
    }
}
