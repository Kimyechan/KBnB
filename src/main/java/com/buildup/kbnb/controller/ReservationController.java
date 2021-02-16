package com.buildup.kbnb.controller;



import com.buildup.kbnb.dto.reservation.ReservationRegisterRequest;
import com.buildup.kbnb.dto.reservation.ReservationRegisterResponse;

import com.buildup.kbnb.advice.exception.ReservationException;

import com.buildup.kbnb.dto.reservation.ReservationConfirmedResponse;
import com.buildup.kbnb.dto.reservation.ReservationDetailResponse;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final UserService userService;
    private final RoomService roomService;
    private final ReservationService reservationService;

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRegisterRequest reservationRegisterRequest, @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId());
        Room room = roomService.findById(reservationRegisterRequest.getRoomId());
        List<Reservation> reservationList = reservationService.findByRoomId(room.getId());

        LocalDate checkIn = reservationRegisterRequest.getCheckIn(); LocalDate checkOut = reservationRegisterRequest.getCheckOut();
        checkAvailableDate(reservationList, checkIn, checkOut);
        ReservationRegisterResponse reservationResponse = createReservationResponse(room, reservationRegisterRequest, user);

        EntityModel<ReservationRegisterResponse> model = EntityModel.of(reservationResponse);
        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel().toUri();
        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel());
        return ResponseEntity.created(location)
                .body(model);
    }

    private ReservationRegisterResponse createReservationResponse(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
        Reservation reservation = createAndSaveReservation(room, reservationRegisterRequest, user);
        return ReservationRegisterResponse.builder()
                .message("예약 성공").reservationId(reservation.getId()).build();
    }

    private void checkAvailableDate(List<Reservation> reservationList, LocalDate checkIn, LocalDate checkOut) {
        for(Reservation reservation : reservationList) {
            if((checkIn.isAfter(reservation.getCheckIn()) && checkIn.isBefore(reservation.getCheckOut()))
                    || (checkIn.isBefore(reservation.getCheckIn()) && checkOut.isAfter(reservation.getCheckOut()))
                    || (checkOut.isAfter(reservation.getCheckIn()) && checkOut.isBefore(reservation.getCheckOut()))
                    || checkOut.isEqual(reservation.getCheckIn()) && checkOut.isEqual(reservation.getCheckOut()))
                throw new ReservationException("예약이 불가능한 날짜입니다.");
        }
    }

    public Reservation createAndSaveReservation(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
        Reservation reservation = Reservation.builder()
                .room(room)
                .guestNum(reservationRegisterRequest.getGuestNumber())
                .checkOut(reservationRegisterRequest.getCheckOut())
                .checkIn(reservationRegisterRequest.getCheckIn())
                .totalCost(reservationRegisterRequest.getTotalCost())
                .user(user)
                .build();
                return reservationService.save(reservation);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getConfirmedReservationLIst(@CurrentUser UserPrincipal userPrincipal, Pageable pageable, PagedResourcesAssembler<ReservationConfirmedResponse> assembler) {
        User user = userService.findById(userPrincipal.getId());

        Page<Reservation> reservationPage = reservationService.findPageByUser(user, pageable);
        List<Reservation> reservationList = reservationPage.getContent(); //해당 페이지의 모든 컨텐츠
        List<ReservationConfirmedResponse> reservation_confirmedResponseList = reservationService.createResponseList(reservationList);
        PagedModel<EntityModel<ReservationConfirmedResponse>> model = makePageModel(reservation_confirmedResponseList, pageable, reservationPage.getTotalElements(), assembler);
        return ResponseEntity.ok(model);
    }

    private  PagedModel<EntityModel<ReservationConfirmedResponse>> makePageModel(List<ReservationConfirmedResponse> reservation_confirmedResponseList, Pageable pageable, Long totalElements, PagedResourcesAssembler<ReservationConfirmedResponse> assembler ) {
        Page<ReservationConfirmedResponse> responsePage = new PageImpl<>(reservation_confirmedResponseList, pageable, totalElements);
        PagedModel<EntityModel<ReservationConfirmedResponse>> model = assembler.toModel(responsePage);
        model.add(Link.of("/docs/api.html#resource-reservation-lookupList").withRel("profile"));
        return model;
    }



    @GetMapping(value = "/detail",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getDetailReservationInfo(@CurrentUser UserPrincipal userPrincipal, Long reservationId) {
    User user = userService.findById(userPrincipal.getId());
    List<Long> reservationIdUserHave = reservationService.findByUser(user).stream().map(s -> s.getId()).collect(Collectors.toList());

        ReservationDetailResponse reservationDetailResponse = reservationService.judgeReservationIdUserHaveContainReservationId(reservationIdUserHave, reservationId);
        EntityModel<ReservationDetailResponse> model = EntityModel.of(reservationDetailResponse);
        model.add(linkTo(methodOn(ReservationController.class).getDetailReservationInfo(userPrincipal, reservationId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-detail").withRel("profile"));
        return ResponseEntity.ok(model);
    }



    @DeleteMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> deleteReservation(@CurrentUser UserPrincipal userPrincipal, Long reservationId) {
        User user = userService.findById(userPrincipal.getId());
        List<Reservation> reservationList = reservationService.findByUser(user);
        if(!reservationList.stream().map(s -> s.getId()).collect(Collectors.toList()).contains(reservationId))
            throw new ReservationException("there is no reservation that you asked");
        reservationService.deleteById(reservationId);
        Map<String, String> map = new HashMap<>();
        EntityModel<Map> model = EntityModel.of(map);
        model.add(linkTo(methodOn(ReservationController.class).deleteReservation(userPrincipal, reservationId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-delete").withRel("profile"));
        return ResponseEntity.ok(model);
    }

}
