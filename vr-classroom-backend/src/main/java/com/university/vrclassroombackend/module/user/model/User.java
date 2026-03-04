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
    
    @TableField(value = "openId")
    private String openId;
    
    @TableField
    private String name;
    
    @TableField
    private String avatar;
    
    @TableField("collegeId")
    private String collegeId;
    
    @TableField("verifyStatus")
    private Integer verifyStatus;
}




