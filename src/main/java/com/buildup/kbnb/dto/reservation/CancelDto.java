package com.buildup.kbnb.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelDto {
    @NotNull
    private Long reservationId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String reason;
}
