package com.buildup.kbnb.service.reservationService;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.dto.room.detail.ReservationDate;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;

import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.reservation.ReservationRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

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
}
