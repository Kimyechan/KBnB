package com.buildup.kbnb.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SignUpRequest {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate birth;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
