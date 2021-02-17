package com.buildup.kbnb.service;

import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import com.buildup.kbnb.repository.UserRoomRepository;
import com.buildup.kbnb.repository.room.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserRoomServiceTest {
    UserRoomService userRoomService;

    @Mock
    RoomRepository roomRepository;

    @Mock
    UserRoomRepository userRoomRepository;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        userRoomService = new UserRoomService(roomRepository, userRoomRepository, userRepository);
    }

    @Test
    @DisplayName("유저가 숙소 찜하기 취소")
    public void uncheckRoomByUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .emailVerified(false)
                .provider(AuthProvider.local)
                .checkRoomList(new ArrayList<>())
                .build();

        Room room = Room.builder()
                .id(1L)
                .name("test room")
                .build();

        UserRoom userRoom = UserRoom.builder()
                .id(1L)
                .user(user)
                .room(room)
                .build();
        List<UserRoom> userRooms = new ArrayList<>();
        userRooms.add(userRoom);

        given(userRoomRepository.findByUserId(user.getId())).willReturn(userRooms);

        Boolean isChecked = userRoomService.checkRoomForUser(room.getId(), user.getId());

        assertFalse(isChecked);
        verify(userRoomRepository, times(1)).deleteById(userRoom.getId());
    }

    @Test
    @DisplayName("유저가 숙소 찜하기")
    public void checkRoomByUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .emailVerified(false)
                .provider(AuthProvider.local)
                .checkRoomList(new ArrayList<>())
                .build();

        Room room1 = Room.builder()
                .id(1L)
                .name("test room")
                .build();

        Room room2 = Room.builder()
                .id(2L)
                .name("test room")
                .build();

        UserRoom userRoom = UserRoom.builder()
                .id(1L)
                .user(user)
                .room(room1)
                .build();
        List<UserRoom> userRooms = new ArrayList<>();
        userRooms.add(userRoom);

        given(userRoomRepository.findByUserId(user.getId())).willReturn(userRooms);
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.of(user));
        given(roomRepository.findById(room2.getId())).willReturn(java.util.Optional.of(room2));

        Boolean isChecked = userRoomService.checkRoomForUser(2L, user.getId());

        assertTrue(isChecked);

        verify(userRoomRepository, times(1)).save(any());
    }
}