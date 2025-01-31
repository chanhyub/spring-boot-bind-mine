package com.example.springbootbindmine.common.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "잘못된 Provider 입니다."),
    INACTIVE_USER(HttpStatus.FORBIDDEN, "인증되지 않은 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
