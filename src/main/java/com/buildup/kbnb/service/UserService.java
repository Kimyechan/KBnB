package com.buildup.kbnb.service;

import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Boolean checkRoomByUser(Long userId, Long roomId) {
        User user = userRepository.findByIdWithCheckRoom(userId).orElseThrow();
        List<UserRoom> userRooms = user.getCheckRoomList();
        for (UserRoom userRoom : userRooms) {
            if (userRoom.getRoom().getId().equals(roomId)) {
                return true;
            }
        }
        return false;
    }
}
