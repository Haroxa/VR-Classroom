package com.university.vrclassroombackend.vo;

public class UserCommentVO {
    private Integer id;
    private String date;
    private String content;
    private Integer likeCount;
    private Integer status;
    private String rejectReason;
    private boolean isLiked;
    private RelatedPostVO relatedPost;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public RelatedPostVO getRelatedPost() {
        return relatedPost;
    }

    public void setRelatedPost(RelatedPostVO relatedPost) {
        this.relatedPost = relatedPost;
    }
}
