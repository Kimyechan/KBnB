package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.LoginRequest;
import com.buildup.kbnb.dto.ReservationRequest;
import com.buildup.kbnb.dto.SignUpRequest;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.RoomRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @MockBean
    RoomRepository roomRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Mock
    Room room;
    @Mock
    Reservation reservation;
/*@Before("필터 추가")
public void filter() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
            .alwaysDo(print())
            .build();
}*/

    @Test
    public void getReservationList() throws Exception {

        User user = User.builder()
                .name("test")
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();

        given(roomRepository.findById(1L)).willReturn(java.util.Optional.of(room));

        String checkIn = "2021-02-01"; String checkOut = "2021-02-02";
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .totalCost(30000)
                .roomId(1L)
                .message("사장님 잘생겼어요")
                .infantNumber(2)
                .guestNumber(2)
                .checkIn(LocalDate.parse(checkIn,DateTimeFormatter.ISO_DATE))
                .checkOut(LocalDate.parse(checkOut, DateTimeFormatter.ISO_DATE))
                .build();
        User savedUser = userRepository.save(user);

        String userToken = tokenProvider.createToken(savedUser.getId().toString());

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
    public void getConfirmedReservationList() throws Exception {
        //유저 필요하고 유저 findBYid
        //룸 필요하고 룸 findBYid
        User host = User.builder()
                .name("host")
                .email("host@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();
        User savedHost = userRepository.save(host);
        List<Reservation> reservationList = new ArrayList<>();

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
                .user(savedHost)
                .build();
        Reservation reservation = Reservation.builder()
                .checkIn(LocalDate.of(2021,02,02))
                .checkOut(LocalDate.of(20201,02,03))
                .guestNum(2)
                .room(room)
                .totalCost(Double.valueOf(2000))
                .user(host)
                .build();

        reservationList.add(reservation);
        User user = User.builder()
                .name("test")
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .provider(AuthProvider.local)
                .emailVerified(false)
                .reservationList(reservationList)
                .build();

        User savedUser = userRepository.save(user);
        String userToken = tokenProvider.createToken(savedUser.getId().toString());

        reservationRepository.save(reservation);

        Map<String, String> map = new HashMap<>();
        map.put("NONE", "NONE");
        mockMvc.perform(get("/reservation")
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
                        fieldWithPath("NONE").description("NONE")
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
                        fieldWithPath("_links.self.href").description("해당 API URL"),
                        fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                )
        ));

    }

}