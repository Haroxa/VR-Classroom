package com.university.vrclassroombackend.module.forum.vo;

import lombok.Data;

@Data
public class PostLikeActionVO {
    private Integer id;
    private Integer likeCount;
    private boolean isLiked;
}
