package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ReservationRepository reservationRepository;

    User savedHost;
    @BeforeEach
    public void setUp() {
        User host = User.builder()
                .name("테스트 호스트")
                .email("shn03014@naver.com")
                .provider(AuthProvider.google)
                .emailVerified(false)
                .build();
        savedHost = em.persist(host);

        Payment payment = Payment.builder()
                .price(1000)
                .receiptId(String.valueOf(1))
                .build();
        em.persist(payment);

        Room room = Room.builder()
                .host(host).build();
        em.persist(room);

        Reservation reservation = Reservation.builder()
                .room(room)
                .payment(payment)
                .build();
        em.persist(reservation);
    }

    @Test
    @DisplayName("호스트를 통해 예약정보를 Payment 와 함께 가져오기")
    public void findByHostWithPayment() {

        User host = em.find(User.class, savedHost.getId());

        List<Reservation> hostReservation = reservationRepository.findByHostWithPayment(host);
        assertThat(hostReservation.size()).isEqualTo(1);
    }
}
