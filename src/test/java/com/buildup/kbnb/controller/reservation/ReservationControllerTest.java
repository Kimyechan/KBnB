package com.buildup.kbnb.controller.reservation;

import com.buildup.kbnb.config.RestDocsConfiguration;

import com.buildup.kbnb.dto.reservation.ReservationConfirmedResponse;
import com.buildup.kbnb.dto.reservation.ReservationDetailResponse;

import com.buildup.kbnb.dto.reservation.CancelDto;
import com.buildup.kbnb.dto.reservation.PaymentDto;

import com.buildup.kbnb.dto.reservation.ReservationRegisterRequest;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservation.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    TokenProvider tokenProvider;
    @MockBean
    UserService userService;
    @MockBean
    RoomService roomService;
    @MockBean
    ReservationService reservationService;
    @MockBean
    CustomUserDetailsService customUserDetailsService;


    public User createUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("test@google.com").name("정한솔")
                .password("111").build();
        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));

        return user;
    }

    public Room createRoom(User user, Location location) {
        Room room = Room.builder()
                .id(1L)
                .location(location)
                .grade(4.5)
                .roomType("이것은 룸타입")
                .checkOutTime(LocalTime.parse("11:11:11"))
                .isParking(true)
                .isSmoking(true)
                .roomCost(111.11)
                .cleaningCost(111.111)
                .tax(0.0)
                .name("this is room name")
                .host(user)
                .bedNum(3)
                .build();
        given(roomService.findById(any())).willReturn(room);

        return room;
    }

    public Location createLocation() {
        Location location = Location.builder().latitude(111.111).longitude(111.111).detailAddress("찾아라 비밀의")
                .neighborhood("열쇠").borough("답도없는").country("모험들").city("현실과").id(1L).build();
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

        return bedRoom;
    }

    public BathRoom createBathRoom() {
        BathRoom bathRoom = BathRoom.builder()
                .id(1L)
                .isPrivate(true)
                .build();

        return bathRoom;
    }

    public Room createRoom(User user) {
        BedRoom bedRoom = createBedRoom();
        BathRoom bathRoom = createBathRoom();

        List<BedRoom> bedRooms = new ArrayList<>();
        bedRooms.add(bedRoom);
        List<BathRoom> bathRooms = new ArrayList<>();
        bathRooms.add(bathRoom);

        Location location = createLocation();

        Room room = Room.builder()
                .host(user)
                .id(1L)
                .bedNum(3)
                .name("room name")
                .peopleLimit(2)
                .description("room description")
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

    public ReservationRegisterRequest createReservation_RegisterRequest(Room room) {
        PaymentDto payment = PaymentDto.builder()
                .receipt_id("receipt_id")
                .price((int) (room.getRoomCost() + room.getTax() + room.getTax()))
                .build();

        return ReservationRegisterRequest.builder()
                .totalCost(30000L)
                .roomId(room.getId())
                .message("사장님 잘생겼어요")
                .infantNumber(2)
                .guestNumber(2)
                .checkIn(LocalDate.of(2021, 2, 1))
                .checkOut(LocalDate.of(2021, 2, 3))
                .payment(payment)
                .build();
    }

    public Reservation createReservation(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .room(room)
                .guestNum(reservationRegisterRequest.getGuestNumber())
                .checkOut(reservationRegisterRequest.getCheckOut())
                .checkIn(reservationRegisterRequest.getCheckIn())
                .totalCost(reservationRegisterRequest.getTotalCost())
                .user(user)
                .build();
        return reservation;
    }

    @Test
    @DisplayName("예약 등록 테스트")
    public void registerReservation() throws Exception {
        User user = createUser();
        String userToken = tokenProvider.createToken(String.valueOf(user.getId()));

        Location location = createLocation();
        Room room = createRoom(user, location);
        ReservationRegisterRequest reservation_registerRequest = createReservation_RegisterRequest(room);
        Reservation reservation = createReservation(room, reservation_registerRequest, user);

        given(userService.findById(any())).willReturn(user);
        given(reservationService.saveWithPayment(any(), any())).willReturn(reservation);

        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(objectMapper.writeValueAsString(reservation_registerRequest)))
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
                                fieldWithPath("message").description("호스트에게 보내는 메시지"),
                                fieldWithPath("payment.receipt_id").description("영수증 식별자 값"),
                                fieldWithPath("payment.price").description("결제된 비용")
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
    @DisplayName("예약 리스트")
    public void getConfirmedReservationList() throws Exception {
        User user = createUser();
        Location location = createLocation();
        String userToken = tokenProvider.createToken(String.valueOf(user.getId()));
        Pageable pageable = PageRequest.of(0, 5);
        List<Reservation> reservationList = getReservationList(user, location).subList((int) pageable.getOffset(), 2);
        Page<Reservation> reservationPage = new PageImpl<>(
                reservationList,
                pageable,
                getReservationList(user, location).size()); List<ReservationConfirmedResponse> reservationConfirmedResponseList = new ArrayList<>();
        ReservationConfirmedResponse reservationConfirmedResponse = ReservationConfirmedResponse.builder().reservationId(1L).build();
        reservationConfirmedResponseList.add(reservationConfirmedResponse);

        given(reservationService.findPageByUser(any(), any())).willReturn(reservationPage);
        given(reservationService.getHostName(any())).willReturn("this is host name");

        given(reservationService.createResponseList(any())).willReturn(reservationConfirmedResponseList);

        Map<String, String> map = new HashMap<>();
        map.put("None", "None");
        mockMvc.perform(get("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
                .content(objectMapper.writeValueAsString(map))
                .param("page", String.valueOf(reservationPage.getNumber()))
                .param("size", String.valueOf(reservationPage.getSize()))
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-lookupList",
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("사이즈")
                        ),
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
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].reservationId").description("예약 식별자"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].status").description("예약 상태"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].roomName").description("방 이름"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].roomLocation").description("방 위치"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].hostName").description("호스트 이름"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].checkIn").description("체크인 날짜"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].checkOut").description("체크아웃 날짜"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].roomId").description("방 식별자"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].imgUrl").description("방 imgUrl 리스트"),
                                fieldWithPath("_embedded.reservationConfirmedResponseList[].imgUrl").description("방 imgUrl"),

                                fieldWithPath("page.size").description("페이지 사이즈"),
                                fieldWithPath("page.totalElements").description("요소의 총 개수"),
                                fieldWithPath("page.totalPages").description("총 페이지 개수"),
                                fieldWithPath("page.number").description("현재 페이지"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                        )
                ));


    }

    private List<Reservation> getReservationList(User user, Location location) {
        List<Reservation> list = new ArrayList<>();
        Room room = createRoom(user, location);
        for (int i = 0; i < 5; i++) {
            Reservation reservation = Reservation.builder()
                    .user(user).id((long) i).room(room).
                            checkIn(LocalDate.parse("2020-11-11")).checkOut(LocalDate.parse("2021-11-11")).build();
            list.add(reservation);
        }
        return list;
    }

    @Test
    @DisplayName("예약 상세 내역 보기")
    public void getDetail() throws Exception {
        User user = createUser();
        String userToken = tokenProvider.createToken(String.valueOf(user.getId()));
        Room room = createRoom(user);

        Reservation reservation = createReservation(room, createReservation_RegisterRequest(room), user);

        List<Reservation> reservationList = new ArrayList<>(); reservationList.add(reservation);
        List<ReservationConfirmedResponse> reservationConfirmedResponseList = new ArrayList<>();
        ReservationConfirmedResponse reservationConfirmedResponse = ReservationConfirmedResponse.builder().reservationId(1L).build();
        reservationConfirmedResponseList.add(reservationConfirmedResponse);
        reservationList.add(reservation);

        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByUser(any())).willReturn(reservationList);
        given(reservationService.findById(any())).willReturn(reservation);
        given(reservationService.createResponseList(any())).willReturn(reservationConfirmedResponseList);
        given(reservationService.judgeReservationIdUserHaveContainReservationId(any(), any())).willReturn(ReservationDetailResponse.builder().roomId(1L).roomName("테스트").build());

        Map<String, String> map = new HashMap<>();
        map.put("None", "None");

        mockMvc.perform(get("/reservation/detail")
                .param("reservationId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-detail",
                        requestParameters(
                                parameterWithName("reservationId").description("예약 식별자")
                        ),
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
    @DisplayName("예약 삭제")
    public void deleteReservation() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        List<Reservation> reservationList = new ArrayList<>();
        Room room = createRoom(user);
        Reservation reservation = createReservation(room, createReservation_RegisterRequest(room), user);
        reservationList.add(reservation);

        CancelDto cancelDto = CancelDto.builder()
                .reservationId(reservation.getId())
                .name(user.getName())
                .reason("test")
                .build();

        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByUser(any())).willReturn(reservationList);

        mockMvc.perform(delete("/reservation")
                .param("reservationId", String.valueOf(1L))
                .content(objectMapper.writeValueAsString(cancelDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reservation-delete",
                        requestFields(
                                fieldWithPath("reservationId").description("예약 식별자 값"),
                                fieldWithPath("name").description("유저 이름"),
                                fieldWithPath("reason").description("취소 사유")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("message").description("상세 메세지"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }
}