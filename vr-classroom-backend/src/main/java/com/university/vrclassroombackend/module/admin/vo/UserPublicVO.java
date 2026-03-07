package com.university.vrclassroombackend.module.admin.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户公开信息VO
 */
@Data
public class UserPublicVO {
    /**
     * 用户id
     */
    private String id;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 头像URL Key
     */
    private String avatar;
    
    /**
     * 学院id
     */
    private String collegeId;
    
    /**
     * 学院名称
     */
    private String collegeName;
    
    /**
     * 是否认证
     */
    private boolean isVerified;
    
    @JsonProperty("isVerified")
    public boolean isVerified() {
        return isVerified;
    }
}
