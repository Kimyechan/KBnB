package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.room.search.CostSearch;
import com.buildup.kbnb.dto.room.search.GuestSearch;
import com.buildup.kbnb.dto.room.search.LocationSearch;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("locationSearch").description("위치 검색 조건").optional(),
                                fieldWithPath("locationSearch.latitude").description("설정한 위도 값").optional(),
                                fieldWithPath("locationSearch.latitudeMin").description("설정한 위도 최소 값").optional(),
                                fieldWithPath("locationSearch.latitudeMax").description("설정한 위도 최대 값").optional(),
                                fieldWithPath("locationSearch.longitude").description("설정한 경도 값").optional(),
                                fieldWithPath("locationSearch.longitudeMin").description("설정한 경도 최소 값").optional(),
                                fieldWithPath("locationSearch.longitudeMax").description("설정한 경도 최대 값").optional(),
                                fieldWithPath("checkDateSearch").description("날짜 검색 조건").optional(),
                                fieldWithPath("guestSearch").description("게스트 수 검색 조건").optional(),
                                fieldWithPath("guestSearch.numOfAdult").description("성인 수").optional(),
                                fieldWithPath("guestSearch.numOfKid").description("어린이 수").optional(),
                                fieldWithPath("guestSearch.numOfInfant").description("유아 수").optional(),
                                fieldWithPath("costSearch").description("비용 검색 조건").optional(),
                                fieldWithPath("costSearch.minCost").description("최소 비용").optional(),
                                fieldWithPath("costSearch.maxCost").description("최대 비용").optional(),
                                fieldWithPath("roomType").description("숙소 유형 검색").optional()
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
                                fieldWithPath("_embedded.roomDtoList[].latitude").description("숙소 위치 위도 값"),
                                fieldWithPath("_embedded.roomDtoList[].longitude").description("숙소 위치 경도 값"),
                                fieldWithPath("_embedded.roomDtoList[].commentCount").description("댓글 수"),
                                fieldWithPath("_embedded.roomDtoList[].isCheck").description("해당 숙소 좋아요 여부"),
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

        return RoomSearchCondition.builder()
                .locationSearch(locationSearch)
                .guestSearch(guestSearch)
                .costSearch(costSearch)
                .roomType("Shared room")
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

            Location location = Location.builder()
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
                    .commentList(List.of())
                    .build();

            roomList.add(room);
        }
        return roomList;
    }
}
