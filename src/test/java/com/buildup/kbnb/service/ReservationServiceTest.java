package com.buildup.kbnb.service;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.service.reservation.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class ReservationServiceTest {
    @Autowired
    ReservationService reservationService;
    @MockBean
    ReservationRepository reservationRepository;

  @Mock
  User user;
  @Mock
  Payment payment;
    public Payment createPayment() {
        Payment payment = Payment.builder()
                .price(1000)
                .receiptId(String.valueOf(1))
                .build();
        return payment;
    }
    public User createUser() {
        User host = User.builder()
                .name("테스트 호스트")
                .email("shn03014@naver.com")
                .provider(AuthProvider.google)
                .emailVerified(false)
                .build();
        return host;
    }

    public Comment createComment(User user, Room room) {
        Comment comment = Comment.builder()
                .room(room)
                .user(user)
                .build();
        return comment;
    }

    public List<Reservation> createReservationList() {
        List<Reservation> list = new ArrayList<>();
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .payment(new Payment(2L,"2",2000))
                .checkIn(LocalDate.of(2021,2,2))
                .build();

        list.add(reservation2);
        return list;
    }

    @Test
    public void findByHostWithPaymentFilterByYear() {
        User host = createUser();
        given(reservationRepository.findByHostWithPayment(any())).willReturn(createReservationList());


        int beforeYear = 2020; int year = 2021; int afterYear = 2022;
        List<Reservation> reservationList = reservationService.findByHostFilterByYear(host,beforeYear);
        assertThat(reservationList.size()).isEqualTo(0);
        reservationList = reservationService.findByHostFilterByYear(host,year);
        assertThat(reservationList.size()).isEqualTo(1);
        reservationList = reservationService.findByHostFilterByYear(host,afterYear);
        assertThat(reservationList.size()).isEqualTo(0);
    }
}
