package com.buildup.kbnb.model.room;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isPrivate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
}
