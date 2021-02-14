package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.room.CreateRoomDto;
import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.Role;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/host")
@RequiredArgsConstructor
public class HostController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;


    @PostMapping(value = "/registerRoom", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public Room registerRoom(@CurrentUser UserPrincipal userPrincipal) {
        User user = userService.findById(userPrincipal.getId());
        CreateRoomDto createRoomDto = new CreateRoomDto();
        Room room = roomService.registerRoom(user, createRoomDto);
        return room;
    }


}
