package com.buildup.kbnb.service.reservationService;

import com.buildup.kbnb.advice.exception.BadRequestException;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> new BadRequestException("there is no reservation which reservationId = " + reservationId));
    }
  /*  public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }*/

}
