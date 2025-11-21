package com.vitalapp.vital_app_backend.exception.custom;

import com.vitalapp.vital_app_backend.exception.ErrorCode;

public class UnauthorizedException extends RuntimeException {
    private final ErrorCode errorCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNAUTHORIZED;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}