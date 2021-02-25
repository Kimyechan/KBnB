package com.buildup.kbnb.service;

import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.GradeInfo;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.CommentRepository;
import com.buildup.kbnb.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final RoomService roomService;
    private final ReservationService reservationService;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Page<Comment> getListByRoomIdWithUser(Room room, Pageable pageable) {
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("date").descending());

        return commentRepository.findByRoom(room, newPageable);
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

    public GradeInfo calcGradeInfo(Room room, Integer commentCount, CommentCreateReq req) {
        Double cleanliness = (room.getCleanliness() * commentCount + req.getCleanliness()) / (commentCount + 1);
        Double accuracy = (room.getAccuracy() * commentCount + req.getAccuracy()) / (commentCount + 1);
        Double communication = (room.getCommunication() * commentCount + req.getCommunication()) / (commentCount + 1);
        Double locationRate = (room.getLocationRate() * commentCount + req.getLocationRate()) / (commentCount + 1);
        Double checkIn = (room.getCheckIn() * commentCount + req.getCheckIn()) / (commentCount + 1);
        Double priceSatisfaction = (room.getPriceSatisfaction() * commentCount + req.getPriceSatisfaction()) / (commentCount + 1);
        Double totalGrade = (cleanliness + accuracy + communication + locationRate + checkIn + priceSatisfaction) / 6;

        return GradeInfo.builder()
                .cleanliness(cleanliness)
                .accuracy(accuracy)
                .communication(communication)
                .locationRate(locationRate)
                .checkIn(checkIn)
                .priceSatisfaction(priceSatisfaction)
                .totalGrade(totalGrade)
                .build();
    }

    public Comment createCommentTx(CommentCreateReq req, Reservation reservation, Room room, GradeInfo gradeInfo) {
        Room savedRoom = roomService.updateRoomGrade(room, gradeInfo);
        Comment comment = saveComment(req, reservation.getUser(), savedRoom);
        reservationService.updateWithComment(reservation, comment);

        return comment;
    }

    public List<Comment> findAllByRoomId(Long roomId) {
        return commentRepository.findAllByRoomId(roomId);
    }

}
