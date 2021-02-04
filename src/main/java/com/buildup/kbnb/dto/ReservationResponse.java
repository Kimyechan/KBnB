package com.buildup.kbnb.dto;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String message;

}
