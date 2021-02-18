package com.buildup.kbnb.controller;

import com.buildup.kbnb.util.payment.BootPayApi;
import com.buildup.kbnb.util.payment.model.request.Cancel;
import com.buildup.kbnb.util.payment.model.response.CancelResult;
import com.buildup.kbnb.util.payment.model.response.ResDefault;
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
    private final BootPayApi bootPayApi;

    @PostMapping
    public String confirmPayment(@RequestBody PaymentDto paymentDto) throws Exception {
        String token = bootPayApi.getAccessToken();

        bootPayApi.verify(token, paymentDto.getReceipt_id(), paymentDto.getPrice());

        ResponseEntity<ResDefault> res = bootPayApi.confirm(token, paymentDto.getReceipt_id());
        bootPayApi.checkConfirm(res);
        
        return  "Ok";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDto {
        private String receipt_id;
        private Integer price;
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelResult> cancelPayment(@RequestBody Cancel cancel) throws Exception {
        String token = bootPayApi.getAccessToken();

        return bootPayApi.cancel(cancel, token);
    }
}
