package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
}
