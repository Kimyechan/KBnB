package com.buildup.kbnb.advice.exception;

public class TypeMissMatchException extends RuntimeException{
    private static final String MESSAGE = "Miss match with required type";
    public TypeMissMatchException() {
        super(MESSAGE);
    }

    public TypeMissMatchException(String message) {
        super(message);
    }
}
