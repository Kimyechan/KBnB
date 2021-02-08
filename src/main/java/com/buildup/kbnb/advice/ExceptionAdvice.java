package com.buildup.kbnb.advice;

import com.buildup.kbnb.advice.exception.EmailDuplicationException;
import com.buildup.kbnb.advice.exception.EmailOrPassWrongException;
import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.dto.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(ReservationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ExceptionResponse emailOrPassWrong(ReservationException e) {
        return ExceptionResponse.builder()
                .code(-2001)
                .msg(e.getMessage())
                .build();
    }

}
