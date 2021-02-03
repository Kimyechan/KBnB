package com.buildup.kbnb.dto.room.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSearch {
    private Double latitude;
    private Double longitude;
    private Double latitudeMax;
    private Double latitudeMin;
    private Double longitudeMax;
    private Double longitudeMin;
}
