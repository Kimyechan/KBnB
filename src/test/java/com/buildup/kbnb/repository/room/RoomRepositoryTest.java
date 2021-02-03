package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.CostSearch;
import com.buildup.kbnb.dto.room.search.GuestSearch;
import com.buildup.kbnb.dto.room.search.LocationSearch;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    private void setUpRoomList() {
        for (int i = 0; i < 25; i++) {
            Location location = Location.builder()
                    .latitude(37.0 + 0.5 * i)
                    .longitude(127.0 + 0.5 * i)
                    .build();
            locationRepository.save(location);

            Room room = Room.builder()
                    .name("test room name 2")
                    .roomType("Shared room")
                    .location(location)
                    .roomCost(10000.0 + 3000 * i)
                    .peopleLimit(i + 1)
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
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 가격 조건")
    public void getListByCost() {
        setUpRoomList();
        CostSearch costSearch = CostSearch.builder()
                .maxCost(30000.0)
                .minCost(10000.0)
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .costSearch(costSearch)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        for (Room room : roomList) {
            assertThat(room.getRoomCost()).isGreaterThanOrEqualTo(10000.0);
            assertThat(room.getRoomCost()).isLessThanOrEqualTo(30000.0);
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 위치 조건")
    public void getListByLocation() {
        setUpRoomList();
        LocationSearch locationSearch = LocationSearch.builder()
                .latitude(40.0)
                .longitude(140.0)
                .longitudeMin(139.0)
                .longitudeMax(142.0)
                .latitudeMin(39.0)
                .latitudeMax(42.0)
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .locationSearch(locationSearch)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        for (Room room : roomList) {
            assertThat(room.getLocation().getLatitude()).isBetween(39.0, 42.0);
            assertThat(room.getLocation().getLongitude()).isBetween(139.0, 142.0);
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 게스트 수 제한 조건")
    public void getListByGuestNum() {
        setUpRoomList();
        GuestSearch guestSearch = GuestSearch.builder()
                .numOfAdult(5)
                .numOfKid(4)
                .numOfInfant(2)
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .guestSearch(guestSearch)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        for (Room room : roomList) {
            assertThat(room.getPeopleLimit()).isGreaterThanOrEqualTo(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 모든 조건")
    public void getListByAllCondition() {
        setUpRoomList();
        GuestSearch guestSearch = GuestSearch.builder()
                .numOfAdult(5)
                .numOfKid(4)
                .numOfInfant(2)
                .build();

        LocationSearch locationSearch = LocationSearch.builder()
                .latitude(40.0)
                .longitude(140.0)
                .longitudeMin(139.0)
                .longitudeMax(142.0)
                .latitudeMin(39.0)
                .latitudeMax(42.0)
                .build();

        CostSearch costSearch = CostSearch.builder()
                .maxCost(30000.0)
                .minCost(10000.0)
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .costSearch(costSearch)
                .locationSearch(locationSearch)
                .guestSearch(guestSearch)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        for (Room room : roomList) {
            assertThat(room.getPeopleLimit()).isGreaterThanOrEqualTo(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
            assertThat(room.getLocation().getLatitude()).isBetween(39.0, 42.0);
            assertThat(room.getLocation().getLongitude()).isBetween(139.0, 142.0);
            assertThat(room.getRoomCost()).isGreaterThanOrEqualTo(10000.0);
            assertThat(room.getRoomCost()).isLessThanOrEqualTo(30000.0);
        }
    }
}