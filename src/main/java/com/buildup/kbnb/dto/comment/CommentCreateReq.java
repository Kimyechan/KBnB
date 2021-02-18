package com.buildup.kbnb.dto.comment;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateReq {
    @NotNull
    private Long reservationId;

    @NotNull
    @Min(0)
    @Max(5)
    private Double cleanliness;

    @NotNull
    @Min(0)
    @Max(5)
    private Double accuracy;

    @NotNull
    @Min(0)
    @Max(5)
    private Double communication;

    @NotNull
    @Min(0)
    @Max(5)
    private Double locationRate;

    @NotNull
    @Min(0)
    @Max(5)
    private Double checkIn;

    @NotNull
    @Min(0)
    @Max(5)
    private Double priceSatisfaction;

    @NotEmpty
    private String description;
}
