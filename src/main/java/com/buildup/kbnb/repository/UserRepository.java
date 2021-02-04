package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("select u from User u left join fetch u.checkRoomList where u.id = :id")
    Optional<User> findByIdWithCheckRoom(@Param("id") Long id);

}
