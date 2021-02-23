package com.buildup.kbnb.dto.host.manage;

import com.buildup.kbnb.model.room.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostGetRoomRes {
    String roomUrl;
    String toDo;
    String available;
    int bedRoomNum;
    int bedNum;
    int bathNum;
    String location;
public HostGetRoomRes createDto(Room room) {
    this.roomUrl = room.getRoomImgList().get(0).getUrl();
    this.toDo = "to do space";
    this.available = "즉시 예약 기능";
    this.bedRoomNum = room.getBedRoomList().size();
    this.bedNum = room.getBedNum();
    this.bathNum = room.getBathRoomList().size();
    this.location = room.getLocation().getCountry() + " " + room.getLocation().getCity() + " " +
                    room.getLocation().getBorough() + " " + room.getLocation().getNeighborhood() +
                    room.getLocation().getNeighborhood();
    return this;
}
    public HostGetRoomRes createDto() {
        this.roomUrl = "test url";
        this.toDo = "to do space";
        this.available = "즉시 예약 기능";
        this.bedRoomNum = 3;
        this.bedNum = 3;
        this.bathNum = 3;
        this.location = "테스트 주소";
        return this;
    }
}
