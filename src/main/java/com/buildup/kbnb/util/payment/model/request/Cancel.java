package com.buildup.kbnb.util.payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cancel {
    public String receipt_id;
    public String name;
    public String reason;
//    public Double price;
}
