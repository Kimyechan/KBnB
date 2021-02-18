package com.buildup.kbnb.dto.room.search;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RoomSearchCondition {
    private LocationSearch locationSearch;
    private CheckDateSearch checkDateSearch;
    private GuestSearch guestSearch;
    private CostSearch costSearch;
    private String roomType;
    private Integer bedNum;
    private Integer bedRoomNum;
    private Integer bathRoomNum;
}
