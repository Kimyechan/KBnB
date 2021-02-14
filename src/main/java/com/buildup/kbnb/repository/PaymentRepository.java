package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteByReceiptId(String receiptId);
}
