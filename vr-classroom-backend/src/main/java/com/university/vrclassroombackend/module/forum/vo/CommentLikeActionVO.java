package com.university.vrclassroombackend.module.forum.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentLikeActionVO {
    private Integer id;
    private Integer likeCount;
    @JsonProperty("isLiked")
    private boolean isLiked;
}
