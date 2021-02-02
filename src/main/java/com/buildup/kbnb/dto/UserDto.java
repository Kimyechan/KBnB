package com.buildup.kbnb.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String email;
    private LocalDate birth;
    private String imageUrl;
    private Boolean emailVerified = false;
}
