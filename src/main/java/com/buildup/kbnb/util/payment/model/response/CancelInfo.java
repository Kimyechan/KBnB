package com.buildup.kbnb.util.payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelInfo {
    private String receipt_id;
    private Integer request_cancel_price;
    private Integer remain_price;
    private Integer remain_tax_free;
    private Integer cancelled_price;
    private Integer cancelled_tax_free;
    private String revoked_at;
    private String tid;
}
