package com.example.springbootbindmine.modules.common.oauth.dto;

import lombok.Builder;

@Builder
public record OAuthLoginDTO(
        String accessToken,
        String refreshToken,
        String userName
) {
}
