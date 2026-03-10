package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.forum.model.CommentLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {
    boolean existsByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    int countByCommentId(@Param("commentId") Integer commentId);
    int deleteByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    List<Integer> selectLikedCommentIdsByUserId(@Param("userId") Integer userId);
}
