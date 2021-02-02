package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.BathRoomRepository;
import com.buildup.kbnb.repository.BedRoomRepository;
import com.buildup.kbnb.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    BathRoomRepository bathRoomRepository;

    @Autowired
    BedRoomRepository bedRoomRepository;

//    @Test
//    @DisplayName("숙소 리스트 조건 검색")
//    public void getListByCondition() {
//        Location location = Location.builder()
//                .latitude(10.0)
//                .longitude(10.0)
//                .build();
//
//        locationRepository.save(location);
//        Room room = Room.builder()
//                .name("test room name")
//                .location(location)
//                .build();
//
//        roomRepository.save(room);
//
//        BathRoom bathRoom = BathRoom.builder()
//                .isPrivate(true)
//                .room(room)
//                .build();
//
//        bathRoomRepository.save(bathRoom);
//
//        BedRoom bedRoom1 = BedRoom.builder()
//                .doubleSize(2)
//                .room(room)
//                .build();
//
//        bedRoomRepository.save(bedRoom1);
//
//        BedRoom bedRoom2 = BedRoom.builder()
//                .doubleSize(2)
//                .room(room)
//                .build();
//
//        bedRoomRepository.save(bedRoom2);
//        bedRoomRepository.flush();
//        bathRoomRepository.flush();
//        roomRepository.flush();
//        locationRepository.flush();
//
//        RoomSearchCondition condition = new RoomSearchCondition();
//        Pageable pageable = PageRequest.of(0, 5);
//
//        Page<Room> rooms = roomRepository.searchByCondition(condition, pageable);
//        List<Room> roomList = rooms.getContent();
//
//        for (Room roomTest : roomList) {
//            Location locationTest = roomTest.getLocation();
//            System.out.println(locationTest.getLatitude());
//            List<BathRoom> bathRooms = roomTest.getBathRoomList();
//            System.out.println(bathRooms.get(0).getIsPrivate());
//            List<BedRoom> bedRooms = roomTest.getBedRoomList();
//            System.out.println(bedRooms.get(0).getDoubleSize());
//        }
//    }
}