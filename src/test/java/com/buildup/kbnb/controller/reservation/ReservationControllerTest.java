package com.buildup.kbnb.controller.reservation;

import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.reservation.Reservation_ConfirmedResponse;
import com.buildup.kbnb.dto.reservation.Reservation_RegisterRequest;
import com.buildup.kbnb.dto.room.search.LocationSearch;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.repository.RoomImgRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.LocationService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservationService.ReservationService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
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
    @MockBean
    LocationService locationService;

    public User createUser() {
        User user = User.builder()
                .id(1L)
                .email("test@google.com").name("정한솔")
                .password("111").build();
        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));//스프링 시큐리티에서 user객체를 다루기위해 변환 시킨 것일 뿐이고
        //해당 서비스는 토큰값을 통해서 유저를 로드할 때 필터로 사용되기 때문에 정의 해줘야 함
        return user;
    }
    public Room createRoom(User user, Location location) {
        Room room = Room.builder()
                .location(location).grade(111.111).roomType("이것은 룸타입").roomCost(111.11)
                .checkOutTime(LocalTime.parse("11:11:11")).isParking(true).isSmoking(true).cleaningCost(111.111)
                .id(1L).name("this is room name").host(user).bedNum(3).build();
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

        List<BedRoom> bedRooms = new ArrayList<>(); bedRooms.add(bedRoom);
        List<BathRoom> bathRooms = new ArrayList<>(); bathRooms.add(bathRoom);

        Location location = createLocation();

        Room room = Room.builder()
                .host(user)
                .id(1L)
                .bedNum(3)
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

    public Reservation_RegisterRequest createReservation_RegisterRequest(Room room) {
        String checkIn = "2021-02-01"; String checkOut = "2021-02-02";
        return Reservation_RegisterRequest.builder()
                .totalCost(30000L)
                .roomId(room.getId())
                .message("사장님 잘생겼어요")
                .infantNumber(2)
                .guestNumber(2)
                .checkIn(LocalDate.parse(checkIn,DateTimeFormatter.ISO_DATE))
                .checkOut(LocalDate.parse(checkOut, DateTimeFormatter.ISO_DATE))
                .build();
    }
    public Reservation createReservation(Room room, Reservation_RegisterRequest reservationRegisterRequest, User user) {
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
        Location location = createLocation();
        Room room = createRoom(user, location);
        Reservation_RegisterRequest reservation_registerRequest = createReservation_RegisterRequest(room);
        Reservation reservation = createReservation(room, reservation_registerRequest, user);
        String userToken = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(reservationService.save(any())).willReturn(reservation);
        createReservation_RegisterRequest(room);
        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken)
                        .content(objectMapper.writeValueAsString(createReservation_RegisterRequest(room))))
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
                getReservationList(user, location).size());
        given(reservationService.findPageByUser(any(), any())).willReturn(reservationPage);
        given(reservationService.getHostName(any())).willReturn("this is host name");

        Map<String, String> map = new HashMap<>();
        map.put("page", "페이지 번호");
        map.put("size", "페이지의 사이즈");
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

    private List<Reservation> getReservationList(User user,Location location) {
        List<Reservation> list = new ArrayList<>();
        Room room = createRoom(user, location);
        for(int i = 0; i< 5; i ++) {
            Reservation reservation = Reservation.builder()
                    .user(user).id((long)i).room(room).
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
        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByUser(any())).willReturn(reservationList);
        given(reservationService.findById(any())).willReturn(reservation);

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
    public void deleteReservation() throws Exception {
        User user = createUser();
        List<Reservation> reservationList = new ArrayList<>();
        Room room = createRoom(user);
        Reservation reservation = createReservation(room, createReservation_RegisterRequest(room), user);
        reservationList.add(reservation);
        String token = tokenProvider.createToken(String.valueOf(user.getId()));//이거 한 순간 loadById(여기엔 user.getId만 들어가)
        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByUser(any())).willReturn(reservationList);

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