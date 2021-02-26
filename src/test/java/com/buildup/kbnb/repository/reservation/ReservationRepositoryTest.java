package com.buildup.kbnb.repository.reservation;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    TestEntityManager em;

    Room savedRoom;

    @Test
    @DisplayName("특정 숙소의 기간 내 예약 리스트 조회")
    void findBeforeMonthReservation() {
        LocalDate startDate = LocalDate.of(2021, 2, 1);
        LocalDate endDate = LocalDate.of(2021, 2, 28);

        long reservationCount = saveReservationBetweenDate(startDate, endDate);

        List<Reservation> reservations =
                reservationRepository.findByBetweenDateAndRoomId(savedRoom.getId(), startDate, endDate);

        assertThat(reservations.size()).isEqualTo(reservationCount);

        for (Reservation reservation : reservations) {
            assertThat(reservation.getCheckIn()).isBefore(endDate);
            assertThat(reservation.getCheckIn()).isAfterOrEqualTo(startDate);
        }
    }

    public long saveReservationBetweenDate(LocalDate startDate, LocalDate endDate) {
        Room room = Room.builder()
                .name("test room")
                .build();
        savedRoom = em.persist(room);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Reservation reservation = Reservation.builder()
                    .checkIn(date)
                    .checkOut(date.plusDays(1))
                    .room(room)
                    .build();

            em.persist(reservation);
        }
        em.flush();
        em.clear();

        return ChronoUnit.DAYS.between(startDate, endDate);
    }
}