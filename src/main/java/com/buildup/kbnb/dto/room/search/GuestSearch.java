package com.buildup.kbnb.dto.room.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestSearch {
    private Integer numOfAdult;
    private Integer numOfKid;
    private Integer numOfInfant;
}
