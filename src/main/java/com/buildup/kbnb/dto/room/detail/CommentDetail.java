package com.buildup.kbnb.dto.room.detail;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDetail {
    private Long id;
    private Float cleanliness;
    private Float accuracy;
    private Float communication;
    private Float location;
    private Float checkIn;
    private Float priceSatisfaction;

    private String userName;
    private LocalDate date;
    private String userImgUrl;
}
