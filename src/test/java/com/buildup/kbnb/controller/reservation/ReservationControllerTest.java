package com.buildup.kbnb.controller.reservation;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.reservation.ReservationRequest;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.RoomImgRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
class ReservationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    RoomImgRepository roomImgRepository;



    @Autowired
    private LocationRepository locationRepository;

    @Test
    @Transactional
    public void register_Reservation() throws Exception {
        User host = User.builder()
                .name("host")
                .email("host1@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();
        userRepository.save(host);
        Location location = Location.builder()
                .city("스울시")
                .country("대한민국")
                .borough("동대문구")
                .neighborhood("청량리동")
                .detailAddress("나무아비타불")
                .latitude(123.22)
                .longitude(111.11)
                .build();
        locationRepository.save(location);
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
        roomRepository.save(room);

        String userToken = tokenProvider.createToken(host.getId().toString());

        Reservation reservation = Reservation.builder()
                .checkIn(LocalDate.of(2021,02,02))
                .checkOut(LocalDate.of(20201,02,03))
                .guestNum(2)
                .room(room)
                .totalCost(2000L)
                .user(host)
                .build();
        reservationRepository.save(reservation);
        String checkIn = "2021-02-01"; String checkOut = "2021-02-02";
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .totalCost(30000)
                .roomId(room.getId())
                .message("사장님 잘생겼어요")
                .infantNumber(2)
                .guestNumber(2)
                .checkIn(LocalDate.parse(checkIn,DateTimeFormatter.ISO_DATE))
                .checkOut(LocalDate.parse(checkOut, DateTimeFormatter.ISO_DATE))
                .build();

        List<Room> roomList = roomRepository.findAll();

        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(objectMapper.writeValueAsString(reservationRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("reservation-register",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("roomId").description("방 식별자"),
                                fieldWithPath("checkIn").description("체크인 날짜"),
                                fieldWithPath("checkOut").description("체크 아웃 날짜"),
                                fieldWithPath("guestNumber").description("게스트 수"),
                                fieldWithPath("infantNumber").description("유아 수"),
                                fieldWithPath("totalCost").description("총 금액"),
                                fieldWithPath("message").description("호스트에게 보내는 메시지")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("reservationId").description("예약 식별자"),
                                fieldWithPath("message").description("예약 여부 메시지"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                        )
                        ));
    }

    @Test
    @Transactional
    public void getConfirmedReservationList() throws Exception  {
        User host = User.builder()
                .name("host")
                .email("host1@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();
        userRepository.save(host);
        Location location = Location.builder()
                .city("스울시")
                .country("대한민국")
                .borough("동대문구")
                .neighborhood("청량리동")
                .detailAddress("나무아비타불")
                .latitude(123.22)
                .longitude(111.11)
                .build();
        locationRepository.save(location);
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
        roomRepository.save(room);

        String userToken = tokenProvider.createToken(host.getId().toString());

        Reservation reservation = Reservation.builder()
                .checkIn(LocalDate.of(2021,02,02))
                .checkOut(LocalDate.of(20201,02,03))
                .guestNum(2)
                .room(room)
                .totalCost(2000L)
                .user(host)
                .build();
        reservationRepository.save(reservation);

        roomImgRepository.save(RoomImg.builder().room(room).url("this is demo url").build());
        roomImgRepository.save(RoomImg.builder().room(room).url("this is demo too").build());
        Map<String, String> map = new HashMap<>();
        map.put("page", "페이지 번호");
        map.put("size", "페이지의 사이즈");
        Pageable pageable = PageRequest.of(0, 5);
        mockMvc.perform(get("/reservation")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(objectMapper.writeValueAsString(map))
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-lookupList",
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                ),
                requestFields(
                        fieldWithPath("page").description("페이지 번호"),
                        fieldWithPath("size").description("페이지 요소 갯수")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                ),
                responseFields(
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].reservationId").description("예약 식별자"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].status").description("예약 상태"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].roomName").description("방 이름"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].roomLocation").description("방 위치"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].hostName").description("호스트 이름"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].checkIn").description("체크인 날짜"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].checkOut").description("체크아웃 날짜"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].roomId").description("방 식별자"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].imgUrl").description("방 imgUrl 리스트"),
                        fieldWithPath("_embedded.reservation_ConfirmedResponseList[].imgUrl").description("방 imgUrl"),


                        fieldWithPath("page.size").description("페이지 사이즈"),
                        fieldWithPath("page.totalElements").description("요소의 총 개수"),
                        fieldWithPath("page.totalPages").description("총 페이지 개수"),
                        fieldWithPath("page.number").description("현재 페이지"),
                        fieldWithPath("_links.self.href").description("해당 API URL"),
                        fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                )
        ));

    }

}