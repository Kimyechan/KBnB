package com.buildup.kbnb.dto.comment;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateReq {
    @NotEmpty
    private Long reservationId;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double cleanliness;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double accuracy;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double communication;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double locationRate;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double checkIn;

    @NotEmpty
    @Min(0)
    @Max(5)
    private Double priceSatisfaction;

    @NotEmpty
    private String description;
}
