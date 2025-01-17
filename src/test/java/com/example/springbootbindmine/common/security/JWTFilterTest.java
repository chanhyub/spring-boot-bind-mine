package com.example.springbootbindmine.common.security;

import com.example.springbootbindmine.common.security.dto.CustomOAuthUser;
import com.example.springbootbindmine.common.security.filter.JWTFilter;
import com.example.springbootbindmine.modules.user.dto.UserDTO;
import com.example.springbootbindmine.modules.user.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JWT 필터 테스트")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JWTFilterTest {
    @Mock
    private JWTUtil jwtUtil;
    @InjectMocks
    private JWTFilter jwtFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void initialize() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
        jwtUtil = Mockito.mock(JWTUtil.class);
        jwtFilter = new JWTFilter(jwtUtil);
    }

    @DisplayName("요청 헤더에 AccessToken이 없는 경우 필터를 통과한다.")
    @Test
    void noAccessToken() throws ServletException, IOException {
        // when
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        // then
        Mockito.verify(filterChain, Mockito.atLeastOnce()).doFilter(request, response);
    }

    @DisplayName("요청 헤더에 token이 있지만 만료된 경우 response에 401 상태코드를 넣고 필터를 통과시키지 않는다.")
    @Test
    void expiredAccessToken() throws ServletException, IOException {
        // given
        String token = "access token expired";

        // when
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtUtil.isExpired(token)).thenReturn(true);
//        Mockito.when(jwtUtil.getCategory(accessToken)).thenReturn("access");

        jwtFilter.doFilter(request, response, filterChain);

        // then
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @DisplayName("요청 헤더에 RefereshToken이 있는 경우 response에 401 상태코드를 넣고 필터를 통과시키지 않는다.")
    @Test
    void tokenTypeIsNotAccess() throws ServletException, IOException {
        //given
        String token = "this token is refresh token";

        // when
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtUtil.isExpired(token)).thenReturn(false);
        Mockito.when(jwtUtil.getCategory(token)).thenReturn("refresh");


        jwtFilter.doFilter(request, response, filterChain);

        // then
        Mockito.verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @DisplayName("요청 헤더에 AccessToken이 유효한 경우, SecurityContextHolder에 authentication을 등록하고 필터를 통과시킨다.")
    @Test
    void validAccessToken() throws ServletException, IOException {
        // given
        String token = "this token is valid access token";
        UserDTO userDTO = UserDTO.builder()
                .userName("kakao_1234")
                .role(Role.USER)
                .build();

        // when
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtUtil.isExpired(token)).thenReturn(false);
        Mockito.when(jwtUtil.getCategory(token)).thenReturn("access");
        Mockito.when(jwtUtil.getUsername(token)).thenReturn(userDTO.userName());
        Mockito.when(jwtUtil.getRole(token)).thenReturn(userDTO.role().name());

        CustomOAuthUser customOAuthUser = new CustomOAuthUser(userDTO);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuthUser, null, customOAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jwtFilter.doFilter(request, response, filterChain);

        // then
        Mockito.verify(filterChain, Mockito.atLeastOnce()).doFilter(request, response);
        assertEquals(SecurityContextHolder.getContext().getAuthentication().getName(), jwtUtil.getUsername(token));
    }

}
