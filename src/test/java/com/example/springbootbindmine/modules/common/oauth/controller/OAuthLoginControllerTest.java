package com.example.springbootbindmine.modules.common.oauth.controller;

import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.restdocs.RestDocsBasicTest;
import com.example.springbootbindmine.modules.common.oauth.dto.OAuthLoginDTO;
import com.example.springbootbindmine.modules.common.oauth.request.OAuthLoginRequest;
import com.example.springbootbindmine.modules.common.oauth.response.OAuthLoginResponse;
import com.example.springbootbindmine.modules.common.oauth.service.OAuthLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("OAuthLoginController 테스트")
@WebMvcTest(OAuthLoginController.class)
public class OAuthLoginControllerTest extends RestDocsBasicTest {
    @MockitoBean
    OAuthLoginService oAuthLoginService;
    @MockitoBean
    RedisService redisService;
    HttpServletResponse response;

    @BeforeEach
    void initialize() {
        response = Mockito.mock(HttpServletResponse.class);
    }

    @DisplayName("카카오 OAuth 로그인을 시도한다.")
    @Test
    @WithMockUser
    void login() throws Exception {
        // given
        OAuthLoginRequest oAuthLoginRequest = new OAuthLoginRequest("kakao access token", "kakao");
        OAuthLoginDTO oAuthLoginDTO = OAuthLoginDTO.builder()
                .accessToken("access token")
                .refreshToken("refresh token")
                .userName("username")
                .build();
        OAuthLoginResponse oAuthLoginResponse = OAuthLoginResponse.builder()
                .accessToken(oAuthLoginDTO.accessToken())
                .build();

        // when
        Mockito.when(oAuthLoginService.login(oAuthLoginRequest)).thenReturn(oAuthLoginDTO);

        Mockito.doNothing().when(redisService).setValues(oAuthLoginDTO.userName(), oAuthLoginDTO.refreshToken(), Duration.ofMillis(259200000L));

        Cookie cookie = new Cookie("refreshToken", oAuthLoginDTO.refreshToken());
        cookie.setMaxAge(3*24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/oauth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oAuthLoginRequest))
                        .with(csrf()))
                .andDo(restDocs.document(
                                requestFields(
                                        fieldWithPath("provider").type(JsonFieldType.STRING)
                                                .description("oauth 로그인 타입(kakao, google)"),
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                .description("provider에서 발급받은 accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                .description("Access 토큰")
                                ),
                                responseCookies(
                                        cookieWithName("refreshToken").description("Refresh 토큰")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("refreshToken", oAuthLoginDTO.refreshToken()))
                .andExpect(jsonPath("$.accessToken").value(oAuthLoginResponse.accessToken()));
    }
}
