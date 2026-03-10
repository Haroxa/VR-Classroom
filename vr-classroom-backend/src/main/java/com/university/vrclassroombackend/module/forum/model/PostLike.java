package com.university.vrclassroombackend.module.forum.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("post_like")
public class PostLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer postId;
    private Date createdAt;
}
