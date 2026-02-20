package com.university.vrclassroombackend.domain.forum.vo;

public class CommentVO {
    private Integer id;
    private String date;
    private String content;
    private Integer commenterId;
    private Integer likeCount;
    private boolean isLiked;
    private com.university.vrclassroombackend.domain.user.vo.UserPublicVO commenter;

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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public com.university.vrclassroombackend.domain.user.vo.UserPublicVO getCommenter() {
        return commenter;
    }

    public void setCommenter(com.university.vrclassroombackend.domain.user.vo.UserPublicVO commenter) {
        this.commenter = commenter;
    }
}
