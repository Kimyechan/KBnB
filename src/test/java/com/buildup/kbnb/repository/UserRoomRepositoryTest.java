package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.UserRoom;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRoomRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRoomRepository userRoomRepository;

    User savedUser;
    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .name("test")
                .email("test@gmail.com")
                .emailVerified(false)
                .provider(AuthProvider.local)
                .build();
        savedUser = entityManager.persist(user);

        Room room = Room.builder()
                .name("test room")
                .build();
        entityManager.persist(room);

        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .build();
        entityManager.persist(userRoom);

        entityManager.flush();
        entityManager.clear();
    }
    @Test
    @DisplayName("유저 식별자로 찜한 방 리스트 찾기")
    public void findRoomListByUserId() {
        List<UserRoom> userRoomList = userRoomRepository.findByUserId(savedUser.getId());

        assertThat(userRoomList.size()).isEqualTo(1);
        assertTrue(Hibernate.isInitialized(userRoomList.get(0).getRoom()));
        assertTrue(Hibernate.isInitialized(userRoomList.get(0).getUser()));
    }
}