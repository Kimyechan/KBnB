package com.buildup.kbnb.dto.room.search;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class GuestSearch {
    private Integer numOfAdult;
    private Integer numOfKid;
    private Integer numOfInfant;
}
