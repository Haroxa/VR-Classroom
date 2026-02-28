package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("college")
@Data
public class College {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField
    private String name;
}




