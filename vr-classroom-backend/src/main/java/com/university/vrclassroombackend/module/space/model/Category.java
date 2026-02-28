package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("category")
@Data
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField
    private String name;
}




