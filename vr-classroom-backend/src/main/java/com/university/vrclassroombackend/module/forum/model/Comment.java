package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TableName("comment")
@Data
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
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
    private String content;
    
    @TableField("commenter_id")
    private Integer commenterId;
    
    @TableField("post_id")
    private Integer postId;
    
    @TableField("like_count")
    private Integer likeCount = 0;
    
    @TableField
    private Integer status = 0;
    
    @TableField("reject_reason")
    private String rejectReason;
}




