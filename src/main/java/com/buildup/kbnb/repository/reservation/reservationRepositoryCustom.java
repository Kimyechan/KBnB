package com.buildup.kbnb.repository.reservation;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface reservationRepositoryCustom {

    @Query("select r from Reservation r join fetch  r.user u where u = :user")
    Page<Reservation>  findByUser(@Param("user") User user);
}
