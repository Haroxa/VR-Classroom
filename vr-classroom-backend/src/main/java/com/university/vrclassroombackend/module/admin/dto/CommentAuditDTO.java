package com.university.vrclassroombackend.module.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论审核DTO
 */
@Data
public class CommentAuditDTO {
    /**
     * 状态 -1：已删除， 0：待审核，1：已发布，2：已驳回
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    
    /**
     * 驳回理由
     */
    @Size(max = 500, message = "驳回理由长度不能超过500个字符")
    private String rejectReason;
}
