package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.BathRoomRepository;
import com.buildup.kbnb.repository.BedRoomRepository;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final BedRoomRepository bedRoomRepository;
    private final BathRoomRepository bathRoomRepository;
    private final LocationRepository locationRepository;

    @PostMapping("/list")
    public ResponseEntity<?> getRoomList(@RequestBody RoomSearchCondition roomSearchCondition,
                                         Pageable pageable,
                                         PagedResourcesAssembler<RoomDto> assembler) {
        Page<Room> roomPage = roomService.searchListByCondition(roomSearchCondition, pageable);
//        List<RoomDto> roomDtoList = roomService.getRoomDtoList(roomPage.getContent());
        List<RoomDto> roomDtoList = new ArrayList<>();

        for (Room room : roomPage.getContent()) {
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

        Page<RoomDto> result = new PageImpl<>(roomDtoList, pageable, roomPage.getTotalElements());
        PagedModel<EntityModel<RoomDto>> model = assembler.toModel(result);

        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/test")
    public String getRoomListTest() {
        Location location = Location.builder()
                .latitude(13.0)
                .longitude(13.0)
                .build();

        locationRepository.save(location);
        Room room = Room.builder()
                .name("test room name 2")
                .roomType("Shared room")
                .location(location)
                .roomCost(10000.0)
                .peopleLimit(4)
                .build();

        roomRepository.save(room);

        BathRoom bathRoom = BathRoom.builder()
                .isPrivate(true)
                .room(room)
                .build();

        bathRoomRepository.save(bathRoom);

        BedRoom bedRoom1 = BedRoom.builder()
                .doubleSize(2)
                .room(room)
                .build();

        bedRoomRepository.save(bedRoom1);

        BedRoom bedRoom2 = BedRoom.builder()
                .doubleSize(2)
                .room(room)
                .build();

        bedRoomRepository.save(bedRoom2);

        return "ok";
    }
}
