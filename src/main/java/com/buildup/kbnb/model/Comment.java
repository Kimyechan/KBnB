package com.buildup.kbnb.model;

import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float cleanliness;

    private Float accuracy;

    private Float communication;

    private Float location;

    private Float checkIn;

    private Float priceSatisfaction;

    private String description;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
