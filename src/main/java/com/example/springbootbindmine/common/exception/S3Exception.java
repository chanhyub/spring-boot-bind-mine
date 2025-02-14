package com.example.springbootbindmine.common.exception;

import com.example.springbootbindmine.common.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException{
    private final ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
