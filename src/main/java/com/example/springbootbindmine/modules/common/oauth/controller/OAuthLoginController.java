package com.example.springbootbindmine.modules.common.oauth.controller;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.exception.error.CommonErrorCode;
import com.example.springbootbindmine.modules.common.api.GoogleClient;
import com.example.springbootbindmine.modules.common.api.KakaoClient;
import com.example.springbootbindmine.common.security.dto.OAuthDTO;
import com.example.springbootbindmine.modules.common.oauth.request.OAuthLoginRequest;
import com.example.springbootbindmine.modules.common.oauth.service.OAuthLoginService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthLoginController {
    private final KakaoClient kakaoClient;
    private final GoogleClient googleClient;
    private final OAuthLoginService oAuthLoginService;

    public OAuthLoginController(KakaoClient kakaoClient,
                                GoogleClient googleClient,
                                OAuthLoginService oAuthLoginService) {
        this.kakaoClient = kakaoClient;
        this.googleClient = googleClient;
        this.oAuthLoginService = oAuthLoginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(OAuthLoginRequest oAuthLoginRequest) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        OAuthDTO oAuthDTO;

        switch (oAuthLoginRequest.provider()) {
            case "kakao" -> {
                ResponseEntity<String> result = kakaoClient.getUserInfo(oAuthLoginRequest.accessToken());
                JSONObject userInfo = (JSONObject) jsonParser.parse(result.getBody());

                oAuthDTO = OAuthDTO.kakao(userInfo);
            }
            case "google" -> {
                ResponseEntity<String> result = googleClient.getTokenInfo(oAuthLoginRequest.accessToken());
                JSONObject tokenInfo = (JSONObject) jsonParser.parse(result.getBody());

                oAuthDTO = OAuthDTO.google(tokenInfo);
            }
            default -> throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }

        return oAuthLoginService.login(oAuthDTO);
    }

}
