package com.buildup.kbnb.advice.exception;

public class WrongDateFormatException extends RuntimeException{
    private static final String MESSAGE = "check date format, yyyy-mm-dd";
    public WrongDateFormatException() {
        super(MESSAGE);
    }

    public WrongDateFormatException(String message) {
        super(message);
    }
}
