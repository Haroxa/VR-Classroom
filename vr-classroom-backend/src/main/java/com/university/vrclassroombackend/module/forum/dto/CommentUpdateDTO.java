package com.university.vrclassroombackend.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新评论DTO
 */
@Data
public class CommentUpdateDTO {
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000个字符之间")
    private String content;
}
