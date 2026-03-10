package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("comment_like")
public class CommentLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer commentId;
    private Date createdAt;
}
