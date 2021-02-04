package com.buildup.kbnb.model.room;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Double grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RoomImg> roomImgList;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<BathRoom> bathRoomList;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<BedRoom> bedRoomList;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Comment> commentList;
}
