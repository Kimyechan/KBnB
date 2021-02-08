package com.buildup.kbnb.controller;

import com.buildup.kbnb.advice.exception.*;
import com.buildup.kbnb.dto.reservation.Reservation_RegisterRequest;
import com.buildup.kbnb.dto.reservation.Reservation_RegisterResponse;
import com.buildup.kbnb.dto.reservation.Reservation_ConfirmedResponse;
import com.buildup.kbnb.dto.reservation.Reservation_Detail_Response;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
   /* private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomImgRepository roomImgRepository;
    private final ReservationService reservationService;*/
    private final UserService userService;
    private final RoomService roomService;
    private final ReservationService reservationService;

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> registerReservation(@Valid @RequestBody Reservation_RegisterRequest reservationRegisterRequest, @CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId());
        //request 에서 받은 방 번호가 존재하지 않을 때 해당 방 번호가 없다는 예외처리 날림
        Room room = roomService.findById(reservationRegisterRequest.getRoomId());
        //해당 룸으로 예약된 모든 예약날짜를 비교함
        List<Reservation> reservationList = reservationService.findByRoomId(room.getId());
        LocalDate checkIn = reservationRegisterRequest.getCheckIn(); LocalDate checkOut = reservationRegisterRequest.getCheckOut();
        for(Reservation reservation : reservationList) {
            if((checkIn.isAfter(reservation.getCheckIn()) && checkIn.isBefore(reservation.getCheckOut()))
            || (checkIn.isBefore(reservation.getCheckIn()) && checkOut.isAfter(reservation.getCheckOut()))
            || (checkOut.isAfter(reservation.getCheckIn()) && checkOut.isBefore(reservation.getCheckOut())))
                throw new ReservationException("예약이 불가능한 날짜입니다.");
        }
        //모든 예외가 성립하지 않는다면
        Reservation reservation = createAndSaveReservation(room, reservationRegisterRequest, user);
        Reservation_RegisterResponse reservationResponse = Reservation_RegisterResponse.builder()
                .message("예약 성공").reservationId(reservation.getId()).build();

        EntityModel<Reservation_RegisterResponse> model = EntityModel.of(reservationResponse);
        URI location = linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel().toUri();
        model.add(Link.of("/docs/api.html#resource-reservation-register").withRel("profile"));
        model.add(linkTo(methodOn(ReservationController.class).registerReservation(reservationRegisterRequest, userPrincipal)).withSelfRel());
        return ResponseEntity.created(location)
                .body(model);
    }
    public Reservation createAndSaveReservation(Room room, Reservation_RegisterRequest reservationRegisterRequest, User user) {
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

    /*@GetMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
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

    }*/
    /*@GetMapping(value = "/detail",produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
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
    }*/


   /* @DeleteMapping(produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
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
    }*/
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
