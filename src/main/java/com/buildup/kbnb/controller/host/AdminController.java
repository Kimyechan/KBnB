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
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/host")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    ReservationService reservationService;

    @Autowired
    UserService userService;
    @GetMapping(value = "/income", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> yearMonthIncome(@CurrentUser UserPrincipal userPrincipal, @RequestBody IncomeRequest incomeRequest) {
        User host = userService.findById(userPrincipal.getId());

        //호스트가 가진 예약정보를 reservation - findbyhost 사용해서
//        Reservation reservation = reservationService.findbyHostWithPayment();

        IncomeResponse incomeResponse = new IncomeResponse();
//        incomeResponse.setYearlyIncome();
        EntityModel<IncomeResponse> model = EntityModel.of(incomeResponse);
        return ResponseEntity.ok(model);
    }
}
