package com.university.vrclassroombackend.module.forum.repository;

import com.university.vrclassroombackend.module.forum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    
    List<Post> findByStatus(Integer status);
    
    Page<Post> findByStatus(Integer status, Pageable pageable);
    
    List<Post> findByAuthorId(Integer authorId);
    
    Page<Post> findByAuthorId(Integer authorId, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    List<Post> searchPosts(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
}




