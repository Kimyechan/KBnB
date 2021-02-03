package com.buildup.kbnb.dto.room.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomSearchCondition {
    private LocationSearch locationSearch;
    private CheckDateSearch checkDateSearch;
    private GuestSearch guestSearch;
    private CostSearch costSearch;
    private String roomType;
}
