package com.example.springbootbindmine.modules.common.oauth.controller;

import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.common.oauth.service.ReissueService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final ReissueService reissueService;

    public ReissueController(
            JWTUtil jwtUtil,
            RedisService redisService,
            ReissueService reissueService
    ) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.reissueService = reissueService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) {
        return reissueService.reissue(request);
    }

}
