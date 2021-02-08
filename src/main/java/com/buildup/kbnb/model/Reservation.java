package com.buildup.kbnb.model;

import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Integer guestNum;

    private Long totalCost;

    @ManyToOne(fetch = FetchType.LAZY )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY )
    private Room room;

    private Boolean commentExisted;

    @OneToOne(fetch = FetchType.LAZY)
    private Comment comment;
}
