package com.buildup.kbnb.dto.room.detail;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDetail {
    private String country;
    private String city;
    private String borough;
    private String neighborhood;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
}
