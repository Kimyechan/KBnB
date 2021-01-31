package com.buildup.kbnb.model.room;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BathRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer queenSize;

    private Integer doubleSize;

    private Integer singleSize;

    private Integer superSingleSize;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
}
