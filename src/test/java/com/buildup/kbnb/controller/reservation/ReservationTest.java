package com.buildup.kbnb.controller.reservation;

import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.*;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.reservationService.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
public class ReservationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    ReservationRepository reservationRepository;

    @MockBean
    ReservationService reservationService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoomRepository roomRepository;

    @MockBean
    LocationRepository locationRepository;

    @MockBean
    BedRoomRepository bedRoomRepository;

    @MockBean
    BathRoomRepository bathRoomRepository;



    public User createUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .imageUrl("Image URL")
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(customUserDetailsService.loadUserById(any()))
                .willReturn(UserPrincipal.create(user));
/*        given((userRepository.findById(user.getId())))
                .willReturn(java.util.Optional.of(user));*/

        return user;
    }

    public Reservation createReservation(User user) {

        Reservation reservation = Reservation.builder()
                .id(1L)
                .totalCost(3000L)
                .checkIn(LocalDate.parse("2020-02-02"))
                .checkOut(LocalDate.parse("2020-02-03"))
                .guestNum(2)
                .user(user)
                .build();


        given(reservationService.findById(any())).willReturn(reservation);

        return reservation;
    }
    public Location createLocation() {
        Location location = Location.builder()
                .longitude(111.111)
                .latitude(111.111)
                .build();
        given(locationRepository.findById(any())).willReturn(java.util.Optional.ofNullable(location));

        return location;
    }

    public BedRoom createBedRoom() {
        BedRoom bedRoom = BedRoom.builder()
                .doubleSize(2)
                .queenSize(0)
                .singleSize(0)
                .superSingleSize(0)
                .id(1L)
                .build();
        given(bedRoomRepository.findById(any())).willReturn(java.util.Optional.ofNullable(bedRoom));

        return bedRoom;
    }
    public BathRoom createBathRoom() {
        BathRoom bathRoom = BathRoom.builder()
                .id(1L)
                .isPrivate(true)
                .build();
        given(bathRoomRepository.findById(any())).willReturn(java.util.Optional.ofNullable(bathRoom));

        return bathRoom;
    }

    public Room createRoom() {
        BedRoom bedRoom = createBedRoom();
        BathRoom bathRoom = createBathRoom();

        List<BedRoom> bedRooms = new ArrayList<>(); bedRooms.add(bedRoom);
        List<BathRoom> bathRooms = new ArrayList<>(); bathRooms.add(bathRoom);

        Location location = createLocation();

        Room room = Room.builder()
                .host(userRepository.findById((long) 1).orElseThrow(ReservationException::new))
                .id(1L)
                .name("사쿠라여?")
                .peopleLimit(2)
                .description("사쿠라네?")
                .tax(200.0)
                .cleaningCost(200.0)
                .isSmoking(true)
                .isParking(true)
                .checkOutTime(LocalTime.parse("02:02:02"))
                .checkOutTime(LocalTime.parse("02:02:03"))
                .bathRoomList(bathRooms)
                .bedRoomList(bedRooms)
                .roomCost(2000.0)
                .roomType("확신이 없으면")
                .grade(4.0)
                .location(location)
                .build();
        return room;
    }

    @Test
    public void detailTest() throws Exception {
        User user = createUser();
        given(userRepository.findById(any())).willReturn(java.util.Optional.ofNullable(user));
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        Reservation reservation = createReservation(user);
        List<Reservation> reservationList = new ArrayList<>(); reservationList.add(reservation);
        given(reservationRepository.findByUserId(any())).willReturn(reservationList);
        reservation.setRoom(createRoom());

        Map<String, String> map = new HashMap<>();
        map.put("None", "None");
        mockMvc.perform(get("/reservation/detail")
                .param("reservationId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-detail",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("None").description("없음")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("roomId").description("방 식별자"),
                                fieldWithPath("roomImage").description("이미지 URL"),
                                fieldWithPath("hostName").description("호스트 이름"),
                                fieldWithPath("hostImage").description("호스트 이미지 URL"),
                                fieldWithPath("totalCost").description("총 가격"),
                                fieldWithPath("address").description("주소"),
                                fieldWithPath("latitude").description("위도"),
                                fieldWithPath("longitude").description("경도"),
                                fieldWithPath("bedRoomNum").description("침실 수"),
                                fieldWithPath("bathRoomNum").description("욕실 수"),
                                fieldWithPath("checkIn").description("체크 인 날짜"),
                                fieldWithPath("checkOut").description("체크 아웃 날짜"),
                                fieldWithPath("smoking").description("흡연 가능 여부"),
                                fieldWithPath("parking").description("주차 가능 여부"),
                                fieldWithPath("guestNum").description("예약된 인원 수"),
                                fieldWithPath("roomName").description("방 이름"),
                                fieldWithPath("bedNum").description("침대 수"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                        ));
    }

    @Test
    public void test() throws  Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        mockMvc.perform(get("/reservation/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print());
    //현재 로그인된 유저는 토큰값을 통해 userRepository.findbyid(customUserservice)해서
        //찾게 되어있어. 그런데 mock을 쓰면 userRepository가 없으니까 customUserservice.loadById를
        //정의해주는거야 그러면 쟤가 현재 유저가 되 그니까 userRepository.findById해도 되는거지

    }

    @Test
    public void deleteReservation() throws Exception {
        User user = createUser();
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(createReservation(user));
        String token = tokenProvider.createToken(String.valueOf(user.getId()));//이거 한 순간 loadById(여기엔 user.getId만 들어가)
        given(userRepository.findById(any())).willReturn(java.util.Optional.of(user));
        given(reservationRepository.findByUserId(any())).willReturn(reservationList);

        Map<String, String> map = new HashMap<>();
        map.put("None", "None");
        mockMvc.perform(delete("/reservation")
                .param("reservationId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("None").description("없음")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
        }
    }
