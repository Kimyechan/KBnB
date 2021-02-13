package com.buildup.kbnb.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import java.time.LocalDate;

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
