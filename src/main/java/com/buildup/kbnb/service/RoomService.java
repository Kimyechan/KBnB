package com.buildup.kbnb.service;

import com.buildup.kbnb.advice.exception.ReservationException;

import com.buildup.kbnb.dto.comment.GradeDto;

import com.buildup.kbnb.dto.room.CreateRoomDto;
import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Page<Room> searchListByCondition(RoomSearchCondition roomSearchCondition, Pageable pageable) {
        return roomRepository.searchByCondition(roomSearchCondition, pageable);
    }

    public int getBedNum(List<BedRoom> bedRoomList) {
        int bedNum = 0;
        for (BedRoom bedRoom : bedRoomList) {
            bedNum += bedRoom.getDoubleSize() + bedRoom.getQueenSize() + bedRoom.getSingleSize() + bedRoom.getSuperSingleSize();
        }
        return bedNum;
    }

    public Room getRoomDetailById(Long roomId) {
        return roomRepository.findByIdWithUserLocation(roomId).orElseThrow();
    }

    public Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new ReservationException("해당 방이 존재하지 않습니다."));
    }
    public Room save(Room room) {
        return roomRepository.save(room);
    }
    public Room updateRoomGrade(Room room, GradeDto gradeDto) {
        room.setCleanliness(gradeDto.getCleanliness());
        room.setAccuracy(gradeDto.getAccuracy());
        room.setCommunication(gradeDto.getCommunication());
        room.setLocationRate(gradeDto.getLocationRate());
        room.setCheckIn(gradeDto.getCheckIn());
        room.setPriceSatisfaction(gradeDto.getPriceSatisfaction());
        room.setGrade(gradeDto.getTotalGrade());

        return roomRepository.save(room);
    }

    public Room registerRoom(User host, CreateRoomDto createRoomDto) {
        Room room = Room.builder().host(host).name(createRoomDto.getName()).cleaningCost(createRoomDto.getCleaningCost()).isSmoking(createRoomDto.getIsSmoking())
                .isParking(createRoomDto.getIsParking()).checkOutTime(createRoomDto.getCheckOutTime()).bathRoomList(createRoomDto.getBathRoomList()).bedRoomList(createRoomDto.getBedRoomList())
                .roomCost(createRoomDto.getRoomCost()).roomType(createRoomDto.getRoomType()).location(createRoomDto.getLocation()).tax(createRoomDto.getTax()).description(createRoomDto.getDescription())
                .peopleLimit(createRoomDto.getPeopleLimit()).checkInTime(createRoomDto.getCheckInTime()).roomImgList(createRoomDto.getRoomImgList()).bedNum(createRoomDto.getBedNum())
                .build();
        roomRepository.save(room);
        return room;
    }
}
