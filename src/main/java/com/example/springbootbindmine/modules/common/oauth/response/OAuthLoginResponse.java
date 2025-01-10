package com.example.springbootbindmine.modules.common.oauth.response;

import lombok.Builder;
import org.apache.tomcat.util.http.parser.Cookie;

@Builder
public record OAuthLoginResponse(
        String accessToken
) {
}
