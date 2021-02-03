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
        Pageable pageable = PageRequest.of(0, 5);
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
                .andExpect(status().isOk());
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
                    .isSmoking(false )
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