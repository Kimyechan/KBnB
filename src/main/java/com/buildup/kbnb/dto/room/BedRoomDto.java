package com.buildup.kbnb.dto.room;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedRoomDto {
    //bedRoom
    private Integer queenSize;
    private Integer doubleSize;
    private Integer singleSize;
    private Integer superSingleSize;
}
