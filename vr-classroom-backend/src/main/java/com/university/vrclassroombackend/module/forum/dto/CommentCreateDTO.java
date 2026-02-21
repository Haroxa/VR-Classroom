package com.university.vrclassroombackend.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentCreateDTO {
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容长度不能超过1000个字符")
    private String content;
    
    @NotNull(message = "帖子ID不能为空")
    private Integer postId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }
}




