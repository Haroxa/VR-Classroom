package com.university.vrclassroombackend.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 创建帖子DTO
 */
@Data
@Schema(description = "创建帖子请求参数")
public class PostCreateDTO {
    /**
     * 帖子标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度必须在1-200个字符之间")
    @Schema(description = "帖子标题", example = "VR技术在教育中的应用")
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(min = 1, max = 10000, message = "内容长度必须在1-10000个字符之间")
    @Schema(description = "帖子内容", example = "VR技术正在改变传统教育方式，为学生提供沉浸式学习体验...")
    private String content;

    /**
     * 帖子图片列表
     */
    @Size(max = 9, message = "最多上传9张图片")
    @Schema(description = "帖子图片列表", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;

    /**
     * 分类ID，默认0表示未分类
     */
    @Schema(description = "分类ID，默认0表示未分类", example = "1")
    private Integer categoryId = 0;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数", example = "0")
    private Integer likeCount = 0;
}
