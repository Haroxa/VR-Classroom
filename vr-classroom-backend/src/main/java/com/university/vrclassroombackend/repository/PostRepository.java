package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Post> findByStatus(@Param("status") Integer status);
    
    @Query("SELECT p FROM Post p WHERE p.categoryId = :categoryId ORDER BY p.createdAt DESC")
    List<Post> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId ORDER BY p.createdAt DESC")
    List<Post> findByAuthorId(@Param("authorId") Integer authorId);
    
    @Query("SELECT p FROM Post p WHERE p.status = 1 AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.createdAt DESC")
    List<Post> findAllPublished();
}
