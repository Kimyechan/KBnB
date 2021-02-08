package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.CommentCreateRes;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    CustomUserDetailsService customUserDetailsService;
    @MockBean
    ReservationService reservationService;
    @MockBean
    RoomService roomService;
    @MockBean
    CommentService commentService;


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
    @DisplayName("댓글 등록")
    public void createComment() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        CommentCreateReq req = CommentCreateReq.builder()
                .reservationId(1L)
                .cleanliness(4.5)
                .accuracy(4.5)
                .communication(4.5)
                .locationRate(4.5)
                .checkIn(4.5)
                .priceSatisfaction(4.5)
                .description("test comment")
                .build();

        Room room = Room.builder()
                .cleanliness(4.5)
                .accuracy(4.5)
                .communication(4.5)
                .locationRate(4.5)
                .checkIn(4.5)
                .priceSatisfaction(4.5)
                .commentList(new ArrayList<>())
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .checkIn(LocalDate.of(2021, 2, 10))
                .checkOut(LocalDate.of(2021, 2, 11))
                .guestNum(3)
                .totalCost(50000L)
                .user(user)
                .comment(Comment.builder().build())
                .room(room)
                .commentExisted(false)
                .build();

        Comment res = Comment.builder()
                .id(1L)
                .build();

        given(reservationService.findByIdWithRoomAndUser(eq(req.getReservationId()))).willReturn(reservation);
        given(commentService.createCommentTx(any(), any(), any(), any())).willReturn(res);

        mockMvc.perform(post("/comment")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("comment-create",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT 인증 토큰").optional(),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        requestFields(
                                fieldWithPath("reservationId").description("예약 식별자 값"),
                                fieldWithPath("cleanliness").description("청결도 점수"),
                                fieldWithPath("accuracy").description("정확도 점수"),
                                fieldWithPath("communication").description("의사소통 점수"),
                                fieldWithPath("locationRate").description("위치 편의성 점수"),
                                fieldWithPath("checkIn").description("체크인 점수"),
                                fieldWithPath("priceSatisfaction").description("가격 대비 만족도"),
                                fieldWithPath("description").description("댓글 내용")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 식별자 값"),
                                fieldWithPath("_links.self.href").description("해당 API 주소"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 주소")
                        )
                ));
    }
}