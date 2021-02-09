package com.buildup.kbnb.dto.reservation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation_RegisterResponse {
    private Long reservationId;
    private String message;

}
