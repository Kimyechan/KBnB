package com.buildup.kbnb.dto.room;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomResponseDto {
    String msg = "방등록이 완료되었습니다.";
    Long roomId;
}
