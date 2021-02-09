package com.buildup.kbnb.service;

import com.buildup.kbnb.model.Location;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.CommentRepository;
import com.buildup.kbnb.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

}
