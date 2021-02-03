package com.buildup.kbnb.dto.room.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostSearch {
    private Double minCost;
    private Double maxCost;
}
