package com.buildup.kbnb.service.reservationService;

import com.buildup.kbnb.advice.exception.ReservationException;
import com.buildup.kbnb.model.user.User;
import com.buildup.kbnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService_Reservation {
    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ReservationException("해당 유저가 존재하지 않습니다."));
    }


}
