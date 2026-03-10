package com.university.vrclassroombackend.module.forum.vo;

public class RelatedPostVO {
    private Integer id;
    private String title;
    private Integer status;

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
