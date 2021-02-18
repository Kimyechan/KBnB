package com.buildup.kbnb.dto.comment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeInfo {
    private Double totalGrade;
    private Double cleanliness;
    private Double accuracy;
    private Double communication;
    private Double locationRate;
    private Double checkIn;
    private Double priceSatisfaction;
}
