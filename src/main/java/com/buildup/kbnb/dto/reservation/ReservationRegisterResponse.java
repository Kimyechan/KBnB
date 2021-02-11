package com.buildup.kbnb.dto.reservation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRegisterResponse {
    private Long reservationId;
    private String message;

}
