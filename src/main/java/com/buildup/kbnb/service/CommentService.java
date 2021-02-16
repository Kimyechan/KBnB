package com.buildup.kbnb.service;

import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.GradeDto;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.CommentRepository;
import com.buildup.kbnb.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final RoomService roomService;
    private final ReservationService reservationService;

    public Page<Comment> getListByRoomIdWithUser(Room room, Pageable pageable) {
        return commentRepository.findByRoom(room, pageable);
    }

    public Comment saveComment(CommentCreateReq req, User user, Room savedRoom) {
        Comment comment = Comment.builder()
                .accuracy(req.getAccuracy())
                .checkIn(req.getCheckIn())
                .cleanliness(req.getCleanliness())
                .communication(req.getCommunication())
                .locationRate(req.getLocationRate())
                .priceSatisfaction(req.getPriceSatisfaction())
                .date(LocalDate.now())
                .description(req.getDescription())
                .room(savedRoom)
                .user(user)
                .build();

        commentRepository.save(comment);
        return comment;
    }

    public Comment createCommentTx(CommentCreateReq req, Reservation reservation, Room room, GradeDto gradeDto) {
        Room savedRoom = roomService.updateRoomGrade(room, gradeDto);
        Comment comment = saveComment(req, reservation.getUser(), savedRoom);
        reservationService.updateWithComment(reservation, comment);

        return comment;
    }
    public List<Comment> findAllByRoomId(Long roomId) {
        return commentRepository.findAllByRoomId(roomId);
    }

}
