package com.buildup.kbnb.advice.exception;

public class BadRequestException extends RuntimeException {
    private static final String MESSAGE = "요청값이 잘못되었습니다";

    public BadRequestException() {
        super(MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
