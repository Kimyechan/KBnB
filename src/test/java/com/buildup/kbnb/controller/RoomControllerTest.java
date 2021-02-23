package com.buildup.kbnb.controller;


import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.room.BathRoomDto;
import com.buildup.kbnb.dto.room.BedRoomDto;
import com.buildup.kbnb.dto.room.CreateRoomRequestDto;
import com.buildup.kbnb.dto.room.check.CheckRoomReq;
import com.buildup.kbnb.dto.room.detail.ReservationDate;
import com.buildup.kbnb.dto.room.search.*;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserRoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservation.ReservationService;
import com.buildup.kbnb.util.S3Uploader;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
class RoomControllerTest {
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
    RoomService roomService;

    @MockBean
    UserService userService;

    @MockBean
    CommentService commentService;

    @MockBean
    UserRoomService userRoomService;

    @MockBean
    ReservationService reservationService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    S3Uploader s3Uploader;

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

        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));

        return user;
    }

    @Test
    @DisplayName("숙소 리스트 검색 - 필터 추가")
    public void getListByCondition() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        RoomSearchCondition roomSearchCondition = getRoomSearchCondition();
        Pageable pageable = PageRequest.of(1, 5);
        List<Room> roomList = getRoomList().subList((int) pageable.getOffset(), (int) (pageable.getOffset() + pageable.getPageSize()));
        Page<Room> roomPage = new PageImpl<>(
                roomList,
                pageable,
                getRoomList().size());

        given(roomService.searchListByCondition(any(), any())).willReturn(roomPage);
        given(roomService.getBedNum(any())).willReturn(2);
        given(userService.checkRoomByUser(eq(user.getId()), any())).willReturn(false);

        mockMvc.perform(post("/room/list")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(roomSearchCondition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("room-get-roomList-by-condition",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰 | 없어도 가능").optional(),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("locationSearch").description("위치 검색 조건"),
                                fieldWithPath("locationSearch.latitude").description("설정한 위도 값"),
                                fieldWithPath("locationSearch.latitudeMin").description("설정한 위도 최소 값"),
                                fieldWithPath("locationSearch.latitudeMax").description("설정한 위도 최대 값"),
                                fieldWithPath("locationSearch.longitude").description("설정한 경도 값"),
                                fieldWithPath("locationSearch.longitudeMin").description("설정한 경도 최소 값"),
                                fieldWithPath("locationSearch.longitudeMax").description("설정한 경도 최대 값"),
                                fieldWithPath("checkDateSearch").description("날짜 검색 조건").optional(),
                                fieldWithPath("checkDateSearch.startDate").description("체크 인 날짜"),
                                fieldWithPath("checkDateSearch.endDate").description("체크 아웃 날짜"),
                                fieldWithPath("guestSearch").description("게스트 수 검색 조건").optional(),
                                fieldWithPath("guestSearch.numOfAdult").description("성인 수"),
                                fieldWithPath("guestSearch.numOfKid").description("어린이 수"),
                                fieldWithPath("guestSearch.numOfInfant").description("유아 수"),
                                fieldWithPath("costSearch").description("비용 검색 조건").optional(),
                                fieldWithPath("costSearch.minCost").description("최소 비용"),
                                fieldWithPath("costSearch.maxCost").description("최대 비용"),
                                fieldWithPath("roomType").description("숙소 유형 검색").optional(),
                                fieldWithPath("bedNum").description("침대 수 조건").optional(),
                                fieldWithPath("bedRoomNum").description("침실 수 조건").optional(),
                                fieldWithPath("bathRoomNum").description("욕실 수 조건").optional()
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.roomDtoList").description("숙소 리스트"),
                                fieldWithPath("_embedded.roomDtoList[].id").description("숙소 식별자 값"),
                                fieldWithPath("_embedded.roomDtoList[].name").description("숙소 이름"),
                                fieldWithPath("_embedded.roomDtoList[].peopleLimit").description("게스트 제한 인원 수"),
                                fieldWithPath("_embedded.roomDtoList[].bedRoomNum").description("침실 수"),
                                fieldWithPath("_embedded.roomDtoList[].bedNum").description("침대 수"),
                                fieldWithPath("_embedded.roomDtoList[].bathRoomNum").description("욕실 수"),
                                fieldWithPath("_embedded.roomDtoList[].checkInTime").description("체크 인 시간"),
                                fieldWithPath("_embedded.roomDtoList[].checkOutTime").description("체크 아웃 시간"),
                                fieldWithPath("_embedded.roomDtoList[].isSmoking").description("흡연 가능 여부"),
                                fieldWithPath("_embedded.roomDtoList[].isParking").description("주차 가능 여부"),
                                fieldWithPath("_embedded.roomDtoList[].roomType").description("숙소 유형"),
                                fieldWithPath("_embedded.roomDtoList[].cost").description("숙소 비용"),
                                fieldWithPath("_embedded.roomDtoList[].grade").description("숙소 평점"),
                                fieldWithPath("_embedded.roomDtoList[].city").description("숙소 위치 도시"),
                                fieldWithPath("_embedded.roomDtoList[].borough").description("숙소 위치 구"),
                                fieldWithPath("_embedded.roomDtoList[].neighborhood").description("숙소 위치 동"),
                                fieldWithPath("_embedded.roomDtoList[].latitude").description("숙소 위치 위도 값"),
                                fieldWithPath("_embedded.roomDtoList[].longitude").description("숙소 위치 경도 값"),
                                fieldWithPath("_embedded.roomDtoList[].commentCount").description("댓글 수"),
                                fieldWithPath("_embedded.roomDtoList[].isCheck").description("해당 숙소 좋아요 여부"),
                                fieldWithPath("_embedded.roomDtoList[].roomImgUrlList[]").description("숙소 사진 리스트"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL"),
                                fieldWithPath("_links.first.href").description("첫번째 페이지 URL"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 URL"),
                                fieldWithPath("_links.self.href").description("현재 페이지 URL"),
                                fieldWithPath("_links.next.href").description("이후 페이지 URL"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 URL"),
                                fieldWithPath("page.size").description("한 페이지 당 크기"),
                                fieldWithPath("page.totalElements").description("전체 숙소 개수"),
                                fieldWithPath("page.totalPages").description("전체 페이지 개수"),
                                fieldWithPath("page.number").description("현재 페이지 번호")
                        )
                ));
    }

    private RoomSearchCondition getRoomSearchCondition() {
        LocationSearch locationSearch = LocationSearch.builder()
                .latitude(10.0)
                .longitude(10.0)
                .latitudeMax(12.0)
                .latitudeMin(8.0)
                .longitudeMax(12.0)
                .longitudeMin(8.0)
                .build();

        GuestSearch guestSearch = GuestSearch.builder()
                .numOfAdult(2)
                .numOfKid(1)
                .numOfInfant(1)
                .build();

        CostSearch costSearch = CostSearch.builder()
                .minCost(1000.0)
                .maxCost(100000.0)
                .build();

        CheckDateSearch checkDateSearch = CheckDateSearch.builder()
                .startDate(LocalDate.of(2021, 2, 10))
                .endDate(LocalDate.of(2021, 2, 13))
                .build();

        return RoomSearchCondition.builder()
                .locationSearch(locationSearch)
                .guestSearch(guestSearch)
                .costSearch(costSearch)
                .checkDateSearch(checkDateSearch)
                .roomType("Shared room")
                .bedNum(4)
                .bedRoomNum(2)
                .bathRoomNum(1)
                .build();
    }

    private List<Room> getRoomList() {
        List<Room> roomList = new ArrayList<>();

        for (long i = 1; i <= 25; i++) {
            List<BathRoom> bathRooms = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                BathRoom bathRoom = BathRoom.builder()
                        .isPrivate(true)
                        .build();
                bathRooms.add(bathRoom);
            }

            List<BedRoom> bedRooms = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                BedRoom bedRoom = BedRoom.builder()
                        .doubleSize(2)
                        .queenSize(0)
                        .singleSize(0)
                        .superSingleSize(0)
                        .build();
                bedRooms.add(bedRoom);
            }

            List<RoomImg> roomImgList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                RoomImg roomImg = RoomImg.builder()
                        .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-05T22%3A49%3A59.421617.png")
                        .build();
                roomImgList.add(roomImg);
            }

            Location location = Location.builder()
                    .city("test city" + i)
                    .borough("test borough" + i)
                    .neighborhood("test neighborhood" + i)
                    .latitude(37.0)
                    .longitude(138.0)
                    .build();

            Room room = Room.builder()
                    .id(i)
                    .name("test room " + i)
                    .peopleLimit(4)
                    .roomCost(5000.0 * i)
                    .roomType("Shared room")
                    .checkInTime(LocalTime.of(15, 0))
                    .checkOutTime(LocalTime.of(12, 0))
                    .cleaningCost(5000.0)
                    .isParking(false)
                    .isSmoking(false)
                    .grade(4.5)
                    .location(location)
                    .bathRoomList(bathRooms)
                    .bedRoomList(bedRooms)
                    .roomImgList(roomImgList)
                    .commentList(List.of())
                    .build();

            roomList.add(room);
        }
        return roomList;
    }

    @Test
    @DisplayName("숙소 상세 검색")
    public void getRoomDetail() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        User host = getHost();
        Location location = getLocation();

        List<RoomImg> roomImgList = getRoomImgList();
        List<BathRoom> bathRoomList = getBathRoomList();
        List<BedRoom> bedRoomList = getBedRoomList();

        Room room = getRoom(host, location, roomImgList, bathRoomList, bedRoomList);

        Pageable pageable = PageRequest.of(0, 6);
        Page<Comment> commentPage = getCommentPages(pageable);

        List<ReservationDate> reservationDates = new ArrayList<>();
        ReservationDate reservationDate = ReservationDate.builder()
                .checkIn(LocalDate.of(2021, 2, 20))
                .checkOut(LocalDate.of(2021, 2, 22))
                .build();
        reservationDates.add(reservationDate);

        given(roomService.getBedNum(any())).willReturn(2);
        given(userService.checkRoomByUser(any(), any())).willReturn(false);
        given(roomService.getRoomDetailById(room.getId())).willReturn(room);
        given(commentService.getListByRoomIdWithUser(room, pageable)).willReturn(commentPage);
        given(reservationService.findByRoomFilterDay(room.getId(), LocalDate.now())).willReturn(reservationDates);

        mockMvc.perform(get("/room/detail")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .param("roomId", String.valueOf(room.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("room-get-detail",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰 | 없어도 가능").optional(),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestParameters(
                                parameterWithName("roomId").description("숙소 식별자 값")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("숙소 식별자 값"),
                                fieldWithPath("name").description("숙소 이름"),
                                fieldWithPath("roomType").description("숙소 유형"),
                                fieldWithPath("roomCost").description("1일 숙박 비용"),
                                fieldWithPath("cleaningCost").description("청소비"),
                                fieldWithPath("tax").description("세금"),
                                fieldWithPath("peopleLimit").description("제한 인원 수"),
                                fieldWithPath("description").description("숙서 설명"),
                                fieldWithPath("checkInTime").description("체크 인 시간"),
                                fieldWithPath("checkOutTime").description("체크 아웃 시간"),
                                fieldWithPath("isSmoking").description("흡연 가능 여부"),
                                fieldWithPath("isParking").description("주차 가능 여부"),
                                fieldWithPath("bedRoomNum").description("침실 수"),
                                fieldWithPath("bedNum").description("침대 수"),
                                fieldWithPath("bathRoomNum").description("욕실 수"),
                                fieldWithPath("grade").description("숙소 평점"),
                                fieldWithPath("commentCount").description("댓글 수"),
                                fieldWithPath("hostName").description("호스트 이름"),
                                fieldWithPath("hostImgURL").description("호스트 이미지 URL"),
                                fieldWithPath("locationDetail.country").description("지정 위치 국가 이름"),
                                fieldWithPath("locationDetail.city").description("지정 위치 도시 이름"),
                                fieldWithPath("locationDetail.borough").description("지정 위치 자치구 이름"),
                                fieldWithPath("locationDetail.neighborhood").description("지정 위치 동"),
                                fieldWithPath("locationDetail.detailAddress").description("지정 위치 세부 주소"),
                                fieldWithPath("locationDetail.latitude").description("지정 위치 위도"),
                                fieldWithPath("locationDetail.longitude").description("지정 위치 경도"),
                                fieldWithPath("roomImgUrlList[]").description("숙소 이미지 리스트"),
                                fieldWithPath("reservationDates").description("예약 기간 리스트"),
                                fieldWithPath("reservationDates[].checkIn").description("예약 체크인 날짜"),
                                fieldWithPath("reservationDates[].checkOut").description("예약 체크아웃 날짜"),
                                fieldWithPath("commentList[].id").description("댓글 식별자 값"),
                                fieldWithPath("commentList[].description").description("댓글 상세 설명"),
                                fieldWithPath("commentList[].userName").description("해당 댓글 유저"),
                                fieldWithPath("commentList[].date").description("댓글 작성 날짜"),
                                fieldWithPath("commentList[].userImgUrl").description("해당 댓글 유저 이미지 url"),
                                fieldWithPath("isChecked").description("검색한 유저 숙소 체크 여부"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    private Room getRoom(User host, Location location, List<RoomImg> roomImgList, List<BathRoom> bathRoomList, List<BedRoom> bedRoomList) {
        return Room.builder()
                .id(1L)
                .name("test room name")
                .roomType("Shared room")
                .roomCost(30000.0)
                .cleaningCost(5000.0)
                .tax(3000.0)
                .peopleLimit(3)
                .description("test room description")
                .checkInTime(LocalTime.of(15, 0))
                .checkOutTime(LocalTime.of(12, 0))
                .isSmoking(false)
                .isParking(true)
                .grade(4.6)
                .host(host)
                .location(location)
                .roomImgList(roomImgList)
                .bedRoomList(bedRoomList)
                .bathRoomList(bathRoomList)
                .build();
    }

    private Location getLocation() {
        return Location.builder()
                .country("Korea")
                .city("Seoul")
                .borough("성동구")
                .neighborhood("성수동")
                .detailAddress("성수2가3동 289-10 제강 빌딩 8층")
                .latitude(37.0)
                .longitude(137.0)
                .build();
    }

    private User getHost() {
        return User.builder()
                .id(2L)
                .name("test host")
                .birth(LocalDate.of(1999, 7, 18))
                .email("host@gmail.com")
                .password(passwordEncoder.encode("host"))
                .imageUrl("Image URL")
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();
    }

    private Page<Comment> getCommentPages(Pageable pageable) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .name("comment user" + i)
                    .imageUrl("user image url" + i)
                    .build();

            Comment comment = Comment.builder()
                    .id((long) i)
                    .user(user)
                    .date(LocalDate.of(2021, 3, 12))
                    .description("comment description" + i)
                    .build();
            comments.add(comment);
        }
        return new PageImpl<>(comments.subList((int) pageable.getOffset(), (int) pageable.getOffset() + pageable.getPageSize()),
                pageable,
                comments.size());
    }

    private List<BathRoom> getBathRoomList() {
        List<BathRoom> bathRoomList = new ArrayList<>();

        BathRoom bathRoom = BathRoom.builder()
                .isPrivate(true)
                .build();

        bathRoomList.add(bathRoom);
        return bathRoomList;
    }

    private List<BedRoom> getBedRoomList() {
        List<BedRoom> bedRoomList = new ArrayList<>();

        BedRoom bedRoom = BedRoom.builder()
                .doubleSize(2)
                .build();

        bedRoomList.add(bedRoom);
        return bedRoomList;
    }

    private List<RoomImg> getRoomImgList() {
        List<RoomImg> roomImgList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            RoomImg roomImg = RoomImg.builder()
                    .url("room img url" + i)
                    .build();

            roomImgList.add(roomImg);
        }
        return roomImgList;
    }
    private CreateRoomRequestDto createRoomRequestDtoList() {
        List<BathRoomDto> bathRoomDtoList = new ArrayList<>(); bathRoomDtoList.add(new BathRoomDto());
        List<BedRoomDto> bedRoomDtoList = new ArrayList<>(); bedRoomDtoList.add(new BedRoomDto());
        return CreateRoomRequestDto.builder()
                .bathRoomDtoList(bathRoomDtoList)
                .bedRoomDtoList(bedRoomDtoList)
                .checkInTime(LocalTime.now())
                .checkOutTime(LocalTime.now())
                .cleaningCost(1.0)
                .isParking(true)
                .isSmoking(true)
                .name("방이름")
                .peopleLimit(2)
                .roomCost(2000.0)
                .roomType("룸타입")
                .tax(1.0)
                .description("설명 설명")
                .build();
    }

    @Test
    @DisplayName("숙소 찜하기")
    public void setRoomChecked() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        CheckRoomReq req = CheckRoomReq.builder()
                .roomId(1L)
                .build();

        given(userRoomService.checkRoomForUser(req.getRoomId(), user.getId())).willReturn(true);

        mockMvc.perform(patch("/room/check")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("room-check",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰").optional(),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("roomId").description("찜하거나 찜 취소할 숙소 식별자 값")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("roomId").description("찜하거나 찜 취소한 숙소 식별자 값"),
                                fieldWithPath("isChecked").description("찜하기 여부"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("호스트의 방 등록")
    public void hostRegisterRoom() throws Exception {
        User user = createUser();
        Room room = Room.builder().name("테스트").id(1L).build();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(roomService.createRoom(any(), any())).willReturn(room);
        given(roomService.save(any())).willReturn(room);

        CreateRoomRequestDto req = createRoomRequestDtoList();

        mockMvc.perform(post("/host/registerBasicRoom")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("host-registerBasicRoom",
                        requestFields(
                                fieldWithPath("name").description("방이름"),
                                fieldWithPath("roomType").description("룸타입"),
                                fieldWithPath("roomCost").description("방비용"),
                                fieldWithPath("cleaningCost").description("청소비용"),
                                fieldWithPath("tax").description("세금"),
                                fieldWithPath("peopleLimit").description("인원 제한"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("checkOutTime").description("체크아웃 시간"),
                                fieldWithPath("checkInTime").description("체크인 시간"),
                                fieldWithPath("isSmoking").description("흡연 가능 여부"),
                                fieldWithPath("isParking").description("주차 가능 여부"),
                                fieldWithPath("country").description("나라"),
                                fieldWithPath("city").description("시"),
                                fieldWithPath("borough").description("구"),
                                fieldWithPath("neighborhood").description("동"),
                                fieldWithPath("detailAddress").description("상세 주소"),
                                fieldWithPath("latitude").description("위도"),
                                fieldWithPath("longitude").description("경도"),
                                fieldWithPath("bedRoomDtoList[].queenSize").description("침실 리스트 퀸사이즈"),
                                fieldWithPath("bedRoomDtoList[].doubleSize").description("침실 리스트 더블사이즈"),
                                fieldWithPath("bedRoomDtoList[].singleSize").description("침실 리스트 싱글사이즈"),
                                fieldWithPath("bedRoomDtoList[].superSingleSize").description("침실 리스트 슈퍼싱글 사이즈"),
                                fieldWithPath("bathRoomDtoList[].isPrivate").description("개인 욕실 여부")
                        ),
                        responseFields(
                                fieldWithPath("msg").description("방 등록 여부"),
                                fieldWithPath("roomId").description("방 식별자"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 주소")
                        )
                ));
    }

    @Test
    @DisplayName("호스트의 방 등록 실패")
    public void hostRegisterRoomFail() throws Exception {
        User user = createUser();
        Room room = Room.builder().name("테스트").id(1L).build();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(roomService.createRoom(any(), any())).willReturn(room);
        given(roomService.save(any())).willReturn(room);
        CreateRoomRequestDto req = new CreateRoomRequestDto(); req.setName("테스트");



        mockMvc.perform(post("/host/registerBasicRoom")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("exception-hostRegisterRoom",
                        responseFields(
                                fieldWithPath("success").description("성공 실패 여부"),
                                fieldWithPath("code").description("exception 코드 번호"),
                                fieldWithPath("msg").description("exception 메시지")
                        )
                ));
    }
    @Test
    @DisplayName("방 등록_사진 추가")
    public void addPhoto() throws Exception {
        User user = createUser();
        Room room = Room.builder().name("테스트방").build();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        given(userService.findById(any())).willReturn(user);
        given(roomService.findById(any())).willReturn(room);
        given(s3Uploader.upload(any(), any(), any())).willReturn("test url");
        given(roomService.save(any())).willReturn(room);

        mockMvc.perform(fileUpload("/host/addPhoto")
                .file("file", "example".getBytes())
                .file("file", "example2".getBytes())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .param("roomId", String.valueOf(1L))
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("host-addPhoto",
                        requestParts(
                                partWithName("file").description("업로드될 파일 리스트")
                        ),
                        requestParameters(
                                parameterWithName("roomId").description("방 식별자")
                        ),
                        responseFields(
                                fieldWithPath("imgCount").description("등록된 사진의 갯수"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 주소")
                        )
                        )

                );
    }

    @Test
    @DisplayName("숙소 지난달 예약률로 숙소 추천")
    public void recommendRoom() throws Exception {
        Long roomId = 1L;

        given(reservationService.checkRecommendedRoom(roomId)).willReturn(true);

        mockMvc.perform(get("/room/recommend")
                .param("roomId", String.valueOf(roomId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("room-recommend",
                        requestParameters(
                                parameterWithName("roomId").description("숙소 식별자 값")
                        ),
                        responseFields(
                                fieldWithPath("isRecommendedRoom").description("숙소 추천 여부"),
                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 URL")
                        )
                ));
    }
}