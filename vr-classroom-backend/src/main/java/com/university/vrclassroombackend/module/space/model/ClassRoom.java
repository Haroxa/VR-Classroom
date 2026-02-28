package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("classroom")
@Data
public class ClassRoom {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("building_id")
    private Integer buildingId;
    
    @TableField
    private String name;
    
    private Integer floor;
    
    private String description;
    
    @TableField("vr_model_url")
    private String vrModelUrl;
    
    @TableField("seat_count")
    private Integer seatCount;
    
    @TableField("claimed_count")
    private Integer claimedCount = 0;
    
    @TableField
    private Boolean active = true;
}




