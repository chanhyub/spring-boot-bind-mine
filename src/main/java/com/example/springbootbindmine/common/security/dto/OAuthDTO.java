package com.example.springbootbindmine.common.security.dto;

import lombok.Builder;
import org.json.simple.JSONObject;

@Builder
public record OAuthDTO(
        String provider,
        String providerId,
        String email,
        String name,
        String profileImage
) {
    public static OAuthDTO google(JSONObject attribute) {
        return OAuthDTO.builder()
                .provider("google")
                .providerId(String.valueOf(attribute.get("sub")))
                .email(String.valueOf(attribute.get("email")))
                .name(String.valueOf(attribute.get("name")))
                .profileImage(String.valueOf(attribute.get("picture")))
                .build();
    }

    public static OAuthDTO kakao(JSONObject attribute) {
        JSONObject kakaoAccount = (JSONObject) attribute.get("kakao_account");
        JSONObject profile = (JSONObject) kakaoAccount.get("profile");
        return OAuthDTO.builder()
                .provider("kakao")
                .providerId(String.valueOf(attribute.get("id")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .name(String.valueOf(profile.get("nickname")))
                .profileImage(String.valueOf(profile.get("profile_image_url")))
                .build();
    }
}
