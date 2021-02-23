package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.dto.host.manage.HostGetRoomRes;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/host")
public class ManageRoomController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;

    @GetMapping(value = "/roomList", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity getRoomList(@CurrentUser UserPrincipal userPrincipal, Pageable pageable,
                                      PagedResourcesAssembler<HostGetRoomRes> assembler) {
        User host = userService.findById(userPrincipal.getId());
        Page<Room> hostRoomPage = roomService.findByHost(host, pageable);
        List<Room> hostRoomList = hostRoomPage.getContent();
        List<HostGetRoomRes> hostGetRoomList = new ArrayList<>();

        for (Room room : hostRoomList) {
            HostGetRoomRes hostGetRoomRes = new HostGetRoomRes();
            hostGetRoomList.add(hostGetRoomRes.createDto(room));
        }

        Page<HostGetRoomRes> listPage = new PageImpl<>(hostGetRoomList, pageable, hostRoomPage.getTotalElements());
        PagedModel<EntityModel<HostGetRoomRes>> model = assembler.toModel(listPage);
        model.add(Link.of("/docs/api.html#resource-host-getRoomList").withRel("profile"));
        return ResponseEntity.ok(model);
    }
}
