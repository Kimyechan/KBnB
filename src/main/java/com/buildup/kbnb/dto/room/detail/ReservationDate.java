package com.buildup.kbnb.dto.room.detail;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDate {
    private LocalDate checkIn;
    private LocalDate checkOut;
}
