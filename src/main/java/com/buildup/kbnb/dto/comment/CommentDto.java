package com.buildup.kbnb.dto.comment;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Double cleanliness;

    private Double accuracy;

    private Double communication;

    private Double locationRate;

    private Double checkIn;

    private Double priceSatisfaction;

    private String description;

    private String userImgUrl;

    private String userName;

    private LocalDate creatingDate;
}
