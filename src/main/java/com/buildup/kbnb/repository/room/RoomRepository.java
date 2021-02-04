package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.model.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
}
