package com.university.vrclassroombackend.module.forum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CommentVO {
    private Integer id;
    private LocalDateTime date;
    private String content;
    private Integer commenterId;
    private Integer likeCount;
    private boolean isLiked;
    private com.university.vrclassroombackend.module.user.vo.UserPublicVO commenter;

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(Integer commenterId) {
        this.commenterId = commenterId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    @JsonProperty("isLiked")
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public com.university.vrclassroombackend.module.user.vo.UserPublicVO getCommenter() {
        return commenter;
    }

    public void setCommenter(com.university.vrclassroombackend.module.user.vo.UserPublicVO commenter) {
        this.commenter = commenter;
    }
}




