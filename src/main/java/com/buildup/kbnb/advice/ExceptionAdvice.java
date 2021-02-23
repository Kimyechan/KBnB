package com.buildup.kbnb.advice;

import com.buildup.kbnb.advice.exception.*;
import com.buildup.kbnb.dto.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(EmailDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse emailDuplicate(EmailDuplicationException e) {
        return ExceptionResponse.builder()
                .code(-1001)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(EmailOrPassWrongException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse emailOrPassWrong(EmailOrPassWrongException e) {
        return ExceptionResponse.builder()
                .code(-1002)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(UserFieldNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse userFieldNotValid(UserFieldNotValidException e) {
        return ExceptionResponse.builder()
                .code(-1003)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(CommentFieldNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse commentFieldNotValid(CommentFieldNotValidException e) {
        return ExceptionResponse.builder()
                .code(-1004)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(ReservationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse reservation(ReservationException e) {
        return ExceptionResponse.builder()
                .code(-2001)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(PaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse payment(PaymentException e) {
        return ExceptionResponse.builder()
                .code(-3001)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(RoomFieldNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse RoomFieldNotValid(RoomFieldNotValidException e) {
        return ExceptionResponse.builder()
                .code(-4001)
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse DateTimeParse(DateTimeParseException e) {
        return ExceptionResponse.builder()
                .code(-6001)
                .msg(e.getMessage())
                .build();
    }
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected ExceptionResponse entireException(Exception e) {
//        return ExceptionResponse.builder()
//                .code(-1000)
//                .msg(e.getMessage())
//                .build();
//    }

}
