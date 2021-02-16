package com.buildup.kbnb.service;

import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.*;
import com.buildup.kbnb.repository.room.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomServiceTest {
    RoomService roomService;

    @Mock
    RoomRepository roomRepository;
    @Mock
    LocationRepository locationRepository;

    @Mock BedRoomRepository bedRoomRepository;
    @Mock BathRoomRepository bathRoomRepository;
    @Mock UserRepository userRepository;
    @Mock RoomImgRepository roomImgRepository;;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roomService = new RoomService(roomRepository, locationRepository, bedRoomRepository, bathRoomRepository, userRepository, roomImgRepository);
    }

    @Test
    void getBedNum() {
        BedRoom bedRoom1 = BedRoom.builder()
                .queenSize(1)
                .doubleSize(1)
                .singleSize(0)
                .superSingleSize(0)
                .build();

        BedRoom bedRoom2 = BedRoom.builder()
                .queenSize(0)
                .doubleSize(0)
                .singleSize(2)
                .superSingleSize(0)
                .build();

        List<BedRoom> bedRooms = new ArrayList<>();
        bedRooms.add(bedRoom1);
        bedRooms.add(bedRoom2);

        Integer bedNum = roomService.getBedNum(bedRooms);

        assertThat(bedNum).isEqualTo(4);
    }
}