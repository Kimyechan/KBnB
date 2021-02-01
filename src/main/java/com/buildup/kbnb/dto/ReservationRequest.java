package com.buildup.kbnb.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guestNumber;
    private int infantNumber;
    private int totalCost;
    private String message;
}
