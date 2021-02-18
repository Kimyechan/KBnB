package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    @Query("select ur from UserRoom ur join fetch ur.room join fetch ur.user where ur.user.id = :userId")
    List<UserRoom> findByUserId(@Param("userId") Long userId);
}
