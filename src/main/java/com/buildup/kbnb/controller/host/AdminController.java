package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.dto.host.income.IncomeRequest;
import com.buildup.kbnb.dto.host.income.IncomeResponse;
import com.buildup.kbnb.model.Reservation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<Reservation> byYear = reservationService.findByHostFilterByYear(host, incomeRequest.getYear());
        IncomeResponse incomeResponse = reservationService.separateByMonth(byYear);

        incomeResponse.setYearlyIncome();
        EntityModel<IncomeResponse> model = EntityModel.of(incomeResponse);
        model.add(Link.of("/docs/api.html#resource-host-income").withRel("profile"));
        return ResponseEntity.ok(model);
    }
}
