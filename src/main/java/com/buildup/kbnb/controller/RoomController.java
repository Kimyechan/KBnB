package com.buildup.kbnb.controller;

import com.buildup.kbnb.dto.room.RoomDto;
import com.buildup.kbnb.dto.room.check.CheckRoomReq;
import com.buildup.kbnb.dto.room.check.CheckRoomRes;
import com.buildup.kbnb.dto.room.detail.CommentDetail;
import com.buildup.kbnb.dto.room.detail.LocationDetail;
import com.buildup.kbnb.dto.room.detail.ReservationDate;
import com.buildup.kbnb.dto.room.detail.RoomDetail;
import com.buildup.kbnb.dto.room.recommend.RecommendResponse;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.security.CurrentUser;
import com.buildup.kbnb.security.UserPrincipal;
import com.buildup.kbnb.service.CommentService;
import com.buildup.kbnb.service.RoomService;
import com.buildup.kbnb.service.UserRoomService;
import com.buildup.kbnb.service.UserService;
import com.buildup.kbnb.service.reservation.ReservationService;
import com.buildup.kbnb.util.S3Uploader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
                .hostName(room.getHost().getName())
                .hostImgURL(room.getHost().getImageUrl())
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

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestParam Long roomId) {
        Boolean isRecommendedRoom = reservationService.checkRecommendedRoom(roomId);

        RecommendResponse response = RecommendResponse.builder()
                .isRecommendedRoom(isRecommendedRoom)
                .build();

        EntityModel<RecommendResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(RoomController.class).recommend(roomId)).withSelfRel());
        model.add(Link.of("/docs/api.html#resource-room-recommend").withRel("profile"));
        return ResponseEntity.ok().body(model);
    }

    @PostMapping("/upload")
    public String upload(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) throws IOException {
        return s3Uploader.upload(file, "kbnbRoom", userPrincipal.getName());
    }

    @PostMapping("/dummy")
    public String dummy(@RequestBody RoomDummy roomDummy, @CurrentUser UserPrincipal userPrincipal) {
        roomService.createRoomDummyData(roomDummy, userPrincipal);
        return "Ok";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomDummy {
        private List<RoomDummyDetail> roomList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomDummyDetail {
        private String room_type;
        private String country;
        private String city;
        private String borough;
        private String neighborhood;
        private Double latitude;
        private Double longitude;
        private String location;
    }
}
