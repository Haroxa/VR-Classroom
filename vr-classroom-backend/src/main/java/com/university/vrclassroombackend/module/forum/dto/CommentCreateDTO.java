package com.university.vrclassroombackend.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 创建评论DTO
 */
@Data
@Schema(description = "创建评论请求参数")
public class CommentCreateDTO {
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    @Schema(description = "评论内容", example = "这篇帖子很有见解，学习了！")
    private String content;

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    @Min(value = 1, message = "帖子ID必须大于0")
    @Schema(description = "帖子ID", example = "1")
    private Integer postId;
}
