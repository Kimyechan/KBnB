/*
package com.buildup.kbnb;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.BathRoomRepository;
import com.buildup.kbnb.repository.BedRoomRepository;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * Pre-load some data using a Spring Boot {@link CommandLineRunner}.
 *
 * @author Greg Turnquist
 *//*

@Component
class DatabaseLoader {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init(UserRepository userRepository, LocationRepository locationRepository, RoomRepository roomRepository, ReservationRepository reservationRepository, BedRoomRepository bedRoomRepository, BathRoomRepository bathRoomRepository) {
        User host = User.builder()
                .name("host")
                .email("host@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

       */
/* Location location = Location.builder()
                .city("스울시")
                .country("대한민국")
                .borough("동대문구")
                .neighborhood("청량리동")
                .detailAddress("나무아비타불")
                .latitude(123.22)
                .longitude(111.11)
                .build();

        Room room = Room.builder()
                .checkInTime(LocalTime.of(14,0))
                .checkOutTime(LocalTime.of(11,0))
                .isParking(true)
                .isSmoking(true)
                .cleaningCost((double) 1000)
                .name("빵꾸똥꾸야")
                .tax((double) 100)
                .peopleLimit(2)
                .description("헤으응")
                .location(location)
                .host(savedHost)
                .build();

        Reservation reservation = Reservation.builder()
                .checkIn(LocalDate.of(2021,02,02))
                .checkOut(LocalDate.of(20201,02,03))
                .guestNum(2)
                .room(room)
                .user(host)
                .totalCost(2000L)
                .user(host)
                .build();

*//*

        Location location = Location.builder()
                .city("스울시")
                .country("대한민국")
                .borough("동대문구")
                .neighborhood("청량리동")
                .detailAddress("나무아비타불")
                .latitude(123.22)
                .longitude(111.11)
                .build();
        Room room = Room.builder()
                .checkInTime(LocalTime.of(14,0))
                .checkOutTime(LocalTime.of(11,0))
                .isParking(true)
                .isSmoking(true)
                .cleaningCost((double) 1000)
                .name("빵꾸똥꾸야")
                .tax((double) 100)
                .peopleLimit(2)
                .description("헤으응")
                .location(location)
                .host(host)
                .build();
        BedRoom bedRoom = BedRoom.builder().room(room).superSingleSize(1).doubleSize(1).queenSize(1).singleSize(1).build();
        BathRoom bathRoom = BathRoom.builder().room(room).isPrivate(true).build();
       */
/* List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);
        User user = User.builder()
                .name("test")
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .reservationList(reservationList)
                .build();

        host.setReservationList(reservationList);*//*


        return args -> {

    */
/*        userRepository.save(user);
            locationRepository.save(location);
            roomRepository.save(room);
            reservationRepository.save(reservation);*//*

            bedRoomRepository.save(bedRoom);
            bathRoomRepository.save(bathRoom);
        };
    }

}

*/
