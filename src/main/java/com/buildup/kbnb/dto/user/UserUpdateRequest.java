package com.buildup.kbnb.dto.user;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String name;
    private String birth;
    @Email
    private String email;
}
