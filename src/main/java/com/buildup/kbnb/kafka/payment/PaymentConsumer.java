package com.buildup.kbnb.kafka.payment;

import com.buildup.kbnb.kafka.dto.PaymentDto;
import com.buildup.kbnb.util.payment.BootPayApi;
import com.buildup.kbnb.util.payment.model.response.ResDefault;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentConsumer {
    private final BootPayApi bootPayApi;

    @KafkaListener(topics = "payment-confirm", groupId = "payment")
    public void confirmPayment(PaymentDto paymentDto, Consumer<Object, Object> consumer) {
        ResponseEntity<ResDefault> res = bootPayApi.confirm(paymentDto.getToken(), paymentDto.getReceiptId());
        if (res.getBody() != null && res.getBody().getStatus() == 200) {
            consumer.commitAsync();
        }
    }
}
