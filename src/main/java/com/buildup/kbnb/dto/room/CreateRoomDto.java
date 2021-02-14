package com.buildup.kbnb.dto.room;

import com.amazonaws.services.ec2.model.Reservation;
import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.BathRoom;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.RoomImg;
import com.buildup.kbnb.model.user.User;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomDto {
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
    private Integer bedNum;

    private User host;
    private Location location;
    private List<RoomImg> roomImgList;
    private List<BathRoom> bathRoomList;
    private List<BedRoom> bedRoomList;
}
