package com.buildup.kbnb.service;

import com.buildup.kbnb.advice.exception.EmailDuplicationException;
import com.buildup.kbnb.advice.exception.EmailOrPassWrongException;
import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.dto.user.LoginRequest;
import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean checkRoomByUser(Long userId, Long roomId) {
        if (userId == null) {
            return false;
        }

        User user = userRepository.findByIdWithCheckRoom(userId).orElseThrow();
        List<UserRoom> userRooms = user.getCheckRoomList();
        for (UserRoom userRoom : userRooms) {
            if (userRoom.getRoom().getId().equals(roomId)) {
                return true;
            }
        }
        return false;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ReservationException("해당 유저를 찾을 수 없습니다."));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(EmailOrPassWrongException::new);
    }

    public void checkCorrectPassword(String decodingPassword, String encodingPassword) {
        if (!passwordEncoder.matches(decodingPassword, encodingPassword)) {
            throw new EmailOrPassWrongException();
        }
    }

    public void checkEmailExisted(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new EmailDuplicationException();
        }
    }
}
