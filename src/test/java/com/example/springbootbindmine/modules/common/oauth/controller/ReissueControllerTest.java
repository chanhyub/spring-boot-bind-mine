package com.example.springbootbindmine.modules.common.oauth.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.example.springbootbindmine.common.redis.service.RedisService;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ReissueControllerTest 테스트")
@WebMvcTest(ReissueController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public class ReissueControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    RedisService redisService;
    @MockitoBean
    JWTUtil jwtUtil;
    HttpServletRequest request;
    HttpServletResponse response;

    @BeforeEach
    void initialize() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
    }

    @DisplayName("요청 헤더의 쿠키에서 리프레시 토큰을 가져와 redis에 저장된 토큰과 비교하여 새로운 토큰을 발급한다.")
    @WithMockUser
    @Test
    void reissue() throws Exception {
        // given
        UserEntity userEntity = UserEntity.builder()
                .name("테스트 유저")
                .email("test@test.com")
                .imageFileLink("http://localhost:8080/test.jpg")
                .userName("kakao_123456")
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .build();

        Cookie requestCookie = new Cookie("refreshToken", "existRefreshToken");
        String redisRefreshToken = "refreshToken";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        // when
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{requestCookie});
        Mockito.when(jwtUtil.isExpired("existRefreshToken")).thenReturn(false);
        Mockito.when(jwtUtil.getCategory("existRefreshToken")).thenReturn("refresh");
        Mockito.when(jwtUtil.getUsername("existRefreshToken")).thenReturn(userEntity.getUserName());
        Mockito.when(jwtUtil.getRole("existRefreshToken")).thenReturn(userEntity.getRole().name());
        Mockito.when(redisService.getValues("refreshToken")).thenReturn(redisRefreshToken);
        Mockito.when(redisService.checkExistsValue(redisRefreshToken)).thenReturn(false);
        Mockito.when(jwtUtil.createJwt("access", userEntity.getUserName(), userEntity.getRole().name(), 600000L)).thenReturn(newAccessToken);
        Mockito.when(jwtUtil.createJwt("refresh", userEntity.getUserName(), userEntity.getRole().name(), 259200000L)).thenReturn(newRefreshToken);

        Cookie responseCookie = new Cookie("refreshToken", newRefreshToken);
        responseCookie.setMaxAge(3*24*60*60);
        responseCookie.setHttpOnly(true);
        response.addCookie(responseCookie);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/reissue")
                        .cookie(requestCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("reissue",
                                resourceDetails().description("JWT 토큰 재발급"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                .description("Access 토큰")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("refreshToken", newRefreshToken))
                .andExpect(jsonPath("$.accessToken").value(newAccessToken));

    }

}
