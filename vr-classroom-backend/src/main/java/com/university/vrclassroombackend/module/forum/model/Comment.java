package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("comment")
@Data
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("date")
    private String date;
    
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




