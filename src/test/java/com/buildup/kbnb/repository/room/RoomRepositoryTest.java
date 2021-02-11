package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.*;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.*;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    ReservationRepository reservationRepository;

    @BeforeEach
    public void setUpRoomList() {
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
                    .bedNum(4)
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
            Reservation reservation = Reservation.builder()
                    .checkIn(LocalDate.of(2021, 2, 1))
                    .checkOut(LocalDate.of(2021, 2, 3))
                    .room(room)
                    .build();
            reservationRepository.save(reservation);
            Reservation reservation1 = Reservation.builder()
                    .checkIn(LocalDate.of(2021, 2, 5))
                    .checkOut(LocalDate.of(2021, 2, 8))
                    .room(room)
                    .build();
            reservationRepository.save(reservation1);
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 가격 조건")
    public void getListByCost() {
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

        assertThat(roomList).isNotEmpty();
        for (Room room : roomList) {
            assertThat(room.getRoomCost()).isGreaterThanOrEqualTo(10000.0);
            assertThat(room.getRoomCost()).isLessThanOrEqualTo(30000.0);
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 위치 조건")
    public void getListByLocation() {
        LocationSearch locationSearch = LocationSearch.builder()
                .latitude(40.0)
                .longitude(140.0)
                .longitudeMin(129.0)
                .longitudeMax(132.0)
                .latitudeMin(39.0)
                .latitudeMax(42.0)
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .locationSearch(locationSearch)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        assertThat(roomList).isNotEmpty();
        for (Room room : roomList) {
            assertThat(room.getLocation().getLatitude()).isBetween(39.0, 42.0);
            assertThat(room.getLocation().getLongitude()).isBetween(129.0, 132.0);
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 게스트 수 제한 조건")
    public void getListByGuestNum() {
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

        assertThat(roomList).isNotEmpty();
        for (Room room : roomList) {
            assertThat(room.getPeopleLimit()).isGreaterThanOrEqualTo(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
        }
    }

    @Test
    @DisplayName("숙소 리스트 조건 검색 - 모든 조건")
    public void getListByAllCondition() {
        GuestSearch guestSearch = GuestSearch.builder()
                .numOfAdult(1)
                .numOfKid(0)
                .numOfInfant(0)
                .build();

        LocationSearch locationSearch = LocationSearch.builder()
                .latitude(40.0)
                .longitude(140.0)
                .longitudeMin(127.0)
                .longitudeMax(147.0)
                .latitudeMin(37.0)
                .latitudeMax(57.0)
                .build();

        CostSearch costSearch = CostSearch.builder()
                .maxCost(100000.0)
                .minCost(10000.0)
                .build();

        CheckDateSearch checkDateSearch = CheckDateSearch.builder()
                .startDate(LocalDate.of(2021, 2, 3))
                .endDate(LocalDate.of(2021, 2, 5))
                .build();

        RoomSearchCondition roomSearchCondition = RoomSearchCondition.builder()
                .costSearch(costSearch)
                .locationSearch(locationSearch)
                .guestSearch(guestSearch)
                .checkDateSearch(checkDateSearch)
                .roomType("Shared room")
                .bedNum(4)
                .bedRoomNum(2)
                .bathRoomNum(1)
                .build();

        Pageable pageable = PageRequest.of(0, 25);
        Page<Room> roomPage = roomRepository.searchByCondition(roomSearchCondition, pageable);
        List<Room> roomList = roomPage.getContent();

        assertThat(roomList).isNotEmpty();
        for (Room room : roomList) {
            assertThat(room.getPeopleLimit()).isGreaterThanOrEqualTo(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
            assertThat(room.getLocation().getLatitude()).isBetween(37.0, 77.0);
            assertThat(room.getLocation().getLongitude()).isBetween(127.0, 157.0);
            assertThat(room.getRoomCost()).isGreaterThanOrEqualTo(10000.0);
            assertThat(room.getRoomCost()).isLessThanOrEqualTo(100000.0);
        }
    }
}