package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.advice.exception.ResourceNotFoundException;
import com.buildup.kbnb.dto.ReservationRequest;
import com.buildup.kbnb.dto.ReservationResponse;
import com.buildup.kbnb.dto.Reservation_ConfirmedResponse;
import com.buildup.kbnb.dto.Reservation_Detail_Response;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.RoomRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.asm.IModelFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.hibernate.EntityMode;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.function.EntityResponse;

import javax.swing.text.html.parser.Entity;
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
    public ResponseEntity<?> getConfirmedReservationList(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User","id", userPrincipal.getId()));
        List<Reservation> reservationList = user.getReservationList();
        List<EntityModel<Reservation_ConfirmedResponse>> entityResponses = new ArrayList<>();

        for(Reservation reservation : reservationList) {
            Reservation_ConfirmedResponse reservation_confirmedResponse = new Reservation_ConfirmedResponse().builder()
                    .reservationId(reservation.getId())
                    .checkIn(reservation.getCheckIn())
                    .checkOut(reservation.getCheckOut())
                    .roomLocation(reservation.getRoom().getLocation().getCity() + " " + reservation.getRoom().getLocation().getBorough() + " " + reservation.getRoom().getLocation().getNeighborhood())
                    .hostName(reservation.getRoom().getUser().getName())
                    .roomName(reservation.getRoom().getName())
                    .status("예약 완료").build();

            if(LocalDate.now().isAfter(reservation.getCheckOut()))//현재 날짜가 체크아웃날짜보다 나중이라면
            {
                reservation_confirmedResponse.setStatus("완료된 여정");
            }
            EntityModel<Reservation_ConfirmedResponse> reservation_confirmedResponseEntityModel = EntityModel.of(reservation_confirmedResponse);
            entityResponses.add(reservation_confirmedResponseEntityModel);
        }
        CollectionModel model = CollectionModel.of(entityResponses);
        model.add(linkTo(methodOn(ReservationController.class).getConfirmedReservationList(userPrincipal)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-lookupList").withRel("profile"));
//        model.add(Link.of())
        return ResponseEntity.ok(model);

    }

    @PostMapping
    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRequest reservationRequest, @CurrentUser UserPrincipal userPrincipal) {
        Room room = roomRepository.findById(reservationRequest.getRoomId()).orElseThrow(() -> new BadRequestException("there is no room like that"));
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Reservation newReservation = new Reservation().builder()
                .checkIn(reservationRequest.getCheckIn())
                .checkOut(reservationRequest.getCheckOut())
                .guestNum(reservationRequest.getGuestNumber())
                .room(room)
                .user(user)
                .build();

        ReservationResponse reservationResponse= new ReservationResponse();
            reservationRepository.save(newReservation);
            reservationResponse.builder()
                    .reservationId(newReservation.getId())
                    .message("성공적으로 예약되었습니다.")
                    .build();




        EntityModel<ReservationResponse> model = EntityModel.of(reservationResponse);
        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRequest, userPrincipal)).withSelfRel().toUri();
        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRequest, userPrincipal)).withSelfRel());
        return ResponseEntity.created(location)
                .body(model);
    }

}
