package com.university.vrclassroombackend.module.user.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField
    private String phone;
    
    @TableField(value = "open_id")
    private String openId;
    
    @TableField
    private String name = "未认证";
    
    @TableField
    private String avatar = "assets/default_avatar.png";
    
    @TableField("college_id")
    private String collegeId;
    
    @TableField("verify_status")
    private Integer verifyStatus = 0;
}




