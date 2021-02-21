package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.dto.reservation.ReservationConfirmedResponse;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("host")
public class ManageRoomController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @PostMapping(value = "/roomList" , produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public void getRoomList(@CurrentUser UserPrincipal userPrincipal, Pageable pageable,
                            PagedResourcesAssembler<ReservationConfirmedResponse> assembler) {
        User host = userService.findById(userPrincipal.getId());
        Page<Room> hostRoomPage = roomService.findByHost(host, pageable);
        List<Room> hostRoomList = hostRoomPage.getContent();



    }
}
