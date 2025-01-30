package com.example.springbootbindmine.modules.common.oauth.service;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.exception.error.UserErrorCode;
import com.example.springbootbindmine.common.security.JWTUtil;
import com.example.springbootbindmine.modules.common.api.GoogleClient;
import com.example.springbootbindmine.modules.common.api.KakaoClient;
import com.example.springbootbindmine.modules.common.oauth.dto.OAuthLoginDTO;
import com.example.springbootbindmine.modules.common.oauth.dto.UserInfoDTO;
import com.example.springbootbindmine.modules.common.oauth.request.OAuthLoginRequest;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OAuthLoginService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final KakaoClient kakaoClient;
    private final GoogleClient googleClient;

    public OAuthLoginService(
            UserRepository userRepository,
            JWTUtil jwtUtil,
            KakaoClient kakaoClient,
            GoogleClient googleClient
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.kakaoClient = kakaoClient;
        this.googleClient = googleClient;
    }

    @Transactional
    public OAuthLoginDTO login(OAuthLoginRequest oAuthLoginRequest) throws ParseException {
        JSONParser jsonParser = new JSONParser();

        UserInfoDTO userInfoDTO;

        switch (oAuthLoginRequest.provider()) {
            case "kakao" -> {
                ResponseEntity<String> result = kakaoClient.getUserInfo(oAuthLoginRequest.accessToken());
                JSONObject userInfo = (JSONObject) jsonParser.parse(result.getBody());

                userInfoDTO = UserInfoDTO.kakao(userInfo);
            }
            case "google" -> {
                ResponseEntity<String> result = googleClient.getTokenInfo(oAuthLoginRequest.accessToken());
                JSONObject tokenInfo = (JSONObject) jsonParser.parse(result.getBody());

                userInfoDTO = UserInfoDTO.google(tokenInfo);
            }
            default -> throw new RestApiException(UserErrorCode.INVALID_PROVIDER);
        }

        String username = userInfoDTO.provider() + "_" + userInfoDTO.providerId();
        Optional<UserEntity> optionalUser = userRepository.findByUserNameAndDeleteDateNull(username);

        if (optionalUser.isEmpty()) {
            UserEntity newUserEntity = UserEntity.builder()
                    .name(userInfoDTO.name())
                    .email(userInfoDTO.email())
                    .imageFileLink(userInfoDTO.profileImage())
                    .userName(username)
                    .role(Role.USER)
                    .createDate(LocalDateTime.now())
                    .build();

            UserEntity saveUser = userRepository.save(newUserEntity);

            // accessToken과 refreshToken 생성
            String accessToken = jwtUtil.createJwt("access", saveUser.getUserName(), saveUser.getRole().name(), 3600000L);
            String refreshToken = jwtUtil.createJwt("refresh", saveUser.getUserName(), saveUser.getRole().name(), 259200000L);

            return OAuthLoginDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userName(saveUser.getUserName())
                    .build();
        }
        else{
            // accessToken과 refreshToken 생성
            String accessToken = jwtUtil.createJwt("access", optionalUser.get().getUserName(), optionalUser.get().getRole().name(), 3600000L);
            String refreshToken = jwtUtil.createJwt("refresh", optionalUser.get().getUserName(), optionalUser.get().getRole().name(), 259200000L);

            return OAuthLoginDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userName(optionalUser.get().getUserName())
                    .build();
        }

    }
}
