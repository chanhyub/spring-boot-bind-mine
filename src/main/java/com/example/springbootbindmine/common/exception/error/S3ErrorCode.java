package com.example.springbootbindmine.common.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum S3ErrorCode implements ErrorCode{
    EMPTY_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "업로드 한 이미지 파일이 비어있습니다."),
    IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일 업로드에 실패했습니다."),
    NO_FILE_EXTENSION(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일의 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 파일 확장자 입니다."),
    PUT_OBJECT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3에 파일을 저장하는데 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    S3ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
