//package com.buildup.kbnb;
//
//import com.buildup.kbnb.model.Location;
//import com.buildup.kbnb.model.Reservation;
//import com.buildup.kbnb.model.room.Room;
//import com.buildup.kbnb.model.user.AuthProvider;
//import com.buildup.kbnb.model.user.User;
//import com.buildup.kbnb.repository.LocationRepository;
//import com.buildup.kbnb.repository.ReservationRepository;
//import com.buildup.kbnb.repository.RoomRepository;
//import com.buildup.kbnb.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Pre-load some data using a Spring Boot {@link CommandLineRunner}.
// *
// * @author Greg Turnquist
// */
//@Component
//class DatabaseLoader {
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Bean
//    CommandLineRunner init(UserRepository userRepository, LocationRepository locationRepository, RoomRepository roomRepository, ReservationRepository reservationRepository) {
//        User host = User.builder()
//                .name("host")
//                .email("host@gmail.com")
//                .password(passwordEncoder.encode("test"))
//                .provider(AuthProvider.local)
//                .emailVerified(false)
//                .build();
//        User savedHost = userRepository.save(host);
//        List<Reservation> reservationList = new ArrayList<>();
//
//        Location location = Location.builder()
//                .city("스울시")
//                .country("대한민국")
//                .borough("동대문구")
//                .neighborhood("청량리동")
//                .detailAddress("나무아비타불")
//                .latitude(123.22)
//                .longitude(111.11)
//                .build();
//
//        Room room = Room.builder()
//                .checkInTime(LocalTime.of(14,0))
//                .checkOutTime(LocalTime.of(11,0))
//                .isParking(true)
//                .isSmoking(true)
//                .cleaningCost((double) 1000)
//                .name("빵꾸똥꾸야")
//                .tax((double) 100)
//                .peopleLimit(2)
//                .description("헤으응")
//                .location(location)
//                .user(savedHost)
//                .build();
//        Reservation reservation = Reservation.builder()
//                .checkIn(LocalDate.of(2021,02,02))
//                .checkOut(LocalDate.of(20201,02,03))
//                .guestNum(2)
//                .room(room)
//                .totalCost(Double.valueOf(2000))
//                .user(host)
//                .build();
//
//        reservationList.add(reservation);
//        User user = User.builder()
//                .name("test")
//                .email("test@gmail.com")
//                .password(passwordEncoder.encode("test"))
//                .provider(AuthProvider.local)
//                .emailVerified(false)
//                .reservationList(reservationList)
//                .build();
//
//        host.setReservationList(reservationList);
//
//        User savedUser = userRepository.save(user);
//        return args -> {
//            userRepository.save(host);
//            userRepository.save(user);
//            locationRepository.save(location);
//            roomRepository.save(room);
////            reservationRepository.save(reservation);
//        };
//    }
//
//}
//
