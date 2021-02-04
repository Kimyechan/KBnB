package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.advice.exception.ResourceNotFoundException;
import com.buildup.kbnb.dto.ReservationRequest;
import com.buildup.kbnb.dto.ReservationResponse;
import com.buildup.kbnb.dto.Reservation_ConfirmedResponse;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    /*@GetMapping("/reservation_id")
    public ResponseE<Reservation_Detail_Response> getDetailReservation(@CurrentUser UserPrincipal userPrincipal) {


    }*/
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getConfirmedReservationList(@CurrentUser UserPrincipal userPrincipal, Pageable pageable, PagedResourcesAssembler<Reservation_ConfirmedResponse> assembler) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Page<Reservation> reservationPage = reservationRepository.findByUser(user, pageable);
        List<Reservation> reservationList = reservationPage.getContent();//해당 페이지에만 있는 리스트

        List<Reservation_ConfirmedResponse> reservation_confirmedResponseList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            Reservation_ConfirmedResponse reservation_confirmedResponse = new Reservation_ConfirmedResponse().builder()
                    .reservationId(reservation.getId())
                    .checkIn(reservation.getCheckIn())
                    .checkOut(reservation.getCheckOut())
                    .roomLocation(reservation.getRoom().getLocation().getCity() + " " + reservation.getRoom().getLocation().getBorough() + " " + reservation.getRoom().getLocation().getNeighborhood())
                    .hostName(reservation.getRoom().getUser().getName())
                    .roomName(reservation.getRoom().getName())
                    .status("예약 완료").build();
            if (LocalDate.now().isAfter(reservation.getCheckOut()))//현재 날짜가 체크아웃날짜보다 나중이라면
            {
                reservation_confirmedResponse.setStatus("완료된 여정");
            }
            reservation_confirmedResponseList.add(reservation_confirmedResponse);
        }

        Page<Reservation_ConfirmedResponse> result = new PageImpl<>(reservation_confirmedResponseList, pageable, reservationPage.getTotalElements());
        PagedModel<EntityModel<Reservation_ConfirmedResponse>> model = assembler.toModel(result);
        model.add(Link.of("/docs/api.html#resource-reservation-lookupList").withRel("profile"));

        return ResponseEntity.ok(model);

    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRequest reservationRequest, @CurrentUser UserPrincipal userPrincipal) {
        Room room = roomRepository.findById(reservationRequest.getRoomId()).orElseThrow(() -> new BadRequestException("there is no room like that"));
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Reservation newReservation = Reservation.builder()
                .checkIn(reservationRequest.getCheckIn())
                .checkOut(reservationRequest.getCheckOut())
                .guestNum(reservationRequest.getGuestNumber())
                .room(room)
                .user(user)
                .build();

        reservationRepository.save(newReservation);


        ReservationResponse reservationResponse = new ReservationResponse();
        reservationResponse.setMessage("성공");
        reservationResponse.setReservationId(newReservation.getId());


        EntityModel<ReservationResponse> model = EntityModel.of(reservationResponse);
        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRequest, userPrincipal)).withSelfRel().toUri();
        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRequest, userPrincipal)).withSelfRel());
        return ResponseEntity.created(location)
                .body(model);
    }

    @GetMapping(value = "/{reservationId}",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getDetailReservationInfo(@CurrentUser @Valid  UserPrincipal userPrincipal, Long reservationId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));
// 해당 유저가 해당 예약 식별자를 갖고 있는지
        List<Long> reservationId_user = reservationRepository.findByUserId(user.getId()).stream().map(s -> s.getId()).collect(Collectors.toList());
        return null;
    }

}
