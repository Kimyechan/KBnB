package com.buildup.kbnb.model.room;

import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<UserRoom> check;
}
