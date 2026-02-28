package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("building")
@Data
public class Building {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("campus_id")
    private Integer campusId;
    
    @TableField
    private String name;
    
    private String description;
    
    @TableField("vr_model_url")
    private String vrModelUrl;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField
    private Boolean active = true;
}




