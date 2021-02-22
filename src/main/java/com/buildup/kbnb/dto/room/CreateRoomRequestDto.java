package com.buildup.kbnb.dto.room;

import com.sun.istack.Nullable;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequestDto {
    @NotNull
    private String name;
    @NotNull
    private String roomType;
    @NotNull
    private Double roomCost;
    @NotNull
    private Double cleaningCost;
    @NotNull
    private Double tax;
    @NotNull
    private Integer peopleLimit;
    @NotNull
    private String description;
    @NotNull
    private LocalTime checkOutTime;
    @NotNull
    private LocalTime checkInTime;
    @NotNull
    private Boolean isSmoking;
    @NotNull
    private Boolean isParking;


    //location
    private String country;
    private String city;
    private String borough;
    private String neighborhood;
    private String detailAddress;
    private Double latitude;
    private Double longitude;

    @Nullable
    List<BedRoomDto> bedRoomDtoList = new ArrayList<>();
    @Nullable
    List<BathRoomDto> bathRoomDtoList = new ArrayList<>();
}
