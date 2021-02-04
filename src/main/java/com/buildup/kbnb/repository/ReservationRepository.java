package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByUser(User user, Pageable page);
    Page<Reservation> findAll(Pageable page);
    @Query("select r from Reservation r where r.user.id = :userId")
    List<Reservation> findByUserId(@Param("userId") Long userId);


}
