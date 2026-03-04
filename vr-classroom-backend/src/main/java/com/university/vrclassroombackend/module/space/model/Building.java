package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("building")
@Data
public class Building {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("campusId")
    private Integer campusId;
    
    @TableField
    private String name;
}




