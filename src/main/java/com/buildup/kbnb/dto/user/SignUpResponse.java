package com.buildup.kbnb.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
