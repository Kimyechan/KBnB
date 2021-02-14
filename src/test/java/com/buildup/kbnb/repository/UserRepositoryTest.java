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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

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
    @DisplayName("이메일로 유저 찾기")
    public void getByEmail() {
        assertThat(userRepository.findByEmail("test@gmail.com")).isNotEmpty();
    }

    @Test
    @DisplayName("이메일로 유저 존재 확인")
    public void isExistedByEmail() {
        assertThat(userRepository.existsByEmail("test@gmail.com")).isEqualTo(true);
    }

    @Test
    @DisplayName("유저가 찜하기한 방 리스트 조회")
    public void getCheckRoomListById() {
        User user = userRepository.findByIdWithCheckRoom(savedUser.getId()).orElse(null);

        assert user != null;
        assertTrue(Hibernate.isInitialized(user.getCheckRoomList()));
    }
}