package com.university.vrclassroombackend.vo;

public class RelatedPostVO {
    private Integer id;
    private String date;
    private String title;
    private Integer status;
    private UserPublicVO author;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserPublicVO getAuthor() {
        return author;
    }

    public void setAuthor(UserPublicVO author) {
        this.author = author;
    }
}
