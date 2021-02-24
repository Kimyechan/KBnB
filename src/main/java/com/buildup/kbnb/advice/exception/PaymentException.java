package com.buildup.kbnb.advice.exception;

public class PaymentException extends RuntimeException {
    private static final String MESSAGE = "Not valid payment info";
    public PaymentException() {
        super(MESSAGE);
    }

    public PaymentException(String MESSAGE) {
        super(MESSAGE);
    }
}
