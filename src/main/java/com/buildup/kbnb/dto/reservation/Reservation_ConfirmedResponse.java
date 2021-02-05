package com.buildup.kbnb.dto.reservation;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation_ConfirmedResponse {
    private Long reservationId;
    private Long roomId;
    private List<String> imgUrl;
    private String status;
    private String roomName;
    private String roomLocation;
    private String hostName;
    private LocalDate checkIn;
    private LocalDate checkOut;
}
