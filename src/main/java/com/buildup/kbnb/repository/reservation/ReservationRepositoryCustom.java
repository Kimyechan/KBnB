package com.buildup.kbnb.repository.reservation;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface ReservationRepositoryCustom {
    @Query("select r from Reservation r join fetch r.user u where u=:user")
    List<Reservation> findByUserId(@Param("user") User user);

    @Query("select reservation from Reservation reservation join fetch reservation.room room where room.id =:id")
    List<com.buildup.kbnb.model.Reservation> findByRoomId(@Param("id") Long id);
}
