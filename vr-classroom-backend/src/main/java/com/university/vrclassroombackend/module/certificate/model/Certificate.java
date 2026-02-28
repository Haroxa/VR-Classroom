package com.university.vrclassroombackend.module.certificate.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("certificate")
@Data
public class Certificate {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("claim_id")
    private Integer claimId;
    
    @TableField("donor_id")
    private Integer donorId;
    
    @TableField("certificate_no")
    private String certificateNo;
    
    @TableField("certificate_url")
    private String certificateUrl;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}




