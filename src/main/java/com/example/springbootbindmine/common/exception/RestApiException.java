package com.example.springbootbindmine.common.exception;

import com.example.springbootbindmine.common.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class RestApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public RestApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
