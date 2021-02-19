package com.buildup.kbnb.dto.host.manage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostGetRoomListRes {
    String roomUrl;
    String toDo;
    String available;
    int bedRoomNum;
    int bedNum;
    int bathNum;
    String location;

}
