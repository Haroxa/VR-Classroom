package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("room")
@Data
public class ClassRoom {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("buildingId")
    private Integer buildingId;
    
    @TableField("roomNumber")
    private String roomNumber;
    
    @TableField("totalRows")
    private Integer totalRows;
    
    @TableField("totalCols")
    private Integer totalCols;
}




