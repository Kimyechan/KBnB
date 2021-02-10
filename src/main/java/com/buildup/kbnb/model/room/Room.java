package com.buildup.kbnb.model.room;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.Reservation;
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

    private Double cleanliness;

    private Double accuracy;

    private Double communication;

    private Double locationRate;

    private Double checkIn;

    private Double priceSatisfaction;

    private Integer bedNum;

    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

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

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Reservation> reservationList;

    @PrePersist
    public void prePersist() {
        this.cleanliness = this.cleanliness == null ? 0 : this.cleanliness;
        this.accuracy = this.accuracy == null ? 0 : this.accuracy;
        this.communication = this.communication == null ? 0 : this.communication;
        this.locationRate = this.locationRate == null ? 0 : this.locationRate;
        this.checkIn = this.checkIn == null ? 0 : this.checkIn;
        this.priceSatisfaction = this.priceSatisfaction == null ? 0 : this.priceSatisfaction;
    }
}
