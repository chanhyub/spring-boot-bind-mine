package com.example.springbootbindmine.modules.common.oauth.request;

public record OAuthLoginRequest(
        String accessToken,
        String provider
) {
}
