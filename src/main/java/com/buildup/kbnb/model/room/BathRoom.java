package com.buildup.kbnb.model.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Boolean isPrivate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
}
