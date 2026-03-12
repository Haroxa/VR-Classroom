package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.forum.model.CommentLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {
    
    @Select("SELECT COUNT(*) > 0 FROM comment_like WHERE userId = #{userId} AND commentId = #{commentId}")
    boolean existsByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    
    @Select("SELECT COUNT(*) FROM comment_like WHERE commentId = #{commentId}")
    int countByCommentId(@Param("commentId") Integer commentId);
    
    @Delete("DELETE FROM comment_like WHERE userId = #{userId} AND commentId = #{commentId}")
    int deleteByUserIdAndCommentId(@Param("userId") Integer userId, @Param("commentId") Integer commentId);
    
    @Select("SELECT commentId FROM comment_like WHERE userId = #{userId}")
    List<Integer> selectLikedCommentIdsByUserId(@Param("userId") Integer userId);
}
