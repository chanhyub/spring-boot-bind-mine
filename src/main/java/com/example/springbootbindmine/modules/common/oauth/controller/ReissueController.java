package com.example.springbootbindmine.modules.common.oauth.controller;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.exception.error.CommonErrorCode;
import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.common.oauth.response.OAuthLoginResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    public ReissueController(
            JWTUtil jwtUtil,
            RedisService redisService
    ) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
    }

    @PostMapping("/reissue")
    public OAuthLoginResponse reissue(HttpServletRequest request, HttpServletResponse response) {

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
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 유효기간 확인
        try {
            if(jwtUtil.isExpired(refresh)) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        } catch (ExpiredJwtException e) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 새로운 Token을 만들기 위해 준비
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // Redis내에 존재하는 refreshToken인지 확인
        String redisRefrshToken = redisService.getValues(username);
        if(redisService.checkExistsValue(redisRefrshToken)) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 새로운 JWT Token 생성
        String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 259200000L);

        // update refreshToken to Redis
        redisService.setValues(username, newRefreshToken, Duration.ofMillis(259200000L));

        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setMaxAge(3*24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        response.addCookie(cookie);

        return OAuthLoginResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

}
