package com.example.springbootbindmine.common.security.config;

import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.common.security.filter.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String springDatasourceDriverClassName;

    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // h2 관련 옵션
        if (springDatasourceDriverClassName.equals("org.h2.Driver")) {

            // h2 관련 옵션
            http.headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

            // h2 관련 authorize
            http.authorizeHttpRequests(config -> config
                    .requestMatchers(PathRequest.toH2Console())
                    .permitAll());
        }

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    // 모든 출처에서 요청 허용 (http://localhost:3000와 같이 주소로 허용가능)
                    configuration.setAllowedOrigins(
                            List.of(
                                    "http://localhost",
                                    "http://localhost:3000/",
                                    "http://localhost:8080",
                                    "http://43.201.78.53",
                                    "http://43.201.78.53:3000/",
                                    "http://43.201.78.53:8080")
                    );
                    // HTTP 메소드(GET, POST 등 모든요청)의 요청을 허용
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    // 인증 정보(쿠키, 인증 토큰 등)의 전송을 허용
                    configuration.setAllowCredentials(true);
                    // 모든 HTTP 헤더의 요청을 허용
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    // 최대 유효시간 설정
                    configuration.setMaxAge(3600L);

                    // 브라우저가 접근할 수 있도록 특정 응답 헤더를 노출
                    configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                }));
        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);
        //From 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);
        //HTTP Basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);
        //JWT Filter
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
//        //oauth2
//        http
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(customSuccessHandler)
//                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
//                                .userService(customOAuth2UserService)));
        //경로별 인가 작업
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/oauth/login", "/reissue", "/docs/swagger", "/docs/swagger-ui/**", "/v3/api-docs/**", "/docs/**").permitAll()
                        .anyRequest().authenticated());
        //세션 설정 : STATELESS
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
