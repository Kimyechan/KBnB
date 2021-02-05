package com.buildup.kbnb.dto.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private boolean success;
    private int code;
    private String msg;
}
