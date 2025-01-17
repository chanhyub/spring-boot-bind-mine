package com.example.springbootbindmine.modules.common.oauth.dto;

import lombok.Builder;
import org.json.simple.JSONObject;

@Builder
public record UserInfoDTO(
        String provider,
        String providerId,
        String email,
        String name,
        String profileImage
) {
    public static UserInfoDTO google(JSONObject attribute) {
        return UserInfoDTO.builder()
                .provider("google")
                .providerId(String.valueOf(attribute.get("sub")))
                .email(String.valueOf(attribute.get("email")))
                .name(String.valueOf(attribute.get("name")))
                .profileImage(String.valueOf(attribute.get("picture")))
                .build();
    }

    public static UserInfoDTO kakao(JSONObject attribute) {
        JSONObject kakaoAccount = (JSONObject) attribute.get("kakao_account");
        JSONObject profile = (JSONObject) kakaoAccount.get("profile");
        return UserInfoDTO.builder()
                .provider("kakao")
                .providerId(String.valueOf(attribute.get("id")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .name(String.valueOf(profile.get("nickname")))
                .profileImage(String.valueOf(profile.get("profile_image_url")))
                .build();
    }
}
