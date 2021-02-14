package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.dto.room.check.CheckRoomReq;
import com.buildup.kbnb.dto.room.check.CheckRoomRes;
import com.buildup.kbnb.dto.room.detail.CommentDetail;
import com.buildup.kbnb.dto.room.detail.LocationDetail;
import com.buildup.kbnb.dto.room.detail.ReservationDate;
import com.buildup.kbnb.dto.room.detail.RoomDetail;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.*;
import com.buildup.kbnb.repository.room.RoomRepository;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserRoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservationService.ReservationService;
import com.buildup.kbnb.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class   RoomController {
    private final RoomService roomService;
    private final UserService userService;
    private final CommentService commentService;
    private final UserRoomService userRoomService;
    private final ReservationService reservationService;
    private final S3Uploader s3Uploader;
    private final RoomRepository roomRepository;
    private final BedRoomRepository bedRoomRepository;
    private final BathRoomRepository bathRoomRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RoomImgRepository roomImgRepository;

    @PostMapping("/list")
    public ResponseEntity<?> getRoomList(@RequestBody RoomSearchCondition roomSearchCondition,
                                         Pageable pageable,
                                         PagedResourcesAssembler<RoomDto> assembler,
                                         @CurrentUser UserPrincipal userPrincipal) {
        Page<Room> roomPage = roomService.searchListByCondition(roomSearchCondition, pageable);
        Long userId = getUserIdAndCheckNull(userPrincipal);

        List<RoomDto> roomList = getRoomDtoList(userId, roomPage.getContent());

        Page<RoomDto> result = new PageImpl<>(roomList, pageable, roomPage.getTotalElements());

        PagedModel<EntityModel<RoomDto>> model = assembler.toModel(result);
        model.add(Link.of("/docs/api.html#resource-room-get-list-by-condition").withRel("profile"));

        return ResponseEntity.ok().body(model);
    }

    private Long getUserIdAndCheckNull(UserPrincipal userPrincipal) {
        Long userId;
        if (userPrincipal == null) {
            userId = null;
        } else {
            userId = userPrincipal.getId();
        }
        return userId;
    }

    private List<RoomDto> getRoomDtoList(Long userId, List<Room> roomList) {
        List<RoomDto> roomDtoList = new ArrayList<>();
        for (Room room : roomList) {
            int bedNum = roomService.getBedNum(room.getBedRoomList());
            List<String> roomImgUrlList = getRoomImgUrlList(room);

            RoomDto roomDto = RoomDto.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .roomType(room.getRoomType())
                    .cost(room.getRoomCost())
                    .peopleLimit(room.getPeopleLimit())
                    .grade(room.getGrade())
                    .bedRoomNum(room.getBedRoomList().size())
                    .bedNum(bedNum)
                    .bathRoomNum(room.getBathRoomList().size())
                    .checkInTime(room.getCheckInTime())
                    .checkOutTime(room.getCheckOutTime())
                    .isSmoking(room.getIsSmoking())
                    .isParking(room.getIsParking())
                    .city(room.getLocation().getCity())
                    .borough(room.getLocation().getBorough())
                    .neighborhood(room.getLocation().getNeighborhood())
                    .latitude(room.getLocation().getLatitude())
                    .longitude(room.getLocation().getLongitude())
                    .commentCount(room.getCommentList().size())
                    .isCheck(userService.checkRoomByUser(userId, room.getId()))
                    .roomImgUrlList(roomImgUrlList)
                    .build();

            roomDtoList.add(roomDto);
        }
        return roomDtoList;
    }

    private List<String> getRoomImgUrlList(Room room) {
        List<String> roomImgUrlList = new ArrayList<>();
        int endIdx = Math.min(room.getRoomImgList().size(), 5);

        for (RoomImg roomImg : room.getRoomImgList().subList(0, endIdx)) {
            roomImgUrlList.add(roomImg.getUrl());
        }
        return roomImgUrlList;
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getRoomDetail(@RequestParam("roomId") Long roomId, @CurrentUser UserPrincipal userPrincipal) {
        Room room = roomService.getRoomDetailById(roomId);

        LocationDetail locationDetail = getLocationDetail(room.getLocation());

        Pageable pageable = PageRequest.of(0, 6);
        Page<Comment> commentPage = commentService.getListByRoomIdWithUser(room, pageable);
        List<CommentDetail> commentDetails = getCommentDetails(commentPage.getContent());

        List<String> roomImgUrlList = getRoomImgUrls(room.getRoomImgList());
        List<ReservationDate> reservationDates = reservationService.findByRoomFilterDay(room.getId(), LocalDate.now());

        Long userId = getUserIdAndCheckNull(userPrincipal);
        int bedNum = roomService.getBedNum(room.getBedRoomList());
        RoomDetail roomDetail = getRoomDetail(userId, room, locationDetail, commentPage, commentDetails, roomImgUrlList, reservationDates, bedNum);

        EntityModel<RoomDetail> model = EntityModel.of(roomDetail);
        model.add(linkTo(methodOn(RoomController.class).getRoomDetail(roomId, userPrincipal)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-room-get-detail").withRel("profile"));
        return ResponseEntity.ok().body(model);
    }

    private RoomDetail getRoomDetail(Long userId,
                                     Room room,
                                     LocationDetail locationDetail,
                                     Page<Comment> commentPage,
                                     List<CommentDetail> commentDetails, List<String> roomImgUrlList, List<ReservationDate> reservationDates, int bedNum) {
        return RoomDetail.builder()
                .id(room.getId())
                .name(room.getName())
                .grade(room.getGrade())
                .roomType(room.getRoomType())
                .bedRoomNum(room.getBedRoomList().size())
                .bedNum(bedNum)
                .bathRoomNum(room.getBathRoomList().size())
                .roomCost(room.getRoomCost())
                .cleaningCost(room.getCleaningCost())
                .tax(room.getTax())
                .checkInTime(room.getCheckInTime())
                .checkOutTime(room.getCheckOutTime())
                .peopleLimit(room.getPeopleLimit())
                .description(room.getDescription())
                .isSmoking(room.getIsSmoking())
                .isParking(room.getIsParking())
                .locationDetail(locationDetail)
                .roomImgUrlList(roomImgUrlList)
                .commentCount(commentPage.getTotalElements())
                .commentList(commentDetails)
                .reservationDates(reservationDates)
                .isChecked(userService.checkRoomByUser(userId, room.getId()))
                .build();
    }

    private List<String> getRoomImgUrls(List<RoomImg> roomImgList) {
        List<String> roomImgUrlList = new ArrayList<>();
        for (RoomImg roomImg : roomImgList) {
            roomImgUrlList.add(roomImg.getUrl());
        }
        return roomImgUrlList;
    }

    private List<CommentDetail> getCommentDetails(List<Comment> commentList) {
        List<CommentDetail> commentDetails = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDetail commentDetail = CommentDetail.builder()
                    .id(comment.getId())
                    .description(comment.getDescription())
                    .date(comment.getDate())
                    .userName(comment.getUser().getName())
                    .userImgUrl(comment.getUser().getImageUrl())
                    .build();

            commentDetails.add(commentDetail);
        }
        return commentDetails;
    }

    private LocationDetail getLocationDetail(Location location) {
        return LocationDetail.builder()
                .country(location.getCountry())
                .city(location.getCity())
                .borough(location.getBorough())
                .neighborhood(location.getNeighborhood())
                .detailAddress(location.getDetailAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    @PatchMapping("/check")
    public ResponseEntity<?> checkRoom(@RequestBody CheckRoomReq checkRoomReq,
                                       @CurrentUser UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        Boolean isChecked = userRoomService.checkRoomForUser(checkRoomReq.getRoomId(), userId);
        CheckRoomRes res = CheckRoomRes.builder()
                .roomId(checkRoomReq.getRoomId())
                .isChecked(isChecked)
                .build();

        EntityModel<CheckRoomRes> model = EntityModel.of(res);
        model.add(linkTo(methodOn(RoomController.class).checkRoom(checkRoomReq, userPrincipal)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-room-check").withRel("profile"));
        return ResponseEntity.ok().body(model);
    }


    @PostMapping("/upload")
    public String upload(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) throws IOException {
        return s3Uploader.upload(file, "kbnbRoom", userPrincipal.getName());
    }

    @GetMapping("/test")
    public String getRoomListTest(@CurrentUser UserPrincipal userPrincipal,
                                  @RequestParam Integer numberOfRoom) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        for (int i = 0; i < numberOfRoom; i++) {
//            Location location = Location.builder()
//                    .latitude(37.55559028863329 + 0.001 * i)
//                    .longitude(126.76740548073847 + 0.002 * i)
//                    .build();
            Location location = Location.builder()
                    .latitude(37.5051891 + 0.001 * i)
                    .longitude(126.9774869 + 0.002 * i)
                    .build();
            locationRepository.save(location);

            Room room = Room.builder()
                    .name("test room name 2")
                    .roomType("Shared room")
                    .host(user)
                    .location(location)
                    .roomCost(10000.0)
                    .peopleLimit(4)
                    .checkInTime(LocalTime.of(15, 0))
                    .checkOutTime(LocalTime.of(13, 0))
                    .isSmoking(false)
                    .isParking(false)
                    .grade(0.0)
                    .bedNum(4)
                    .build();
            roomRepository.save(room);
            for (int j = 0; j < 5; j++) {
                RoomImg roomImg = RoomImg.builder()
                        .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-05T22%3A49%3A59.421617.png")
                        .room(room)
                        .build();
                roomImgRepository.save(roomImg);
            }

            BathRoom bathRoom = BathRoom.builder()
                    .isPrivate(true)
                    .room(room)
                    .build();
            bathRoomRepository.save(bathRoom);

            BedRoom bedRoom1 = BedRoom.builder()
                    .doubleSize(2)
                    .room(room)
                    .build();
            bedRoomRepository.save(bedRoom1);

            BedRoom bedRoom2 = BedRoom.builder()
                    .doubleSize(2)
                    .room(room)
                    .build();
            bedRoomRepository.save(bedRoom2);
        }

//        Location location = Location.builder()
//                .latitude(37.5051891 + 0.001 )
//                .longitude(126.9774869 + 0.002 )
//                .build();
//        locationRepository.save(location);
//
//        Room room = Room.builder()
//                .name("test room name 2")
//                .roomType("Shared room")
//                .host(user)
//                .location(location)
//                .roomCost(10000.0)
//                .peopleLimit(4)
//                .checkInTime(LocalTime.of(15, 0))
//                .checkOutTime(LocalTime.of(13, 0))
//                .isSmoking(false)
//                .isParking(false)
//                .grade(0.0)
//                .bedNum(4)
//                .build();
//        roomRepository.save(room);
//        for (int j = 0; j < 5; j++) {
//            RoomImg roomImg = RoomImg.builder()
//                    .url("https://pungdong.s3.ap-northeast-2.amazonaws.com/kbnbRoom/12021-02-05T22%3A49%3A59.421617.png")
//                    .room(room)
//                    .build();
//            roomImgRepository.save(roomImg);
//        }
//
//        BathRoom bathRoom = BathRoom.builder()
//                .isPrivate(true)
//                .room(room)
//                .build();
//        bathRoomRepository.save(bathRoom);
//
//        BedRoom bedRoom1 = BedRoom.builder()
//                .doubleSize(2)
//                .room(room)
//                .build();
//        bedRoomRepository.save(bedRoom1);
//
//        BedRoom bedRoom2 = BedRoom.builder()
//                .doubleSize(2)
//                .room(room)
//                .build();
//        bedRoomRepository.save(bedRoom2);

        return "ok";
    }
}
