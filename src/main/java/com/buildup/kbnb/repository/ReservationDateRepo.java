package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.ReservationDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationDateRepo extends JpaRepository<ReservationDate, Long> {
}
