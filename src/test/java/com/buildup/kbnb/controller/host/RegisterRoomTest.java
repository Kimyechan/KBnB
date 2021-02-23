package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.room.BathRoomDto;
import com.buildup.kbnb.dto.room.BedRoomDto;
import com.buildup.kbnb.dto.room.CreateRoomRequestDto;
import com.buildup.kbnb.model.room.Room;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
public class RegisterRoomTest {
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
}
