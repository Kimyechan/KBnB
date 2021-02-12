package com.buildup.kbnb.controller;

import com.buildup.kbnb.util.payment.BootPayApi2;
import com.buildup.kbnb.util.payment.model.response.Receipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final BootPayApi2 bootPayApi2;

    @PostMapping
    public String paymentVerify(@RequestBody PaymentDto paymentDto) throws Exception {
        String token = bootPayApi2.getAccessToken();
        ResponseEntity<Receipt> receiptResponseEntity = bootPayApi2.verify(paymentDto.getReceipt_id(), token);
        
        Receipt receipt = receiptResponseEntity.getBody();
        if (!receipt.getData().getPrice().equals(paymentDto.getPrice())) {
            throw new Exception("금액이 변조 되었습니다");
        }
        
        return  "Ok";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDto {
        private String receipt_id;
        private Integer price;
    }

}
