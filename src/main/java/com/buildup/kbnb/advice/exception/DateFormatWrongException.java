package com.buildup.kbnb.advice.exception;

public class DateFormatWrongException extends RuntimeException{
    private static final String MESSAGE = "check date format, yyyy-mm-dd";
    public DateFormatWrongException() {
        super(MESSAGE);
    }

    public DateFormatWrongException(String message) {
        super(message);
    }
}
