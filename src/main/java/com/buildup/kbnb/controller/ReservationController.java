package com.buildup.kbnb.controller;


import com.buildup.kbnb.dto.reservation.*;

import com.buildup.kbnb.advice.exception.ReservationException;

import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import com.buildup.kbnb.util.payment.model.request.Cancel;
import com.buildup.kbnb.util.payment.model.response.CancelInfo;
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
    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRegisterRequest reservationRegisterRequest, @CurrentUser UserPrincipal userPrincipal) throws Exception {
        User user = userService.findById(userPrincipal.getId());
        Room room = roomService.findById(reservationRegisterRequest.getRoomId());
        List<Reservation> reservationList = reservationService.findByRoomId(room.getId());

        LocalDate checkIn = reservationRegisterRequest.getCheckIn();
        LocalDate checkOut = reservationRegisterRequest.getCheckOut();
        checkAvailableDate(reservationList, checkIn, checkOut);

        Reservation reservation = mapToReservation(room, reservationRegisterRequest, user);
        Payment payment = Payment.builder()
                .receiptId(reservationRegisterRequest.getPayment().getReceipt_id())
                .price(reservationRegisterRequest.getPayment().getPrice())
                .build();
        Reservation savedReservation = reservationService.saveWithPayment(reservation, payment);

        ReservationRegisterResponse reservationResponse = ReservationRegisterResponse.builder()
                .message("예약 성공")
                .reservationId(savedReservation.getId())
                .build();

        EntityModel<ReservationRegisterResponse> model = EntityModel.of(reservationResponse);
        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel().toUri();
        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel());
        return ResponseEntity.created(location)
                .body(model);
    }

    private Reservation mapToReservation(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
        return Reservation.builder()
                .room(room)
                .guestNum(reservationRegisterRequest.getGuestNumber())
                .checkOut(reservationRegisterRequest.getCheckOut())
                .checkIn(reservationRegisterRequest.getCheckIn())
                .totalCost(reservationRegisterRequest.getTotalCost())
                .user(user)
                .build();
    }

    private void checkAvailableDate(List<Reservation> reservationList, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation reservation : reservationList) {
            if ((checkIn.isEqual(reservation.getCheckIn()) || checkIn.isAfter(reservation.getCheckIn()) && checkIn.isBefore(reservation.getCheckOut()))
                    || (checkIn.isBefore(reservation.getCheckIn()) && checkOut.isAfter(reservation.getCheckOut()))
                    || (checkOut.isAfter(reservation.getCheckIn()) && (checkOut.isBefore(reservation.getCheckOut()) || checkOut.isEqual(reservation.getCheckOut()))
                    || (checkIn.isEqual(reservation.getCheckIn()) && checkOut.isEqual(reservation.getCheckOut()))))
                throw new ReservationException("예약이 불가능한 날짜입니다.");
        }
    }

    //    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
//    public ResponseEntity<?> registerReservation(@Valid @RequestBody ReservationRegisterRequest reservationRegisterRequest, @CurrentUser UserPrincipal userPrincipal) {
//        User user = userService.findById(userPrincipal.getId());
//        Room room = roomService.findById(reservationRegisterRequest.getRoomId());
//        List<Reservation> reservationList = reservationService.findByRoomId(room.getId());
//
//        LocalDate checkIn = reservationRegisterRequest.getCheckIn();
//        LocalDate checkOut = reservationRegisterRequest.getCheckOut();
//        checkAvailableDate(reservationList, checkIn, checkOut);
//        ReservationRegisterResponse reservationResponse = createReservationResponse(room, reservationRegisterRequest, user);
//
//        EntityModel<ReservationRegisterResponse> model = EntityModel.of(reservationResponse);
//        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel().toUri();
//        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
//        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel());
//        return ResponseEntity.created(location)
//                .body(model);
//    }

//    private ReservationRegisterResponse createReservationResponse(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
//        Reservation reservation = createAndSaveReservation(room, reservationRegisterRequest, user);
//        return ReservationRegisterResponse.builder()
//                .message("예약 성공")
//                .reservationId(reservation.getId())
//                .build();
//    }

