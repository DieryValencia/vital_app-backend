package com.vitalapp.vital_app_backend.exception.custom;

import com.vitalapp.vital_app_backend.exception.ErrorCode;

public class InvalidTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidTokenException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_TOKEN;
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INVALID_TOKEN;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}