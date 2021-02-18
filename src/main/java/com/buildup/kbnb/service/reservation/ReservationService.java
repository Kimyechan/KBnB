package com.buildup.kbnb.service.reservation;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.dto.reservation.ReservationConfirmedResponse;
import com.buildup.kbnb.dto.reservation.ReservationDetailResponse;
import com.buildup.kbnb.dto.room.detail.ReservationDate;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.service.PaymentService;
import com.buildup.kbnb.util.payment.BootPayApi;
import com.buildup.kbnb.util.payment.model.request.Cancel;
import com.buildup.kbnb.util.payment.model.response.ResDefault;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;
    private final BootPayApi bootPayApi;

    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new BadRequestException("there is no reservation which reservationId = " + reservationId));
    }
    public List<Reservation> findByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Page<Reservation> findPageByUser(User user, Pageable pageable) {
        return reservationRepository.findByUser(user, pageable);
    }

    public String getHostName(Reservation reservation) {
        return reservation.getRoom().getHost().getName();
    }

    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation updateWithComment(Reservation reservation, Comment comment) {
        reservation.setComment(comment);
        reservation.setCommentExisted(true);
        return reservationRepository.save(reservation);
    }

    public Reservation findByIdWithRoomAndUser(Long reservationId) {
        return reservationRepository.findByIdWithRoomAndUser(reservationId).orElseThrow();
    }

    public List<ReservationDate> findByRoomFilterDay(Long roomId, LocalDate date) {
        return reservationRepository.findByRoomFromCurrent(roomId, date);
    }

    public List<ReservationConfirmedResponse> createResponseList(List<Reservation> reservationList) {
        List<ReservationConfirmedResponse> reservation_confirmedResponseList = new ArrayList<>();
        for(Reservation reservation : reservationList) {
            Room room = reservation.getRoom(); Location location = room.getLocation();
            ReservationConfirmedResponse reservation_confirmedResponse = ReservationConfirmedResponse.builder()
                    .reservationId(reservation.getId()).checkIn(reservation.getCheckIn()).checkOut(reservation.getCheckOut())
                    .hostName(getHostName(reservation)).imgUrl(room.getRoomImgList().get(0).getUrl()).roomName(room.getName())
                    .roomId(room.getId()).roomLocation(location.getCountry() + " " + location.getCity() + " " +  location.getBorough() + " " +  location.getNeighborhood() + " " + location.getDetailAddress())
                    .status("예약 완료").build();

            if(reservation_confirmedResponse.getCheckOut().isBefore(LocalDate.now()))
                reservation_confirmedResponse.setStatus("완료된 여정");
            reservation_confirmedResponseList.add(reservation_confirmedResponse);
        }
        return reservation_confirmedResponseList;
    }
    public ReservationDetailResponse judgeReservationIdUserHaveContainReservationId(List<Long> reservationIdUserHave, Long reservationId) {
        ReservationDetailResponse reservationDetailResponse;
        if(reservationIdUserHave.contains(reservationId))
            reservationDetailResponse = ifReservationIdExist(reservationId);
        else throw new ReservationException("해당 유저의 예약 리스트에는 요청한 예약건이 없습니다.");
        return reservationDetailResponse;
    }

    public ReservationDetailResponse ifReservationIdExist(Long reservationId) {
        Reservation reservation = findById(reservationId);
        List<BedRoom> bedRoomList = reservation.getRoom().getBedRoomList();
        int bedRoomNum = bedRoomList.size();
        int bedNum = reservation.getRoom().getBedNum();
        ReservationDetailResponse reservation_detail_response = ReservationDetailResponse.builder()
                .hostImage(reservation.getRoom().getHost().getImageUrl())
                .roomImage(reservation.getRoom().getRoomImgList().get(0).getUrl())
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

    public Reservation saveWithPayment(Reservation reservation, Payment payment) throws Exception {
        String token = bootPayApi.getAccessToken();

        bootPayApi.verify(token, payment.getReceiptId(), payment.getPrice());

        Payment savedPayment = paymentService.savePayment(payment);
        reservation.setPayment(savedPayment);
        Reservation savedReservation = save(reservation);

        ResponseEntity<ResDefault> res = bootPayApi.confirm(token, payment.getReceiptId());
        bootPayApi.checkConfirm(res);
        return savedReservation;
    }

    public void cancelReservation(Long reservationId, Cancel cancel) throws Exception {
        Reservation reservation = findById(reservationId);
        Payment payment = reservation.getPayment();
        cancel.setReceipt_id(payment.getReceiptId());

        paymentService.deleteById(payment.getId());
        deleteById(reservationId);

        String token = bootPayApi.getAccessToken();
        bootPayApi.cancel(cancel, token);

    }
}
