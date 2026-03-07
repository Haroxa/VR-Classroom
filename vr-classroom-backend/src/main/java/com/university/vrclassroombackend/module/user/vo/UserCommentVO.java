package com.university.vrclassroombackend.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UserCommentVO {
    private Integer id;
    private LocalDateTime date;
    private String content;
    private Integer likeCount;
    private Integer status;
    private String rejectReason;
    private boolean isLiked;
    private com.university.vrclassroombackend.module.forum.vo.RelatedPostVO relatedPost;

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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @JsonProperty("isLiked")
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public com.university.vrclassroombackend.module.forum.vo.RelatedPostVO getRelatedPost() {
        return relatedPost;
    }

    public void setRelatedPost(com.university.vrclassroombackend.module.forum.vo.RelatedPostVO relatedPost) {
        this.relatedPost = relatedPost;
    }
}




