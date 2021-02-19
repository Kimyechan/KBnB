package com.buildup.kbnb.controller;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.comment.CommentCreateReq;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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

    @Test
    @DisplayName("댓글 리스트 확인")
    public void getCommentList() throws Exception {
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

        Room room = Room.builder()
                .id(1L)
                .cleanliness(4.5)
                .accuracy(4.5)
                .communication(4.5)
                .locationRate(4.5)
                .checkIn(4.5)
                .priceSatisfaction(4.5)
                .commentList(new ArrayList<>())
                .build();
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i<2; i++) {
            Comment comment = Comment.builder()
                    .id((long) i)
                    .cleanliness(4.5)
                    .accuracy(4.5)
                    .communication(4.5)
                    .locationRate(4.5)
                    .checkIn(4.5)
                    .priceSatisfaction(4.5)
                    .user(user)
                    .room(room)
                    .date(LocalDate.parse("2020-02-02"))
                    .description("오호홓 너무좋아요")
                    .build();
            commentList.add(comment);
        }
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> commentPage = new PageImpl<>(commentList, pageable, commentList.size());
        String token = tokenProvider.createToken(String.valueOf(user.getId())); //이거 인증 뺴도 되는부분인데 물어보기
        given(roomService.findById(any())).willReturn(room);
        given(commentService.findAllByRoomId(any())).willReturn(commentList);
        given(commentService.getListByRoomIdWithUser(any(), any())).willReturn(commentPage);


        mockMvc.perform(get("/comment")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .param("roomId", String.valueOf(1))
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment-list",
                        requestParameters(
                                parameterWithName("roomId").description("방 식별자"),
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지의 사이즈")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json 타입")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        responseFields(
                                fieldWithPath("grade").description("총 등급"),
                                fieldWithPath("cleanliness").description("청결도"),
                                fieldWithPath("accuracy").description("정확성"),
                                fieldWithPath("communication").description("의사소통"),
                                fieldWithPath("locationRate").description("위치"),
                                fieldWithPath("checkIn").description("체크인"),
                                fieldWithPath("priceSatisfaction").description("가격 대비 만족도"),

                                fieldWithPath("allComments._embedded.commentDtoList[].cleanliness").description("댓글당 청결도"),
                                fieldWithPath("allComments._embedded.commentDtoList[].accuracy").description("댓글당 정확성"),
                                fieldWithPath("allComments._embedded.commentDtoList[].communication").description("댓글당 의사소통"),
                                fieldWithPath("allComments._embedded.commentDtoList[].locationRate").description("댓글당 위치"),
                                fieldWithPath("allComments._embedded.commentDtoList[].checkIn").description("댓글당 체크인"),
                                fieldWithPath("allComments._embedded.commentDtoList[].priceSatisfaction").description("댓글당 가격대비 만족도"),
                                fieldWithPath("allComments._embedded.commentDtoList[].description").description("댓글당 댓글"),
                                fieldWithPath("allComments._embedded.commentDtoList[].userImgUrl").description("유저 이미지 url"),
                                fieldWithPath("allComments._embedded.commentDtoList[].userName").description("유저 이름"),
                                fieldWithPath("allComments._embedded.commentDtoList[].creatingDate").description("댓글 작성 날짜"),


                                fieldWithPath("allComments._links.self.href").description("해당 API 주소"),

                                fieldWithPath("allComments.page.size").description("페이지 사이즈"),
                                fieldWithPath("allComments.page.totalElements").description("총 요소의 갯수"),
                                fieldWithPath("allComments.page.totalPages").description("총 페이지 갯수"),
                                fieldWithPath("allComments.page.number").description("해당 페이지 번호"),

                                fieldWithPath("_links.self.href").description("해당 API 주소"),
                                fieldWithPath("_links.profile.href").description("해당 API 문서 주소")
                        )
                ));
    }
    public List<Comment> createCommentList() {
        List<Comment> commentList = new ArrayList<>();
        for(int i = 0; i< 7; i++) {
            Comment comment = Comment.builder()
                    .accuracy(2.2).checkIn(2.2).cleanliness(2.2).communication(2.3)
                    .id((long)i).priceSatisfaction(2.2).locationRate(2.2).build();
            commentList.add(comment);
        }
        return commentList;
    }
}