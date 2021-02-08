package com.buildup.kbnb.dto.room.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckDateSearch {
    private LocalDate startDate;
    private LocalDate endDate;
}
