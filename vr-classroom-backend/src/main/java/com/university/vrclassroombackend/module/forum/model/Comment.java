package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("comment")
@Data
public class Comment {
    public static final int STATUS_DELETED = -1;
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_REJECTED = 2;
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("date")
    private LocalDateTime date;
    
    @TableField
    private String content;
    
    @TableField("commenterId")
    private Integer commenterId;
    
    @TableField("postId")
    private Integer postId;
    
    @TableField("likeCount")
    private Integer likeCount = 0;
    
    @TableField
    private Integer status = 0;
    
    @TableField("rejectReason")
    private String rejectReason;
}




