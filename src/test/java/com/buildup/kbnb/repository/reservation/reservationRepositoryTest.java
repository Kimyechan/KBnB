package com.buildup.kbnb.repository.reservation;

import com.amazonaws.services.ec2.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class reservationRepositoryTest {

    @Autowired
    ReservationRepository reservationRepository;

    @BeforeEach
    public void init() {

    }
/*    @Test
    @DisplayName("유저 별 예약 내역 검색")
    public void findByUser() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Reservation> reservationPage = reservationRepository.findByUser()

    }*/
}
