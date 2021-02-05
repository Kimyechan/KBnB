package com.buildup.kbnb.dto.room;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private Integer peopleLimit;
    private Integer bedRoomNum;
    private Integer bedNum;
    private Integer bathRoomNum;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Boolean isSmoking;
    private Boolean isParking;
    private String roomType;
    private Double cost;
    private Double grade;
    private Double latitude;
    private Double longitude;
    private Integer commentCount;
    private Boolean isCheck;
    private List<String> roomImgUrlList;
    // ToDo: RoomImg List 5개
}
