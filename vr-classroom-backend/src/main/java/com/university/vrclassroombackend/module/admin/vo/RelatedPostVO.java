package com.university.vrclassroombackend.module.admin.vo;

import lombok.Data;

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
    private String date;
    
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
