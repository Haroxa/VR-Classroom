package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("campus")
@Data
public class Campus {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField
    private String name;
    
    private String description;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField
    private Boolean active = true;
}




