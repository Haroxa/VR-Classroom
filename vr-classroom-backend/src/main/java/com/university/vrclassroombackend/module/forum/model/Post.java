package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@TableName("post")
@Data
public class Post {
    public static final int STATUS_DELETED = -1;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int SUMMARY_LENGTH = 50;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 格式化后的创建时间，格式为yyyy-MM-dd HH:mm:ss
    @TableField("date")
    private String date;
    
    // MyBatis-Plus 不支持 @PrePersist 和 @PreUpdate，需要在Service层处理
    public void updateDate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = createdAt.format(formatter);
    }
    
    @TableField
    private String title;
    
    @TableField
    private String summary;
    
    @TableField
    private String content;
    
    // MyBatis-Plus 不直接支持 @ElementCollection，需要使用 TypeHandler 或其他方式处理
    @TableField(exist = false)
    private List<String> images;
    
    @TableField("author_id")
    private Integer authorId;
    
    @TableField("category_id")
    private Integer categoryId;
    
    @TableField("like_count")
    private Integer likeCount = 0;
    
    @TableField("share_count")
    private Integer shareCount = 0;
    
    @TableField("comment_count")
    private Integer commentCount = 0;
    
    @TableField
    private Integer status = 0;
    
    @TableField("reject_reason")
    private String rejectReason;
}




