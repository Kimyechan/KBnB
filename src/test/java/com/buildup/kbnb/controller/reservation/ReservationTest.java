package com.buildup.kbnb.controller.reservation;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.ReservationRepository;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.reservationService.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
public class ReservationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    ReservationRepository reservationRepository;

    @MockBean
    ReservationService reservationService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoomRepository roomRepository;

    User user;

    Room room;

    Reservation reservation;


    @BeforeEach
    public void createUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .birth(LocalDate.of(1999, 7, 18))
                .email("test@gmail.com")
                .password(passwordEncoder.encode("test"))
                .imageUrl("Image URL")
                .provider(AuthProvider.local)
                .emailVerified(false)
                .build();


        this.user = user;
        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));
    }

    @BeforeEach
    public void createRoom() {
        Room room = Room.builder()
                .id(1L)
                .name("this is room name")
                .host(user)
                .build();
        this.room = room;
    }

    @BeforeEach
    public void createReservation() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .room(room)
                .build();

        this.reservation = reservation;
        given(reservationService.findByUserId(any())).willReturn(reservation);
    }

    @Test
    public void detailTest() throws Exception {
        String token = tokenProvider.createToken(String.valueOf(user.getId()));
        List<Long> list = new ArrayList<>();
        list.add(1L);
        given(userRepository.findById(any())).willReturn(java.util.Optional.of(user));
        given(reservationRepository.findByUserId(user.getId()).stream().map(com.buildup.kbnb.model.Reservation::getId).collect(Collectors.toList()))
                .willReturn(list);


        Map<String, String> map = new HashMap<>();
        map.put("reservationId", "예약 번호");
        mockMvc.perform(get("/reservation/detail")
                .param("reservationId", String.valueOf(1))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
