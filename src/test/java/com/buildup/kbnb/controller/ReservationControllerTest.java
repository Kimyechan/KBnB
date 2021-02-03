package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.ReservationRequest;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    Room room;

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


}