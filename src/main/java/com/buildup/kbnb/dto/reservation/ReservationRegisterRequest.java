package com.buildup.kbnb.dto.reservation;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRegisterRequest {
    @NotNull
    private Long roomId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;

    @NotNull
    private Integer guestNumber;

    @NotNull
    private Integer infantNumber;

    @NotNull
    private Long totalCost;

    private String message;

    private PaymentDto payment;
}
