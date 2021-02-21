package com.buildup.kbnb.service;

import com.buildup.kbnb.advice.exception.EmailOrPassWrongException;
import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 비밀번호 일치")
    public void correctPassword() {
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        assertThrows(EmailOrPassWrongException.class,
                () -> userService.checkCorrectPassword("test", "test"));
    }

    @Test
    @DisplayName("로그인 비밀번호 불일치")
    public void incorrectPassword() {
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        assertDoesNotThrow(() -> userService.checkCorrectPassword("test", "test"));
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