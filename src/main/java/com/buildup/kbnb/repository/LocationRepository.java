package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
