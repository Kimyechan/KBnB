package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.room.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {
    Page<Room> searchByCondition(RoomSearchCondition condition, Pageable pageable);
}
