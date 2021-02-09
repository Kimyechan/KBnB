package com.buildup.kbnb.dto.room.search;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CheckDateSearch {
    private LocalDate startDate;
    private LocalDate endDate;
}
