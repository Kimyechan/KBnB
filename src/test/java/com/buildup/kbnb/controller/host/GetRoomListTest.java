package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
public class GetRoomListTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    UserService userService;

    @MockBean
    ReservationService reservationService;

    @MockBean
    RoomService roomService;

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
    public List<Room> createRoomList() {
        Location location = new Location();
        List<BathRoom> bathRoomList = new ArrayList<>();
        BathRoom bathRoom = new BathRoom();
        bathRoomList.add(bathRoom);
        List<BedRoom> bedRoomList = new ArrayList<>();
        RoomImg roomImg = RoomImg.builder()
                .url("test url")
                .build();
        List<RoomImg> roomImgList = new ArrayList<>();
        roomImgList.add(roomImg);
        bedRoomList.add(new BedRoom());
        List<Room> list = new ArrayList<>();
        Room room = Room.builder()
                .bedRoomList(bedRoomList)
                .location(location)
                .roomImgList(roomImgList)
                .bedNum(3)
                .bathRoomList(bathRoomList)
                .build();
        list.add(room);
        return list;
    }


    @Test
    @DisplayName("수입 테스트 통과")
    public void incomeTest() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        Pageable pageable = PageRequest.of(0, 5);
        List<Room> roomList = createRoomList();

        Page<Room> reservationPage = new PageImpl<>(
                roomList,
                pageable,
                roomList.size());
        given(userService.findById(any())).willReturn(user);
        given(roomService.findByHost(any(), any())).willReturn(reservationPage);


        mockMvc.perform(get("/host/roomList")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("host-getRoomList",
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("사이즈")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.hostGetRoomResList[].roomUrl").description("방 사진 url"),
                                fieldWithPath("_embedded.hostGetRoomResList[].toDo").description("해당 API문서 URL"),
                                fieldWithPath("_embedded.hostGetRoomResList[].available").description("해당 API문서 URL"),
                                fieldWithPath("_embedded.hostGetRoomResList[].bedRoomNum").description("해당 API문서 URL"),
                                fieldWithPath("_embedded.hostGetRoomResList[].bedNum").description("해당 API문서 URL"),
                                fieldWithPath("_embedded.hostGetRoomResList[].bathNum").description("해당 API문서 URL"),
                                fieldWithPath("_embedded.hostGetRoomResList[].location").description("해당 API문서 URL"),

                                fieldWithPath("page.size").description("페이지 사이즈"),
                                fieldWithPath("page.totalElements").description("총 게시물 개수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("현재 페이지 넘버"),

                                fieldWithPath("_links.self.href").description("해당 API URL"),
                                fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                        )
                ));
    }
}
