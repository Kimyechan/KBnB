package com.buildup.kbnb.dto.room.check;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckRoomRes {
    private Long roomId;
    private Boolean isChecked;
}
