package com.buildup.kbnb.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private Long id;
    private String name;
    private String birth;
    @Email
    private String email;
    @JsonIgnore
    private String password;
}
