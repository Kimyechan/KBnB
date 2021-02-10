package com.buildup.kbnb.service;

import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRoomRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoomService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    public boolean checkRoomForUser(Long roomId, User user) {
        List<UserRoom> checkedRoomList = user.getCheckRoomList();

        boolean isCheckRoom = false;
        for (UserRoom userRoom : checkedRoomList) {
            Long checkRoomId = userRoom.getRoom().getId();
            if (checkRoomId.equals(roomId)) {
                userRoomRepository.deleteById(userRoom.getId());
                isCheckRoom = true;
                break;
            }
        }

        if (!isCheckRoom) {
            Room room = roomRepository.findById(roomId).orElseThrow();
            saveUserRoom(room, user);
        }

        return !isCheckRoom;
    }

    private void saveUserRoom(Room room, User user) {
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .build();
        userRoomRepository.save(userRoom);
    }
}
