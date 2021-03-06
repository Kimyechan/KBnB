package com.buildup.kbnb.advice.exception;

public class ReservationException extends RuntimeException{
    private static final String MESSAGE = "Not valid reservation info";
    public ReservationException() {
        super(MESSAGE);
    }

    public ReservationException(String message) {
        super(message);
    }
}
