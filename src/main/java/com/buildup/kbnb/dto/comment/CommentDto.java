package com.buildup.kbnb.dto.comment;

import com.buildup.kbnb.model.Comment;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

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
}
