package com.example.springbootbindmine.common.security.filter;

import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.common.security.dto.CustomOAuthUser;
import com.example.springbootbindmine.modules.user.dto.UserDTO;
import com.example.springbootbindmine.modules.user.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    // @RequiredArgsConstructor 통해 생성자 주입
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        // 요청헤더에 accessToken이 없는 경우
        if(accessToken  == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 제거 <- oAuth2를 이용했다고 명시적으로 붙여주는 타입인데 JWT를 검증하거나 정보를 추출 시 제거해줘야한다.
        String originToken = accessToken.substring(7);

        // 유효한지 확인 후 클라이언트로 상태 코드 응답
        try {
            if(jwtUtil.isExpired(originToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // accessToken인지 refreshToken인지 확인
        String category = jwtUtil.getCategory(originToken);

        // JWTFilter는 요청에 대해 accessToken만 취급하므로 access인지 확인
        if(!"access".equals(category)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 사용자명과 권한을 accessToken에서 추출
        String username = jwtUtil.getUsername(originToken);
        String role = jwtUtil.getRole(originToken);

        UserDTO userDTO = UserDTO.builder()
                .userName(username)
                .role(Role.valueOf(role))
                .build();

        CustomOAuthUser customOAuth2User = new CustomOAuthUser(userDTO);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
