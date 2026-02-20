package com.university.vrclassroombackend.domain.forum.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "post")
@Data
public class Post {
    public static final int STATUS_DELETED = -1;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int SUMMARY_LENGTH = 50;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 格式化后的创建时间，格式为 yyyy-MM-dd HH:mm:ss
    @Column(name = "date", nullable = false)
    private String date;
    
    @PrePersist
    @PreUpdate
    protected void updateDate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = createdAt.format(formatter);
    }
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String summary;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ElementCollection
    private List<String> images;
    
    @Column(nullable = false)
    private Integer authorId;
    
    private Integer categoryId;
    
    @Column(nullable = false)
    private Integer likeCount = 0;
    
    @Column(nullable = false)
    private Integer shareCount = 0;
    
    @Column(nullable = false)
    private Integer commentCount = 0;
    
    @Column(nullable = false)
    private Integer status = 0;
    
    private String rejectReason;
}
