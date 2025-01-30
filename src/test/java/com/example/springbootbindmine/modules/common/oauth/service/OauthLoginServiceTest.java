package com.example.springbootbindmine.modules.common.oauth.service;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.common.api.GoogleClient;
import com.example.springbootbindmine.modules.common.api.KakaoClient;
import com.example.springbootbindmine.modules.common.oauth.dto.OAuthLoginDTO;
import com.example.springbootbindmine.modules.common.oauth.dto.UserInfoDTO;
import com.example.springbootbindmine.modules.common.oauth.request.OAuthLoginRequest;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OAuthLoginService 테스트")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OauthLoginServiceTest {
    @Autowired
    private OAuthLoginService oAuthLoginService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;
    @MockitoBean
    private KakaoClient kakaoClient;
    @MockitoBean
    private GoogleClient googleClient;

    @DisplayName("신규 사용자가 로그인을 시도할 경우 새로운 사용자를 생성하여 저장하고 AccessToken과 RefreshToken과 userName을 리턴한다.")
    @Test
    @Transactional
    void loginNewUser() throws ParseException {
        // given
        String result = "{\"id\":123456,\"connected_at\":\"2025-01-09T14:12:03Z\",\"properties\":{\"nickname\":\"테스트 유저\",\"profile_image\":\"http://localhost:8080/test.jpg\",\"thumbnail_image\":\"http://localhost:8080/test.jpg\"},\"kakao_account\":{\"profile_nickname_needs_agreement\":false,\"profile_image_needs_agreement\":false,\"profile\":{\"nickname\":\"테스트 유저\",\"thumbnail_image_url\":\"http://localhost:8080/test.jpg\",\"profile_image_url\":\"http://localhost:8080/test.jpg\",\"is_default_image\":false,\"is_default_nickname\":false},\"name_needs_agreement\":false,\"name\":\"테스트 유저\",\"has_email\":true,\"email_needs_agreement\":false,\"is_email_valid\":true,\"is_email_verified\":true,\"email\":\"test@test.com\"}}";
        OAuthLoginRequest oAuthLoginRequest = new OAuthLoginRequest("kakao access token", "kakao");
        UserInfoDTO userInfoDTO = createUserInfoDTO();
        String username = userInfoDTO.provider() + "_" + userInfoDTO.providerId();
        UserEntity userEntity = UserEntity.builder()
                .name(userInfoDTO.name())
                .email(userInfoDTO.email())
                .imageFileLink(userInfoDTO.profileImage())
                .userName(username)
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .build();

        UserEntity saveUser = userRepository.save(userEntity);

        String accessToken = jwtUtil.createJwt("access", saveUser.getUserName(), saveUser.getRole().name(), 3600000L);
        String refreshToken = jwtUtil.createJwt("refresh", saveUser.getUserName(), saveUser.getRole().name(), 259200000L);

        OAuthLoginDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userName(saveUser.getUserName())
                .build();

        // when
        Mockito.when(kakaoClient.getUserInfo(oAuthLoginRequest.accessToken())).thenReturn(ResponseEntity.ok(result));
        OAuthLoginDTO oAuthLoginDTO = oAuthLoginService.login(oAuthLoginRequest);

        // then
        assertEquals(oAuthLoginDTO.accessToken(), accessToken);
        assertEquals(oAuthLoginDTO.refreshToken(), refreshToken);
        assertEquals(oAuthLoginDTO.userName(), saveUser.getUserName());
    }

    @DisplayName("기존 사용자가 로그인을 시도할 경우 userName을 기준으로 사용자 정보를 조회하고 AccessToken과 RefreshToken과 userName을 리턴한다.")
    @Test
    @Transactional
    void loginExistUser() throws ParseException {
        // given
        String result = "{\"id\":123456,\"connected_at\":\"2025-01-09T14:12:03Z\",\"properties\":{\"nickname\":\"테스트 유저\",\"profile_image\":\"http://localhost:8080/test.jpg\",\"thumbnail_image\":\"http://localhost:8080/test.jpg\"},\"kakao_account\":{\"profile_nickname_needs_agreement\":false,\"profile_image_needs_agreement\":false,\"profile\":{\"nickname\":\"테스트 유저\",\"thumbnail_image_url\":\"http://localhost:8080/test.jpg\",\"profile_image_url\":\"http://localhost:8080/test.jpg\",\"is_default_image\":false,\"is_default_nickname\":false},\"name_needs_agreement\":false,\"name\":\"테스트 유저\",\"has_email\":true,\"email_needs_agreement\":false,\"is_email_valid\":true,\"is_email_verified\":true,\"email\":\"test@test.com\"}}";
        OAuthLoginRequest oAuthLoginRequest = new OAuthLoginRequest("kakao access token", "kakao");
        UserInfoDTO userInfoDTO = createUserInfoDTO();
        String username = userInfoDTO.provider() + "_" + userInfoDTO.providerId();
        UserEntity userEntity = UserEntity.builder()
                .name(userInfoDTO.name())
                .email(userInfoDTO.email())
                .imageFileLink(userInfoDTO.profileImage())
                .userName(username)
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .build();

        userRepository.save(userEntity);
        UserEntity existUser = userRepository.findByUserNameAndDeleteDateNull(username).get();

        String accessToken = jwtUtil.createJwt("access", existUser.getUserName(), existUser.getRole().name(), 3600000L);
        String refreshToken = jwtUtil.createJwt("refresh", existUser.getUserName(), existUser.getRole().name(), 259200000L);

        OAuthLoginDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userName(existUser.getUserName())
                .build();

        // when
        Mockito.when(kakaoClient.getUserInfo(oAuthLoginRequest.accessToken())).thenReturn(ResponseEntity.ok(result));
        OAuthLoginDTO oAuthLoginDTO = oAuthLoginService.login(oAuthLoginRequest);

        // then
        assertEquals(existUser.getUserName(), username);
        assertEquals(oAuthLoginDTO.accessToken(), accessToken);
        assertEquals(oAuthLoginDTO.refreshToken(), refreshToken);
        assertEquals(oAuthLoginDTO.userName(), existUser.getUserName());
    }

    @DisplayName("유효하지 않은 provider가 들어온 경우 예외를 발생시킨다.")
    @Test
    void invalidProvider() {
        // given
        OAuthLoginRequest oAuthLoginRequest = new OAuthLoginRequest("kakao access token", "invalid");

        // when, then
        Assertions.assertThrows(RestApiException.class, () -> oAuthLoginService.login(oAuthLoginRequest));
    }

    private UserInfoDTO createUserInfoDTO() {
        return UserInfoDTO.builder()
                .provider("kakao")
                .providerId("123456")
                .email("test@test.com")
                .name("테스트 유저")
                .profileImage("http://localhost:8080/test.jpg")
                .build();
    }

}
