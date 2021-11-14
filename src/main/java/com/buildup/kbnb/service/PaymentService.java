package com.buildup.kbnb.service;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    public void makeConfirmStateTrue(Long paymentId) {
        Payment payment = findPayment(paymentId);
        payment.setIsConfirm(true);

        paymentRepository.save(payment);
    }

    public Payment findPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new BadRequestException("결제 정보를 찾을 수 없습니다."));
    }
}
