package com.buildup.kbnb.dto.reservation;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRegisterRequest {
    @NotEmpty
    private Long roomId;
    @NotEmpty
    private LocalDate checkIn;
    @NotEmpty
    private LocalDate checkOut;
    @NonNull
    private int guestNumber;
    @NonNull
    private int infantNumber;
    @NotEmpty
    private Long totalCost;
    private String message;
    private PaymentDto payment;
}
