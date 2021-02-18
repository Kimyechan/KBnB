package com.buildup.kbnb.util.payment;

import com.buildup.kbnb.util.payment.model.request.Cancel;
import com.buildup.kbnb.util.payment.model.request.Confirm;
import com.buildup.kbnb.util.payment.model.request.Token;
import com.buildup.kbnb.util.payment.model.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BootPayApi {
    private final RestTemplate restTemplate;

    private final String BASE_URL = "https://api.bootpay.co.kr/";
    private final String URL_ACCESS_TOKEN = BASE_URL + "request/token.json";
    private final String URL_CONFIRM = BASE_URL + "submit.json";
    private final String URL_VERIFY = BASE_URL + "receipt";
    private final String URL_CANCEL = BASE_URL + "cancel.json";

    @Value("${bootPay.applicationId}")
    private String application_id;

    @Value("${bootPay.privateKey}")
    private String private_key;

    public String getAccessToken() throws Exception {
        if (this.application_id == null || this.application_id.isEmpty()) {
            throw new Exception("application_id 값이 비어있습니다.");
        }
        if (this.private_key == null || this.private_key.isEmpty()) {
            throw new Exception("private_key 값이 비어있습니다.");
        }

        Token token = Token.builder()
                .application_id(application_id)
                .private_key(private_key)
                .build();

        ResToken resToken = restTemplate.postForObject(URL_ACCESS_TOKEN, token, ResToken.class);

        if (resToken.getStatus() == 200)
            return resToken.data.token;
        return null;
    }

    public ResponseEntity<Receipt> getReceiptInfo(String receipt_id, String token) throws Exception {
        if (token == null || token.isEmpty()) {
            throw new Exception("token 값이 비어있습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        HttpEntity<Receipt> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(URL_VERIFY + "/" + receipt_id + ".json", HttpMethod.GET, entity, Receipt.class);
    }

    public ResponseEntity<CancelResult> cancel(Cancel cancel, String token) throws Exception {
        if (token == null || token.isEmpty()) {
            throw new Exception("token 값이 비어있습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        HttpEntity<Cancel> entity = new HttpEntity<>(cancel, headers);
        return restTemplate.exchange(URL_CANCEL, HttpMethod.POST, entity, CancelResult.class);
    }

    public void verify(String token, String receipt_id, Integer price) throws Exception {
        ResponseEntity<Receipt> receiptResponseEntity = getReceiptInfo(receipt_id, token);

        Receipt receipt = receiptResponseEntity.getBody();
        if (receipt.getStatus() == 200) {
            ReceiptData data = receipt.getData();
            if (!data.getPrice().equals(price) || data.getStatus() != 2) {
                throw new Exception("잘못된 정보로 결제 승인이 되지 않습니다");
            }
        }
    }

    public ResponseEntity<ResDefault> confirm(String token, String receipt_id) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        Confirm confirm = Confirm.builder()
                .receipt_id(receipt_id)
                .build();

        HttpEntity<Confirm> entity = new HttpEntity<>(confirm, headers);
        return restTemplate.exchange(URL_CONFIRM, HttpMethod.POST, entity, ResDefault.class);
    }

    public void checkConfirm(ResponseEntity<ResDefault> res) throws Exception {
        if(res.getBody().getStatus() != 200) {
            throw new Exception("잘못된 정보로 결제 승인이 되지 않습니다");
        }
    }
}
