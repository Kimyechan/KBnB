package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.model.user.AuthProvider;
import com.buildup.kbnb.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        Room room = Room.builder()
                .name("test room name 2")
                .roomType("Shared room")
                .build();
        entityManager.persist(room);

        for (int i = 0; i < 20; i++) {
            User user = User.builder()
                    .name("test" + i)
                    .email("test" + i + "@gmail.com")
                    .emailVerified(false)
                    .provider(AuthProvider.local)
                    .build();
            entityManager.persist(user);

            Comment comment = Comment.builder()
                    .user(user)
                    .room(room)
                    .description("test description")
                    .build();
            entityManager.persist(comment);
        }
        entityManager.flush();
    }

    @Test
    @DisplayName("댓글 정보 유저에서 가져오기")
    public void getCommentWithUser() {
        Room room = entityManager.find(Room.class,1L);
        Pageable pageable = PageRequest.of(1, 6);

        Page<Comment> comments = commentRepository.findByRoom(room, pageable);

        for (Comment comment : comments.getContent()) {
            assertThat(comment).isNotNull();
            assertThat(comment.getUser()).isNotNull();
        }
    }
}