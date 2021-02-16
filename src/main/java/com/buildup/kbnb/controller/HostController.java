package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.reservation.ReservationDetailResponse;
import com.buildup.kbnb.dto.reservation.ReservationRegisterResponse;
import com.buildup.kbnb.dto.room.CreateRoomRequestDto;
import com.buildup.kbnb.dto.room.CreateRoomResponseDto;
import com.buildup.kbnb.dto.user.UserUpdateRequest;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.LocationRepository;
import com.buildup.kbnb.repository.RoomImgRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.util.S3Uploader;
import com.sun.mail.iap.Response;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/host")
@RequiredArgsConstructor
public class HostController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    S3Uploader s3Uploader;
    @Autowired
    RoomImgRepository roomImgRepository;


@PostMapping(value = "/registerBasicRoom", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")//멀티파트만 추가하면 이상하게 안됨
public ResponseEntity<?> registerBasicRoom(@CurrentUser UserPrincipal userPrincipal, CreateRoomRequestDto createRoomRequestDto) {
        User user = userService.findById(userPrincipal.getId());

        Room room = roomService.createRoom(user, createRoomRequestDto);
    CreateRoomResponseDto createRoomResponseDto = CreateRoomResponseDto.builder().roomId(room.getId()).msg("방 기본정보 등록 성공").build();

        EntityModel<CreateRoomResponseDto> model = EntityModel.of(createRoomResponseDto);
        return ResponseEntity.ok(model);
    }
@PostMapping(value = "/addPhoto", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
public ResponseEntity<?> addPhoto(@CurrentUser UserPrincipal userPrincipal, List<MultipartFile> files, Long roomId) throws IOException {
    Room room = roomService.findById(roomId);
    User user = userService.findById(userPrincipal.getId());
    List<RoomImg> imgUrlList = new ArrayList<>();
    int i = 0;
    for(MultipartFile file : files) {
        String newImgUrl = s3Uploader.upload(file, "roomImg", user.getName()+ i++);
        RoomImg roomImg = RoomImg.builder().url(newImgUrl).room(room).build();
        roomImgRepository.save(roomImg);
        imgUrlList.add(roomImg);
    }
    room.setRoomImgList(imgUrlList);
    EntityModel<Room> model = EntityModel.of(room);//json으로 안말려서
    System.out.println(model);
    System.out.println("===========================================");
    return ResponseEntity.ok(model);//한번에 작성하는 법 테스트 통과 x 실행시도 에러
}
}
