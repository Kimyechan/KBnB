package com.buildup.kbnb.dto.comment;

import com.buildup.kbnb.model.Comment;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListResponse {
    private Double grade;

    private Double cleanliness;

    private Double accuracy;

    private Double communication;

    private Double locationRate;

    private Double checkIn;

    private Double priceSatisfaction;

    PagedModel<EntityModel<CommentDto>> allComments;
}
