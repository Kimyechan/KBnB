package com.buildup.kbnb.repository.reservation;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ReservationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Reservation> findByHostWithPaymentSpecificYear(User Host, int Year) {
        return null;
    }
}
