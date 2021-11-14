package com.buildup.kbnb.service;

import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Spy
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 승인 상태 완료로 변경")
    public void makeConfirmStateTrue() {
        Payment payment = Payment.builder()
                .id(1L)
                .isConfirm(false)
                .build();

        doReturn(payment).when(paymentService).findPayment(payment.getId());

        paymentService.makeConfirmStateTrue(payment.getId());

        assertTrue(payment.getIsConfirm());
    }
}