package com.buildup.kbnb.dto.room;

import com.sun.istack.Nullable;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequestDto {
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


    //location
    private String country;
    private String city;
    private String borough;
    private String neighborhood;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    //imgList는 multipartFile로
    @Nullable
    List<BedRoomDto> bedRoomDtoList = new ArrayList<>();
    @Nullable
    List<BathRoomDto> bathRoomDtoList = new ArrayList<>();
}
