package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.room.RoomImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomImgRepository extends JpaRepository<RoomImg, Long> {
}
