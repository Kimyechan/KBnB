package com.buildup.kbnb.dto.room.search;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CostSearch {
    private Double minCost;
    private Double maxCost;
}
