package com.university.vrclassroombackend.module.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论审核VO
 */
@Data
public class CommentAuditVO {
    /**
     * 评论id
     */
    private String id;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 状态 0：待审核，1：已发布，2：已驳回
     */
    private Integer status;
    
    /**
     * 驳回理由
     */
    private String rejectReason;
    
    /**
     * 评论人信息
     */
    private UserPublicVO commenter;
    
    /**
     * 关联帖子信息
     */
    private RelatedPostVO relatedPost;
}
