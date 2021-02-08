package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.*;
import com.buildup.kbnb.dto.reservation.ReservationRequest;
import com.buildup.kbnb.dto.reservation.ReservationResponse;
import com.buildup.kbnb.dto.reservation.Reservation_ConfirmedResponse;
import com.buildup.kbnb.dto.reservation.Reservation_Detail_Response;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.RoomImgRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
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
import java.util.*;
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
    private final RoomImgRepository roomImgRepository;
    private final ReservationService reservationService;

    /*@GetMapping("/reservation_id")
    public ResponseE<Reservation_Detail_Response> getDetailReservation(@CurrentUser UserPrincipal userPrincipal) {


    }*/
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getConfirmedReservationList(@CurrentUser UserPrincipal userPrincipal, Pageable pageable, PagedResourcesAssembler<Reservation_ConfirmedResponse> assembler) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ReservationException("there is no user"));

        Page<Reservation> reservationPage = reservationRepository.findByUser(user, pageable);
        List<Reservation> reservationList = reservationPage.getContent();//해당 페이지에만 있는 리스트

        List<Reservation_ConfirmedResponse> reservation_confirmedResponseList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            Reservation_ConfirmedResponse reservation_confirmedResponse = new Reservation_ConfirmedResponse().builder()
                    .reservationId(reservation.getId())
                    .checkIn(reservation.getCheckIn())
                    .checkOut(reservation.getCheckOut())
                    .roomLocation(reservation.getRoom().getLocation().getCity() + " " + reservation.getRoom().getLocation().getBorough() + " " + reservation.getRoom().getLocation().getNeighborhood())
                    .hostName(reservation.getRoom().getHost().getName())
                    .roomName(reservation.getRoom().getName())
                    .roomId(reservation.getRoom().getId())
                    .imgUrl(roomImgRepository.findByRoom(reservation.getRoom()).get(0).getUrl())
                    .status("예약 완료").build();

            if (LocalDate.now().isAfter(reservation.getCheckOut()))//현재 날짜가 체크아웃날짜보다 나중이라면
            {
                reservation_confirmedResponse.setStatus("완료된 여정");
            }
            else
                reservation_confirmedResponse.setStatus("예약 완료");
            reservation_confirmedResponseList.add(reservation_confirmedResponse);
        }

        Page<Reservation_ConfirmedResponse> result = new PageImpl<>(reservation_confirmedResponseList, pageable, reservationPage.getTotalElements());
        PagedModel<EntityModel<Reservation_ConfirmedResponse>> model = assembler.toModel(result);
        model.add(Link.of("/docs/api.html#resource-reservation-lookupList").withRel("profile"));

        return ResponseEntity.ok(model);

    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRequest reservationRequest, @CurrentUser UserPrincipal userPrincipal) {
        Room room = roomRepository.findById(reservationRequest.getRoomId()).orElseThrow(() -> new ReservationException("there is no room"));
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ReservationException("there is no user"));
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

    @GetMapping(value = "/detail",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getDetailReservationInfo(@CurrentUser UserPrincipal userPrincipal, Long reservationId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ReservationException("there is no user"));
        List<Long> reservationId_user = reservationRepository.findByUserId(user.getId()).stream().map(Reservation::getId).collect(Collectors.toList());
        Reservation_Detail_Response reservation_detail_response = new Reservation_Detail_Response();
        if(reservationId_user.contains(reservationId))
          reservation_detail_response = ifReservationIdExist(reservationId);
        EntityModel<Reservation_Detail_Response> model = EntityModel.of(reservation_detail_response);
        model.add(linkTo(methodOn(ReservationController.class).getDetailReservationInfo(userPrincipal, reservationId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-detail").withRel("profile"));
        return ResponseEntity.ok(model);
    }


    @DeleteMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> deleteReservation(@CurrentUser UserPrincipal userPrincipal, Long reservationId) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ReservationException("there is no user"));
        List<Reservation> reservationList = reservationRepository.findByUserId(user.getId());
        if(!reservationList.stream().map(s -> s.getId()).collect(Collectors.toList()).contains(reservationId))
            throw new ReservationException("there is no reservation that you asked");
        reservationRepository.deleteById(reservationId);
        Map<String, String> map = new HashMap<>();
        EntityModel<Map> model = EntityModel.of(map);
        model.add(linkTo(methodOn(ReservationController.class).deleteReservation(userPrincipal, reservationId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-delete").withRel("profile"));
        return ResponseEntity.ok(model);
    }
    @GetMapping(value = "/test",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public void test(@CurrentUser UserPrincipal userPrincipal) {
        System.out.println(userPrincipal.getId());
        System.out.println("=============================================");
    }
    public Reservation_Detail_Response ifReservationIdExist(Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        List<BedRoom> bedRoomList = reservation.getRoom().getBedRoomList();
        int bedRoomNum = bedRoomList.size();
        int bedNum = bedNum(bedRoomList);
        Reservation_Detail_Response reservation_detail_response = Reservation_Detail_Response.builder()
                .hostImage("this is demo host Image URL")
                .roomImage("this is demo room Image URL")
                .bedRoomNum(bedRoomNum)
                .bedNum(bedNum)
                .bathRoomNum(reservation.getRoom().getBathRoomList().size())
                .address(
                        reservation.getRoom().getLocation().getCountry() + " "
                                +  reservation.getRoom().getLocation().getCity() + " "
                                +  reservation.getRoom().getLocation().getBorough() + " "
                                + reservation.getRoom().getLocation().getNeighborhood() + " "
                                + reservation.getRoom().getLocation().getDetailAddress() )
                .latitude(reservation.getRoom().getLocation().getLatitude())
                .longitude(reservation.getRoom().getLocation().getLongitude())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .guestNum(reservation.getGuestNum())
                .hostName(reservation.getRoom().getHost().getName())
                .roomName(reservation.getRoom().getName())
                .isParking(reservation.getRoom().getIsParking())
                .isSmoking(reservation.getRoom().getIsSmoking())
                .roomId(reservation.getRoom().getId())
                .totalCost(reservation.getTotalCost())
                .build();
        return reservation_detail_response;
    }
public int bedNum(List<BedRoom> bedRoomList) {
        int bedNum = 0;
    for(BedRoom bedRoom : bedRoomList) {
        bedNum += bedRoom.getDoubleSize();
        bedNum += bedRoom.getQueenSize();
        bedNum += bedRoom.getSingleSize();
        bedNum += bedRoom.getSuperSingleSize();
    }

    return bedNum;
}
}
