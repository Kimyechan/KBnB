package com.buildup.kbnb.model.user;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.UserRoom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate birth;

    @Email
    @Column(nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    private String role = Role.USER.getValue();

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<UserRoom> checkRoomList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Reservation> reservationList;
}
