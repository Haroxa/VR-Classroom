package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByStatus(Integer status);
    List<Post> findByCategoryId(Integer categoryId);
    List<Post> findByAuthorId(Integer authorId);
    
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    List<Post> searchPosts(@Param("keyword") String keyword);
}
