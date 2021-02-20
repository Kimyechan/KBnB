package com.buildup.kbnb.service.reservation;

import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.service.PaymentService;
import com.buildup.kbnb.util.payment.BootPayApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BootPayApi bootPayApi;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("지난달 예약내역 조회")
    public void getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(1);

        LocalDate previousStartDate = previous.withDayOfMonth(1);
        LocalDate previousEndDate = now.withDayOfMonth(1).minusDays(1);

        given(reservationRepository.findBeforeMonthReservation(1L, previousStartDate, previousEndDate))
                .willReturn(new ArrayList<>());

        reservationService.getBeforeMonthReservation(1L);

        verify(reservationRepository, times(1)).findBeforeMonthReservation(1L, previousStartDate, previousEndDate);
    }
}