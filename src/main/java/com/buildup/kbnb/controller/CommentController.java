package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.comment.*;
import com.buildup.kbnb.dto.reservation.Reservation_ConfirmedResponse;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.CommentRepository;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
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
    private final RoomService roomService;

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
    public ResponseEntity<?> getCommentList(Long roomId,Pageable pageable, PagedResourcesAssembler<CommentDto> assembler) {
        Room room = roomService.findById(roomId);
        Page<Comment> commentPage = commentService.getListByRoomIdWithUser(room, pageable);
        List<Comment> commentList = commentPage.getContent();
        List<CommentDto> commentDtoList = buildCommentDtoList(commentList);
        Page<CommentDto> commentDtoPage = new PageImpl<>(commentDtoList, pageable, commentPage.getTotalElements());
        PagedModel<EntityModel<CommentDto>> pagedModel = assembler.toModel(commentDtoPage);

        CommentListResponse commentListResponse = CommentListResponse.builder()
                .accuracy(room.getAccuracy()).checkIn(room.getCheckIn()).cleanliness(room.getCleanliness()).communication(room.getCommunication())
                .locationRate(room.getLocationRate()).priceSatisfaction(room.getPriceSatisfaction()).grade(room.getGrade()).allComments(pagedModel).build();

        EntityModel<CommentListResponse> model = EntityModel.of(commentListResponse);
        model.add(linkTo(methodOn(CommentController.class).getCommentList(roomId, pageable, assembler)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-comment-list").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    public List<CommentDto> buildCommentDtoList(List<Comment> commentList) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for(Comment comment : commentList) {
            CommentDto commentDto = CommentDto.builder().accuracy(comment.getAccuracy()).checkIn(comment.getCheckIn())
                    .cleanliness(comment.getCleanliness()).communication(comment.getCommunication()).description(comment.getDescription())
                    .locationRate(comment.getLocationRate()).priceSatisfaction(comment.getPriceSatisfaction()).build();
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }
}
