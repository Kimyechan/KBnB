package com.buildup.kbnb.repository;

import com.buildup.kbnb.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
