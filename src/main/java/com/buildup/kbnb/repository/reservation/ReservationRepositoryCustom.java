package com.buildup.kbnb.repository.reservation;
import com.buildup.kbnb.model.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.buildup.kbnb.model.Reservation;

import java.util.List;
public interface ReservationRepositoryCustom {
    @Query("select r from Reservation r join fetch r.user u where u=:user")
    List<Reservation> findByUser(@Param("user") User user);

    @Query("select reservation from Reservation reservation join fetch reservation.room room where room.id =:id")
    List<Reservation> findByRoomId(@Param("id") Long id);
}
