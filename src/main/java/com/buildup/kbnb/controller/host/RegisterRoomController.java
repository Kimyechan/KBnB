package com.buildup.kbnb.controller.host;

import com.buildup.kbnb.dto.host.HostPhotoResponse;
import com.buildup.kbnb.dto.host.UrlListResponse;
import com.buildup.kbnb.dto.reservation.ReservationDetailResponse;
import com.buildup.kbnb.dto.reservation.ReservationRegisterResponse;
import com.buildup.kbnb.dto.room.CreateRoomRequestDto;
import com.buildup.kbnb.dto.room.CreateRoomResponseDto;
import com.buildup.kbnb.dto.user.UserImgUpdateResponse;
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
import java.lang.reflect.Array;
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
public class RegisterRoomController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    S3Uploader s3Uploader;
    @Autowired
    RoomImgRepository roomImgRepository;


@PostMapping(value = "/registerBasicRoom", produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
public ResponseEntity<?> registerBasicRoom(@CurrentUser UserPrincipal userPrincipal, CreateRoomRequestDto createRoomRequestDto) {
        User user = userService.findById(userPrincipal.getId());

        Room room = roomService.createRoom(user, createRoomRequestDto);
        roomService.save(room);
    CreateRoomResponseDto createRoomResponseDto = CreateRoomResponseDto.builder().roomId(room.getId()).msg("방 기본정보 등록 성공").build();

        EntityModel<CreateRoomResponseDto> model = EntityModel.of(createRoomResponseDto);
    model.add(Link.of("/docs/api.html#resource-host-registerBasicRoom").withRel("profile"));
        return ResponseEntity.ok(model);
    }
    @PostMapping(value = "/addPhoto" , produces = MediaTypes.HAL_JSON_VALUE + ";charset=utf8")
    public ResponseEntity<?> updatePhoto(@CurrentUser UserPrincipal userPrincipal, @RequestParam Long roomId, @RequestPart List<MultipartFile> file) throws IOException {
    User user = userService.findById(userPrincipal.getId());
    Room room = roomService.findById(roomId);
    List<RoomImg> roomImgList = new ArrayList<>();
    int i = 0;
    for(MultipartFile file1 : file) {
        String newUrl = s3Uploader.upload(file1,"roomImg", user.getName() + i++);
        RoomImg roomImg = RoomImg.builder().room(room).url(newUrl).build();
        roomImgRepository.save(roomImg);
        roomImgList.add(roomImg);
    }
    room.setRoomImgList(roomImgList);
    roomService.save(room);

    HostPhotoResponse hostPhotoResponse = HostPhotoResponse.builder()
            .imgCount(file.size()).build();
    EntityModel<HostPhotoResponse> model = EntityModel.of(hostPhotoResponse);
        model.add(Link.of("/docs/api.html#resource-host-addPhoto").withRel("profile"));
    return ResponseEntity.ok(model);
    }
}
