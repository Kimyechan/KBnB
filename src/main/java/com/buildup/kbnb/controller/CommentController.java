package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.CommentFieldNotValidException;
import com.buildup.kbnb.dto.comment.*;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<?> create(@RequestBody @Valid CommentCreateReq req, BindingResult error) {
        if (error.hasErrors()) {
            throw new CommentFieldNotValidException();
        }

        Reservation reservation = reservationService.findByIdWithRoomAndUser(req.getReservationId());

        Room room = reservation.getRoom();
        Integer commentCount = room.getCommentList().size();
        GradeInfo gradeInfo = commentService.calcGradeInfo(room, commentCount, req);
        Comment savedComment = commentService.createCommentTx(req, reservation, room, gradeInfo);

        CommentCreateRes res = CommentCreateRes.builder()
                .commentId(savedComment.getId())
                .build();

        EntityModel<CommentCreateRes> model = EntityModel.of(res);
        WebMvcLinkBuilder selfLink = linkTo(methodOn(CommentController.class).create(req, error));
        model.add(selfLink.withSelfRel());
        model.add(Link.of("/docs/api.html#resource-comment-create").withRel("profile"));

        return ResponseEntity.created(selfLink.toUri()).body(model);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getCommentList(Long roomId, Pageable pageable, PagedResourcesAssembler<CommentDto> assembler) {
        Room room = roomService.findById(roomId);
        Page<Comment> commentPage = commentService.getListByRoomIdWithUser(room, pageable);
        List<Comment> commentList = commentPage.getContent();

        List<CommentDto> commentDtoList = mapToCommentDtoList(commentList);
        Page<CommentDto> commentDtoPage = new PageImpl<>(commentDtoList, pageable, commentPage.getTotalElements());

        PagedModel<EntityModel<CommentDto>> pagedModel = assembler.toModel(commentDtoPage);
        CommentListResponse commentListResponse = mapToCommentListResponse(room, pagedModel);

        EntityModel<CommentListResponse> model = EntityModel.of(commentListResponse);
        model.add(linkTo(methodOn(CommentController.class).getCommentList(roomId, pageable, assembler)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-comment-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    private CommentListResponse mapToCommentListResponse(Room room, PagedModel<EntityModel<CommentDto>> pagedModel) {
        return CommentListResponse.builder()
                .accuracy(room.getAccuracy())
                .checkIn(room.getCheckIn())
                .cleanliness(room.getCleanliness())
                .communication(room.getCommunication())
                .locationRate(room.getLocationRate())
                .priceSatisfaction(room.getPriceSatisfaction())
                .grade(room.getGrade())
                .allComments(pagedModel).build();
    }

    public List<CommentDto> mapToCommentDtoList(List<Comment> commentList) {
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDto commentDto = CommentDto.builder()
                    .accuracy(comment.getAccuracy())
                    .checkIn(comment.getCheckIn())
                    .cleanliness(comment.getCleanliness())
                    .communication(comment.getCommunication())
                    .description(comment.getDescription())
                    .locationRate(comment.getLocationRate())
                    .priceSatisfaction(comment.getPriceSatisfaction())
                    .userImgUrl(comment.getUser().getImageUrl())
                    .creatingDate(comment.getDate())
                    .userName(comment.getUser().getName())
                    .build();
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }
}
