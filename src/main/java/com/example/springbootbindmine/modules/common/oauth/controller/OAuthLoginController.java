package com.example.springbootbindmine.modules.common.oauth.controller;

import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.modules.common.oauth.dto.OAuthLoginDTO;
import com.example.springbootbindmine.modules.common.oauth.request.OAuthLoginRequest;
import com.example.springbootbindmine.modules.common.oauth.response.OAuthLoginResponse;
import com.example.springbootbindmine.modules.common.oauth.service.OAuthLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class OAuthLoginController {
    private final OAuthLoginService oAuthLoginService;
    private final RedisService redisService;

    public OAuthLoginController(
            OAuthLoginService oAuthLoginService,
            RedisService redisService
    )
    {
        this.oAuthLoginService = oAuthLoginService;
        this.redisService = redisService;
    }

    @PostMapping("/oauth/login")
    public OAuthLoginResponse login(@RequestBody OAuthLoginRequest oAuthLoginRequest, HttpServletResponse response) throws ParseException {

        OAuthLoginDTO oAuthLoginDTO = oAuthLoginService.login(oAuthLoginRequest);

        // redis에 insert (key = username / value = refreshToken)
        redisService.setValues(oAuthLoginDTO.userName(), oAuthLoginDTO.refreshToken(), Duration.ofMillis(259200000L));

        Cookie cookie = new Cookie("refreshToken", oAuthLoginDTO.refreshToken());
        cookie.setMaxAge(3*24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        response.addCookie(cookie);

        return OAuthLoginResponse.builder()
                .accessToken(oAuthLoginDTO.accessToken())
                .build();
    }

}
