package com.university.vrclassroombackend.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建帖子DTO
 */
@Data
public class PostCreateDTO {
    /**
     * 帖子标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度必须在1-200个字符之间")
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(min = 1, max = 10000, message = "内容长度必须在1-10000个字符之间")
    private String content;

    /**
     * 帖子图片列表
     */
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> images;

    /**
     * 分类ID，默认0表示未分类
     */
    private Integer categoryId = 0;

    /**
     * 点赞数
     */
    private Integer likeCount = 0;
}
