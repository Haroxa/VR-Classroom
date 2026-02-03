package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(Integer postId);
    List<Comment> findByCommenterId(Integer commenterId);
}
