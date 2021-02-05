package com.buildup.kbnb.service;

import com.buildup.kbnb.model.Comment;
import com.buildup.kbnb.model.room.Room;
import com.buildup.kbnb.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;


    public Page<Comment> getListByRoomIdWithUser(Room room, Pageable pageable) {
        return commentRepository.findByRoom(room, pageable);
    }
}
