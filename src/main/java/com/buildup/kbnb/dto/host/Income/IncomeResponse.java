package com.buildup.kbnb.dto.host.Income;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeResponse {
    long yearlyIncome = 0;
    long Jan = 0;   long May = 0;   long Sep = 0;
    long Feb = 0;   long Jun = 0;   long Oct = 0;
    long Mar = 0;   long Jul = 0;   long Nov = 0;
    long Apr = 0;   long Agu = 0;   long Dec = 0;
}
