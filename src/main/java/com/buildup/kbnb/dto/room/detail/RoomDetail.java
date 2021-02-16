package com.buildup.kbnb.dto.room.detail;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetail {
    private Long id;
    private String name;
    private String roomType;
    private Double roomCost;
    private Double cleaningCost;
    private Double tax;
    private Integer peopleLimit;
    private String description;
    private LocalTime checkOutTime;
    private LocalTime checkInTime;
    private Boolean isSmoking;
    private Boolean isParking;
    private Integer bedRoomNum;
    private Integer bedNum;
    private Integer bathRoomNum;
    private Double grade;

    private String hostName;
    private String hostImgURL;

    private Long commentCount;
    LocationDetail locationDetail;
    private List<String> roomImgUrlList;
    private List<CommentDetail> commentList;
    private List<ReservationDate> reservationDates;
    private Boolean isChecked;
}
