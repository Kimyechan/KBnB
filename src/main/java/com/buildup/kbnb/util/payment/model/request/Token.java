package com.buildup.kbnb.util.payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by ehowlsla on 2018. 5. 29..
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    public String application_id;
    public String private_key;

}

