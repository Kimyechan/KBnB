package com.buildup.kbnb.dto.room;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.User;
import com.sun.istack.Nullable;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.File;
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
