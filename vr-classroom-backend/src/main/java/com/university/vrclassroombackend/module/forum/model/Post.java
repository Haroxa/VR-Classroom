package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

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
    
    @TableField("date")
    private String date;
    
    @TableField
    private String title;
    
    @TableField
    private String summary;
    
    @TableField
    private String content;
    
    @TableField
    private String images = "[]";
    
    @TableField("authorId")
    private Integer authorId;
    
    @TableField("categoryId")
    private Integer categoryId;
    
    @TableField("likeCount")
    private Integer likeCount = 0;
    
    @TableField("shareCount")
    private Integer shareCount = 0;
    
    @TableField("commentCount")
    private Integer commentCount = 0;
    
    @TableField
    private Integer status = 0;
    
    @TableField("rejectReason")
    private String rejectReason;
}




