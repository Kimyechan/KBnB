package com.buildup.kbnb.util.payment.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptData {
    private Double price;
    private Integer status;
}
