package com.example.springbootbindmine.modules.common.oauth.service;

import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.common.oauth.response.OAuthLoginResponse;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    public ReissueService(
            JWTUtil jwtUtil,
            RedisService redisService
    ) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request) {

        // 쿠키에 존재하는 refreshToken을 가져오자
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if("refreshToken".equals(cookie.getName())) {
                refresh = cookie.getValue();
            }
        }

        // 검증 시작
        // refreshToken이 없는 경우
        if(refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 유효기간 확인
        try {
            if(jwtUtil.isExpired(refresh)) {
                return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 새로운 Token을 만들기 위해 준비
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // Redis내에 존재하는 refreshToken인지 확인
        String redisRefrshToken = redisService.getValues(username);
        if(redisService.checkExistsValue(redisRefrshToken)) {
            return new ResponseEntity<>("no exists in redis refresh token", HttpStatus.BAD_REQUEST);
        }

        // 새로운 JWT Token 생성
        String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 259200000L);

        // update refreshToken to Redis
        redisService.setValues(username, newRefreshToken, Duration.ofMillis(259200000L));

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",newRefreshToken)
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
                                .accessToken(newAccessToken)
                                .build()
                );
    }
}
