package com.buildup.kbnb.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {
    private String name;
    private LocalDate birth;
    @Email
    private String email;

}
