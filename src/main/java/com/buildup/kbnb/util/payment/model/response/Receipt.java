package com.buildup.kbnb.util.payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    private Integer status;
    private Integer code;
    private String message;
    private ReceiptData data;
}
