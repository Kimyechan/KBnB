package com.buildup.kbnb.util.payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cancel {
    public String receipt_id;
    public String name;
    public String reason;
//    public Double price;
}
