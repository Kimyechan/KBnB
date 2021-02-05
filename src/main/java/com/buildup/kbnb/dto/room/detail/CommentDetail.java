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
    private String description;
    private String userName;
    private LocalDate date;
    private String userImgUrl;
}
