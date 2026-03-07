package com.university.vrclassroombackend.module.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子审核VO
 */
@Data
public class PostAuditVO {
    /**
     * 帖子id
     */
    private String id;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子缩略内容，用户上传content自动截取前 50 个字存入该字段
     */
    private String summary;
    
    /**
     * 状态 0：待审核，1：已发布，2：已驳回
     */
    private Integer status;
    
    /**
     * 驳回理由
     */
    private String rejectReason;
    
    /**
     * 分类名字
     */
    private String categoryName;
    
    /**
     * 作者信息
     */
    private UserPublicVO author;
}
