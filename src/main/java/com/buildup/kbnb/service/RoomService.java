package com.buildup.kbnb.service;

import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.model.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    public List<RoomDto> getRoomDtoList(List<Room> content) {
        List<RoomDto> roomDtoList = new ArrayList<>();

        for (Room room : content) {
            RoomDto roomDto = RoomDto.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .roomType(room.getRoomType())
                    .cost(room.getRoomCost())
                    .peopleLimit(room.getPeopleLimit())
                    .grade(room.getGrade())
                    .bedRoomNum(room.getBedRoomList().size())
                    .bathRoomNum(room.getBathRoomList().size())
                    .checkInTime(room.getCheckInTime())
                    .checkOutTime(room.getCheckOutTime())
                    .isSmoking(room.getIsSmoking())
                    .isParking(room.getIsParking())
                    .latitude(room.getLocation().getLatitude())
                    .longitude(room.getLocation().getLongitude())
                    .commentCount(room.getCommentList().size())
                    .build();

            roomDtoList.add(roomDto);
        }
        return roomDtoList;
    }
}
