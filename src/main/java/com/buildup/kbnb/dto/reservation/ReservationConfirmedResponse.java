package com.buildup.kbnb.dto.reservation;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationConfirmedResponse {
    private Long reservationId;
    private Long roomId;
    private String imgUrl;
    private String status;
    private String roomName;
    private String roomLocation;
    private String hostName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean isReviewed;
}
