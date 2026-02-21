package com.university.vrclassroombackend.module.forum.repository;

import com.university.vrclassroombackend.module.forum.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    List<Comment> findByPostId(Integer postId);
    
    Page<Comment> findByPostId(Integer postId, Pageable pageable);
    
    List<Comment> findByCommenterId(Integer commenterId);
    
    Page<Comment> findByCommenterId(Integer commenterId, Pageable pageable);
}




