package com.buildup.kbnb.service;

import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.GradeInfo;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.CommentRepository;
import com.buildup.kbnb.service.reservation.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Spy
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private ReservationService reservationService;

    @Test
    @DisplayName("댓글 평점 종합")
    public void calcGradeInfo() {
        Room room = Room.builder()
                .grade(5.0)
                .cleanliness(5.0)
                .accuracy(5.0)
                .communication(5.0)
                .locationRate(5.0)
                .checkIn(5.0)
                .priceSatisfaction(5.0)
                .build();

        Integer commentCount = 1;

        CommentCreateReq req = CommentCreateReq.builder()
                .cleanliness(0.0)
                .accuracy(0.0)
                .communication(0.0)
                .locationRate(0.0)
                .checkIn(0.0)
                .priceSatisfaction(0.0)
                .build();

        GradeInfo gradeInfo = commentService.calcGradeInfo(room, commentCount, req);

        assertThat(gradeInfo.getTotalGrade()).isEqualTo(2.5);
    }

}