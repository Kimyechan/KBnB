package com.buildup.kbnb.model.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault(value = "0")
    private Integer queenSize;

    @ColumnDefault(value = "0")
    private Integer doubleSize;

    @ColumnDefault(value = "0")
    private Integer singleSize;

    @ColumnDefault(value = "0")
    private Integer superSingleSize;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @PrePersist
    public void prePersist() {
        this.queenSize = this.queenSize == null ? 0 : this.queenSize;
        this.doubleSize = this.doubleSize == null ? 0 : this.doubleSize;
        this.singleSize = this.singleSize == null ? 0 : this.singleSize;
        this.superSingleSize = this.superSingleSize == null ? 0 : this.superSingleSize;
    }
}
