package com.university.vrclassroombackend.dto;

public class CommentCreateDTO {
    private String content;
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
