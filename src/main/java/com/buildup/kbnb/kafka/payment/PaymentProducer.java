package com.buildup.kbnb.kafka.payment;

import com.buildup.kbnb.kafka.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void sendPaymentInfo(String token, String receiptId) {
        PaymentDto paymentDto = PaymentDto.builder()
                .token(token)
                .receiptId(receiptId)
                .build();

        kafkaTemplate.send("payment-confirm", paymentDto);
    }
}
