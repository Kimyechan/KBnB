package com.buildup.kbnb.dto.room.search;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class LocationSearch {
    private Double latitude;
    private Double longitude;
    private Double latitudeMax;
    private Double latitudeMin;
    private Double longitudeMax;
    private Double longitudeMin;
}
