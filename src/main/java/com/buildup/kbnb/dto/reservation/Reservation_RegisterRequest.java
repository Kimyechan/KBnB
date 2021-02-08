package com.buildup.kbnb.dto.reservation;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation_RegisterRequest {
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guestNumber;
    private int infantNumber;
    private Long totalCost;
    private String message;
}
