package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.config.RestDocsConfiguration;
import com.buildup.kbnb.dto.host.income.IncomeResponse;
import com.buildup.kbnb.model.Payment;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CustomUserDetailsService;
import com.buildup.kbnb.security.TokenProvider;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservation.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@Transactional
public class RegisterRoomControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    UserService userService;

    @MockBean
    ReservationService reservationService;

    public User createUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("test@google.com").name("정한솔")
                .password("111").build();
        given(customUserDetailsService.loadUserById(user.getId()))
                .willReturn(UserPrincipal.create(user));

        return user;
    }
    public List<Reservation> createReservationList() {
        List<Reservation> list = new ArrayList<>();
            Reservation reservation1 = Reservation.builder()
                    .checkIn(LocalDate.of(2020,2,2))
                    .payment(new Payment(1L,"1",1000))
                    .build();
            Reservation reservation2 = Reservation.builder()
                    .checkIn(LocalDate.of(2021,2,2))
                    .payment(new Payment(1L,"1",1000))
                    .build();
            list.add(reservation1); list.add(reservation2);
            return list;
    }
    public IncomeResponse createIncomeResponse() {
        IncomeResponse incomeResponse = IncomeResponse.builder()
                .Feb(2000)
                .yearlyIncome(2000)
                .build();
        return incomeResponse;
    }
    @Test
    @DisplayName("수입 테스트 통과")
    public void incomeTest() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByHostFilterByYear(any(), anyInt())).willReturn(createReservationList());
        given(reservationService.separateByMonth(any())).willReturn(createIncomeResponse());
        mockMvc.perform(get("/host/income")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .param("year", String.valueOf(2020))
                .param("month", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("host-income",
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON 타입")
                        ),
                        requestParameters(
                                parameterWithName("year").description("연도"),
                                parameterWithName("month").description("월")
                        ),
                        responseFields(
                                fieldWithPath("yearlyIncome").description("연도 수입"),
                                fieldWithPath("jan").description("1월"),
                                fieldWithPath("feb").description("2월"),
                                fieldWithPath("mar").description("3월"),
                                fieldWithPath("apr").description("4월"),
                                fieldWithPath("may").description("5월"),
                                fieldWithPath("jun").description("6월"),
                                fieldWithPath("jul").description("7월"),
                                fieldWithPath("agu").description("8월"),
                                fieldWithPath("sep").description("9월"),
                                fieldWithPath("oct").description("10월"),
                                fieldWithPath("nov").description("11월"),
                                fieldWithPath("dec").description("12월"),
                                fieldWithPath("_links.profile.href").description("해당 API문서 URL")
                        )
                ));
    }

    @Test
    @DisplayName("수입 테스트 0")
    public void incomeTest0() throws Exception {
        User user = createUser();
        String token = tokenProvider.createToken(String.valueOf(user.getId()));

        given(userService.findById(any())).willReturn(user);
        given(reservationService.findByHostFilterByYear(any(), anyInt())).willReturn(createReservationList());
        given(reservationService.separateByMonth(any())).willReturn(createIncomeResponse());
        mockMvc.perform(get("/host/income")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .param("year", String.valueOf(2020))
                .param("month", String.valueOf(1)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
