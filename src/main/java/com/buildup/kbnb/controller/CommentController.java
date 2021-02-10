package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.comment.CommentCreateReq;
import com.buildup.kbnb.dto.comment.CommentCreateRes;
import com.buildup.kbnb.dto.comment.CommentListResponse;
import com.buildup.kbnb.dto.comment.GradeDto;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        model.add(Link.of("/docs/api.html#resource-comment-create").withRel("profile"));

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

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getCommentList(Long roomId) {

        List<Comment> commentList = commentService.findAllByRoomId(roomId);
        CommentListResponse commentListResponse = buildCommentRes(commentList);

        EntityModel<CommentListResponse> model = EntityModel.of(commentListResponse);
        model.add(linkTo(methodOn(CommentController.class).getCommentList(roomId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-comment-list").withRel("profile"));
        return ResponseEntity.ok(model);
    }
    public CommentListResponse buildCommentRes(List<Comment> commentList) {
        int commentListSize = commentList.size();
        double acc = 0; double check= 0; double clean= 0; double comm= 0; double grade= 0; double lo= 0; double price= 0;
        List<Comment> first6Comments;

        if (commentListSize <= 6)
            first6Comments = commentList;
        else
            first6Comments = commentList.subList(0,6);
        for(Comment comment : commentList) {
            acc += comment.getAccuracy(); lo += comment.getLocationRate();
            check += comment.getCheckIn(); price += comment.getPriceSatisfaction();
            clean += comment.getCleanliness();  comm += comment.getCommunication();
        }
        acc = (double)Math.round(acc/commentListSize*10)/10; lo = (double)Math.round(lo/commentListSize*10)/10; check = (double)Math.round(check/commentListSize*10)/10;
        price = (double)Math.round(price/commentListSize*10)/10; clean = (double)Math.round(clean/commentListSize*10)/10; comm = (double)Math.round(comm/commentListSize*10)/10;
        grade = (double)Math.round((acc + lo + check + price + clean + comm)/6*10)/10;
        CommentListResponse commentListResponse = new CommentListResponse().builder()
                .accuracy(acc).checkIn(check).cleanliness(clean).communication(price).locationRate(lo).priceSatisfaction(price)
                .grade(grade).allComments(commentList).first6Comments(first6Comments).build();
        return commentListResponse;
    }
}
