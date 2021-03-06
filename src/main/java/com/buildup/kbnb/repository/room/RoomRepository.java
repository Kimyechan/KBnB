package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {

    @Query("select r from Room r join fetch r.host join fetch r.location where r.id = :roomId")
    Optional<Room> findByIdWithUserLocation(@Param("roomId") Long roomId);

    Page<Room> findByHost(User host, Pageable pageable);
}
