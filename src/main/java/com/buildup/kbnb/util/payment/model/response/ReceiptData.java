package com.buildup.kbnb.util.payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptData {
    private Integer price;
    private Integer status;
}
