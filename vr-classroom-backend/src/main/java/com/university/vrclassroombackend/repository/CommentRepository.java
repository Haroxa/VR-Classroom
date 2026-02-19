package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId ORDER BY c.createdAt DESC")
    List<Comment> findByPostId(@Param("postId") Integer postId);
    
    @Query("SELECT c FROM Comment c WHERE c.commenterId = :commenterId ORDER BY c.createdAt DESC")
    List<Comment> findByCommenterId(@Param("commenterId") Integer commenterId);
}
