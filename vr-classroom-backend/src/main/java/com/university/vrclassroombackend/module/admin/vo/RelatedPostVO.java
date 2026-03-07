package com.university.vrclassroombackend.module.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关联帖子VO
 */
@Data
public class RelatedPostVO {
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
     * 状态 -1：已删除， 0：待审核，1：已发布，2：已驳回
     */
    private Integer status;
    
    /**
     * 作者信息
     */
    private UserPublicVO author;
}
