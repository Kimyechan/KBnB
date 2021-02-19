package com.buildup.kbnb.repository.reservation;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.user.User;

import java.util.List;

public interface ReservationRepositoryCustom {
    List<Reservation> findByHostWithPaymentSpecificYear(User Host, int Year);

}
