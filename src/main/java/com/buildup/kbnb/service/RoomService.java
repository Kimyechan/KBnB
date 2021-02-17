package com.buildup.kbnb.service;

import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.controller.RoomController;
import com.buildup.kbnb.dto.comment.GradeInfo;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.*;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
    private final RoomRepository roomRepository;
    private final BedRoomRepository bedRoomRepository;
    private final BathRoomRepository bathRoomRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RoomImgRepository roomImgRepository;;


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

    public Room updateRoomGrade(Room room, GradeInfo gradeInfo) {
        room.setCleanliness(gradeInfo.getCleanliness());
        room.setAccuracy(gradeInfo.getAccuracy());
        room.setCommunication(gradeInfo.getCommunication());
        room.setLocationRate(gradeInfo.getLocationRate());
        room.setCheckIn(gradeInfo.getCheckIn());
        room.setPriceSatisfaction(gradeInfo.getPriceSatisfaction());
        room.setGrade(gradeInfo.getTotalGrade());

        return roomRepository.save(room);
    }

    public void createRoomDummyData(RoomController.RoomDummy roomDummy, UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        for (RoomController.RoomDummyDetail roomDummyDetail : roomDummy.getRoomList()) {
            Location location = Location.builder()
                    .country(roomDummyDetail.getCountry())
                    .city(roomDummyDetail.getCity())
                    .borough(roomDummyDetail.getBorough())
                    .neighborhood(roomDummyDetail.getNeighborhood())
                    .detailAddress(roomDummyDetail.getLocation())
                    .latitude(roomDummyDetail.getLatitude())
                    .longitude(roomDummyDetail.getLongitude())
                    .build();
            locationRepository.save(location);

            Room room = Room.builder()
                    .name("room name")
                    .roomType("Shared room")
                    .roomCost(20000.0)
                    .cleaningCost(5000.0)
                    .tax(1000.0)
                    .peopleLimit(4)
                    .description("room description")
                    .checkInTime(LocalTime.of(15, 0))
                    .checkOutTime(LocalTime.of(13, 0))
                    .isSmoking(false)
                    .isParking(false)
                    .grade(0.0)
                    .bedNum(4)
                    .location(location)
                    .host(user)
                    .build();
            Room savedRoom = roomRepository.save(room);

            RoomImg roomImg1 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A57%3A19.837231.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg1);

            RoomImg roomImg2 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A57%3A13.657290.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg2);

            RoomImg roomImg3 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A57%3A07.679442.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg3);

            RoomImg roomImg4 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A57%3A02.003599.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg4);

            RoomImg roomImg5 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A56%3A56.467072.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg5);

            RoomImg roomImg6 = RoomImg.builder()
                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-16T11%3A56%3A16.700504.png")
                    .room(savedRoom)
                    .build();
            roomImgRepository.save(roomImg6);

            BathRoom bathRoom = BathRoom.builder()
                    .isPrivate(true)
                    .room(savedRoom)
                    .build();
            bathRoomRepository.save(bathRoom);

            BedRoom bedRoom1 = BedRoom.builder()
                    .queenSize(0)
                    .doubleSize(2)
                    .singleSize(0)
                    .superSingleSize(0)
                    .room(savedRoom)
                    .build();
            bedRoomRepository.save(bedRoom1);

            BedRoom bedRoom2 = BedRoom.builder()
                    .queenSize(0)
                    .doubleSize(2)
                    .singleSize(0)
                    .superSingleSize(0)
                    .room(savedRoom)
                    .build();
            bedRoomRepository.save(bedRoom2);
        }
    }
}
