package com.buildup.kbnb.service.reservation;

import com.buildup.kbnb.dto.host.income.IncomeResponse;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.reservation.ReservationRepository;
import com.buildup.kbnb.service.PaymentService;
import com.buildup.kbnb.util.payment.BootPayApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BootPayApi bootPayApi;

    @Spy
    @InjectMocks
    private ReservationService reservationService;



    @Test
    @DisplayName("지난달 예약내역 조회")
    public void getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(1);

        LocalDate previousStartDate = previous.withDayOfMonth(1);
        LocalDate previousEndDate = now.withDayOfMonth(1).minusDays(1);

        given(reservationRepository.findByBetweenDateAndRoomId(1L, previousStartDate, previousEndDate))
                .willReturn(new ArrayList<>());

        reservationService.getBeforeMonthReservation(1L);

        verify(reservationRepository, times(1)).findByBetweenDateAndRoomId(1L, previousStartDate, previousEndDate);
    }

    @Test
    @DisplayName("지난 달 예약률 조회")
    public void getBeforeMonthReservationRate() {
        Long roomId = 1L;
        List<Reservation> reservations = getReservations();

        doReturn(reservations).when(reservationService).getBeforeMonthReservation(roomId);

        Double reservationRate = reservationService.getBeforeMonthReservationRate(roomId);

        assertThat(reservationRate).isEqualTo(1);
    }

    private List<Reservation> getReservations() {
        Room room = Room.builder()
                .name("test room")
                .build();

        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(1);

        LocalDate previousStartDate = previous.withDayOfMonth(1);
        LocalDate previousEndDate = now.withDayOfMonth(1).minusDays(1);

        List<Reservation> reservations = new ArrayList<>();
        for (LocalDate date = previousStartDate; date.isBefore(previousEndDate); date = date.plusDays(1)) {
            Reservation reservation = Reservation.builder()
                    .checkIn(date)
                    .checkOut(date.plusDays(1))
                    .room(room)
                    .build();
            reservations.add(reservation);
        }
        return reservations;
    }

    @Test
    @DisplayName("예약률로 추천 숙소인지 확인")
    public void checkRecommendedRoom() {
        doReturn(0.9).when(reservationService).getBeforeMonthReservationRate(1L);

        Boolean isRecommendedRoom = reservationService.checkRecommendedRoom(1L);

        assertTrue(isRecommendedRoom);
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
    @DisplayName("호스트로 예약찾고 특정 년만 뽑아내기")
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

    @Test
    @DisplayName("월별 년별합산 테스트")
    public void separateByMonth() {
        List<Reservation> list = createReservationList();
        IncomeResponse incomeResponse = reservationService.separateByMonth(list);
        System.out.println(incomeResponse.getFeb());
        assertThat(incomeResponse.getFeb()).isEqualTo(2000);
//        System.out.println(incomeResponse.getYearlyIncome());
//        assertThat(incomeResponse.getYearlyIncome()).isEqualTo(2000);
    }
}