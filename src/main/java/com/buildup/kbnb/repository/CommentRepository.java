package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.room.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Comment> findByRoom(Room room, Pageable pageable);
}
