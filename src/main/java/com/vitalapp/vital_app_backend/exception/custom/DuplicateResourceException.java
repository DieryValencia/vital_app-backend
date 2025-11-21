package com.vitalapp.vital_app_backend.exception.custom;

import com.vitalapp.vital_app_backend.exception.ErrorCode;

public class DuplicateResourceException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateResourceException(String message) {
        super(message);
        this.errorCode = ErrorCode.DUPLICATE_RESOURCE;
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.DUPLICATE_RESOURCE;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}