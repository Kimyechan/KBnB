package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.model.Reservation;
import com.buildup.kbnb.model.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {

    @Query("select r from Room r join fetch r.host join fetch r.location where r.id = :roomId")
    Optional<Room> findByIdWithUserLocation(@Param("roomId") Long roomId);

}
