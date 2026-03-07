package com.university.vrclassroombackend.module.forum.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;

import java.time.LocalDateTime;

public class RelatedPostVO {
    private Integer id;
    private LocalDateTime date;
    private String title;
    private Integer status;
    private UserPublicVO author;

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
