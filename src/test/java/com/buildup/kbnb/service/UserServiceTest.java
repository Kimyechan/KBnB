package com.buildup.kbnb.service;

import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

class UserServiceTest {
    UserService userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("유저가 찜한 숙소 일 때")
    public void checkedRoomByUser() {
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

        user.getCheckRoomList().add(userRoom);

        given(userRepository.findByIdWithCheckRoom(user.getId())).willReturn(java.util.Optional.of(user));

        Boolean isChecked = userService.checkRoomByUser(user.getId(), room.getId());

        assertTrue(isChecked);
    }

    @Test
    @DisplayName("유저가 찜한 숙소가 아닐 때")
    public void uncheckedRoomByUser() {
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

        given(userRepository.findByIdWithCheckRoom(user.getId())).willReturn(java.util.Optional.of(user));

        Boolean isChecked = userService.checkRoomByUser(user.getId(), room.getId());

        assertFalse(isChecked);
    }

}