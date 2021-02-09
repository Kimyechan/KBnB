package com.buildup.kbnb.model;

import com.buildup.kbnb.model.room.Room;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDate {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
}
