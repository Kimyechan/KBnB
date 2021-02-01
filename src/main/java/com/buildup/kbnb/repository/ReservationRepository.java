package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
