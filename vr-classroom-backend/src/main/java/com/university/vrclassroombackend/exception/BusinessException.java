package com.university.vrclassroombackend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final int statusCode;
    private final String errorCode;
    
    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = null;
    }
    
    public BusinessException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
