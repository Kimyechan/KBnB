package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.dto.host.Income.IncomeRequest;
import com.buildup.kbnb.dto.host.Income.IncomeResponse;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/host")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    ReservationService reservationService;

    @Autowired
    UserService userService;
    @GetMapping(value = "/income", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> yearMonthIncome(@CurrentUser UserPrincipal userPrincipal, IncomeRequest incomeRequest) {
        User host = userService.findById(userPrincipal.getId());

        //호스트가 가진 예약정보를 reservation - findbyhost 사용해서
        List<Reservation> byYear = reservationService.findByHostWithPaymentFilterByYear(host, incomeRequest.getYear());


        IncomeResponse incomeResponse = new IncomeResponse();
        for(Reservation reservation : byYear) {
            for(int i = 1; i < 10; i++) {
                if (String.valueOf(reservation.getCheckIn()).contains(incomeRequest.getYear() + "-0" + i)) {
                    incomeResponse.add(reservation.getPayment().getPrice(), i);
                    break;
                }
            }
            for(int i = 10; i < 13; i++) {
                if (String.valueOf(reservation.getCheckIn()).contains(incomeRequest.getYear() + "-" + i)) {
                    incomeResponse.add(reservation.getPayment().getPrice(), i);
                    break;
                }
            }

        }
            incomeResponse.setYearlyIncome();
        EntityModel<IncomeResponse> model = EntityModel.of(incomeResponse);
        model.add(Link.of("/docs/api.html#resource-host-income").withRel("profile"));
        return ResponseEntity.ok(model);
    }
}
