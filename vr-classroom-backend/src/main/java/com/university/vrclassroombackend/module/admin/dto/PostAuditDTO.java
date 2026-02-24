package com.university.vrclassroombackend.module.admin.dto;

import lombok.Data;

/**
 * 帖子审核DTO
 */
@Data
public class PostAuditDTO {
    /**
     * 状态 -1：已删除， 0：待审核，1：已发布，2：已驳回
     */
    private Integer status;
    
    /**
     * 驳回理由
     */
    private String rejectReason;
}