//    public Reservation createAndSaveReservation(Room room, ReservationRegisterRequest reservationRegisterRequest, User user) {
//        Reservation reservation = Reservation.builder()
//                .room(room)
//                .guestNum(reservationRegisterRequest.getGuestNumber())
//                .checkOut(reservationRegisterRequest.getCheckOut())
//                .checkIn(reservationRegisterRequest.getCheckIn())
//                .totalCost(reservationRegisterRequest.getTotalCost())
//                .user(user)
//                .build();
//        return reservationService.save(reservation);
//    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getConfirmedReservationLIst(@CurrentUser UserPrincipal userPrincipal, Pageable pageable, PagedResourcesAssembler<ReservationConfirmedResponse> assembler) {
        User user = userService.findById(userPrincipal.getId());

        Page<Reservation> reservationPage = reservationService.findPageByUser(user, pageable);
        List<Reservation> reservationList = reservationPage.getContent(); //해당 페이지의 모든 컨텐츠
        List<ReservationConfirmedResponse> reservation_confirmedResponseList = createResponseList(reservationList);
        PagedModel<EntityModel<ReservationConfirmedResponse>> model = makePageModel(reservation_confirmedResponseList, pageable, reservationPage.getTotalElements(), assembler);
        return ResponseEntity.ok(model);
    }

    private PagedModel<EntityModel<ReservationConfirmedResponse>> makePageModel(List<ReservationConfirmedResponse> reservation_confirmedResponseList, Pageable pageable, Long totalElements, PagedResourcesAssembler<ReservationConfirmedResponse> assembler) {
        Page<ReservationConfirmedResponse> responsePage = new PageImpl<>(reservation_confirmedResponseList, pageable, totalElements);
        PagedModel<EntityModel<ReservationConfirmedResponse>> model = assembler.toModel(responsePage);
        model.add(Link.of("/docs/api.html#resource-reservation-lookupList").withRel("profile"));
        return model;
    }

    private List<ReservationConfirmedResponse> createResponseList(List<Reservation> reservationList) {
        List<ReservationConfirmedResponse> reservation_confirmedResponseList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            Room room = reservation.getRoom();
            Location location = room.getLocation();
            ReservationConfirmedResponse reservation_confirmedResponse = ReservationConfirmedResponse.builder()
                    .reservationId(reservation.getId()).checkIn(reservation.getCheckIn()).checkOut(reservation.getCheckOut())
                    .hostName(reservationService.getHostName(reservation)).imgUrl("this is demo url").roomName(room.getName())
                    .roomId(room.getId()).roomLocation(location.getCountry() + " " + location.getCity() + " " + location.getBorough() + " " + location.getNeighborhood() + " " + location.getDetailAddress())
                    .status("예약 완료").build();

            if (reservation_confirmedResponse.getCheckOut().isBefore(LocalDate.now()))
                reservation_confirmedResponse.setStatus("완료된 여정");
            reservation_confirmedResponseList.add(reservation_confirmedResponse);
        }
        return reservation_confirmedResponseList;
    }

    @GetMapping(value = "/detail", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> getDetailReservationInfo(@CurrentUser UserPrincipal userPrincipal, Long reservationId) {
        User user = userService.findById(userPrincipal.getId());
        List<Long> reservationIdUserHave = reservationService.findByUser(user).stream().map(s -> s.getId()).collect(Collectors.toList());

        ReservationDetailResponse reservationDetailResponse = judgeReservationIdUserHaveContainReservationId(reservationIdUserHave, reservationId);
        EntityModel<ReservationDetailResponse> model = EntityModel.of(reservationDetailResponse);
        model.add(linkTo(methodOn(ReservationController.class).getDetailReservationInfo(userPrincipal, reservationId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-detail").withRel("profile"));
        return ResponseEntity.ok(model);
    }

    private ReservationDetailResponse judgeReservationIdUserHaveContainReservationId(List<Long> reservationIdUserHave, Long reservationId) {
        ReservationDetailResponse reservationDetailResponse;
        if (reservationIdUserHave.contains(reservationId))
            reservationDetailResponse = ifReservationIdExist(reservationId);
        else throw new ReservationException("해당 유저의 예약 리스트에는 요청한 예약건이 없습니다.");
        return reservationDetailResponse;
    }

    public ReservationDetailResponse ifReservationIdExist(Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        List<BedRoom> bedRoomList = reservation.getRoom().getBedRoomList();
        int bedRoomNum = bedRoomList.size();
        int bedNum = reservation.getRoom().getBedNum();
        ReservationDetailResponse reservation_detail_response = ReservationDetailResponse.builder()
                .hostImage("this is demo host Image URL")
                .roomImage("this is demo room Image URL")
                .bedRoomNum(bedRoomNum)
                .bedNum(bedNum)
                .bathRoomNum(reservation.getRoom().getBathRoomList().size())
                .address(
                        reservation.getRoom().getLocation().getCountry() + " "
                                + reservation.getRoom().getLocation().getCity() + " "
                                + reservation.getRoom().getLocation().getBorough() + " "
                                + reservation.getRoom().getLocation().getNeighborhood() + " "
                                + reservation.getRoom().getLocation().getDetailAddress())
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

    @DeleteMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> deleteReservation(@CurrentUser UserPrincipal userPrincipal,
                                               @RequestBody CancelDto cancelDto) throws Exception {
        User user = userService.findById(userPrincipal.getId());
        List<Reservation> reservationList = reservationService.findByUser(user);

        if (!reservationList.stream().map(Reservation::getId).collect(Collectors.toList()).contains(cancelDto.getReservationId())) {
            throw new ReservationException("there is no reservation that you asked");
        }
        Cancel cancel = Cancel.builder()
                .name(cancelDto.getName())
                .reason(cancelDto.getReason())
                .build();

        reservationService.cancelReservation(cancelDto.getReservationId(), cancel);

        Map<String, String> map = new HashMap<>();
        EntityModel<Map> model = EntityModel.of(map);
        model.add(linkTo(methodOn(ReservationController.class).deleteReservation(userPrincipal, cancelDto)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-reservation-delete").withRel("profile"));
        return ResponseEntity.ok(model);
    }

  /*  @GetMapping(value = "/test",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
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
    }*/
/*public int bedNum(List<BedRoom> bedRoomList) {
        int bedNum = 0;
    for(BedRoom bedRoom : bedRoomList) {
        bedNum += bedRoom.getDoubleSize();
        bedNum += bedRoom.getQueenSize();
        bedNum += bedRoom.getSingleSize();
        bedNum += bedRoom.getSuperSingleSize();
    }

    return bedNum;
}*/
}
