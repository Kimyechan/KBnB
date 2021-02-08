package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.CommentCreateRes;
import com.buildup.kbnb.dto.comment.GradeDto;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CommentCreateReq req) {
        Reservation reservation = reservationService.findByIdWithRoomAndUser(req.getReservationId());

        Room room = reservation.getRoom();
        Integer commentCount = room.getCommentList().size();
        GradeDto gradeDto = getGradeInfo(room, commentCount, req);
        Comment savedComment = commentService.createCommentTx(req, reservation, room, gradeDto);

        CommentCreateRes res = CommentCreateRes.builder()
                .commentId(savedComment.getId())
                .build();

        EntityModel<CommentCreateRes> model = EntityModel.of(res);
        WebMvcLinkBuilder selfLink = linkTo(methodOn(CommentController.class).create(req));
        model.add(selfLink.withSelfRel());

        return ResponseEntity.created(selfLink.toUri()).body(model);
    }

    private GradeDto getGradeInfo(Room room, Integer commentCount, CommentCreateReq req) {
        Double cleanliness = (room.getCleanliness() * commentCount + req.getCleanliness()) / (commentCount + 1);
        Double accuracy = (room.getAccuracy() * commentCount + req.getAccuracy()) / (commentCount + 1);
        Double communication = (room.getCommunication() * commentCount + req.getCommunication()) / (commentCount + 1);
        Double locationRate = (room.getLocationRate() * commentCount + req.getLocationRate()) / (commentCount + 1);
        Double checkIn = (room.getCheckIn() * commentCount + req.getCheckIn()) / (commentCount + 1);
        Double priceSatisfaction = (room.getPriceSatisfaction() * commentCount + req.getPriceSatisfaction()) / (commentCount + 1);
        Double totalGrade = (cleanliness + accuracy + communication + locationRate + checkIn + priceSatisfaction) / 6;

        return GradeDto.builder()
                .cleanliness(cleanliness)
                .accuracy(accuracy)
                .communication(communication)
                .locationRate(locationRate)
                .checkIn(checkIn)
                .priceSatisfaction(priceSatisfaction)
                .totalGrade(totalGrade)
                .build();
    }
}
