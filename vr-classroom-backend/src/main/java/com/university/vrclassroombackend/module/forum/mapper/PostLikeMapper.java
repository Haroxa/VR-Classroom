package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.forum.model.PostLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface PostLikeMapper extends BaseMapper<PostLike> {
    
    @Select("SELECT COUNT(*) > 0 FROM post_like WHERE userId = #{userId} AND postId = #{postId}")
    boolean existsByUserIdAndPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
    
    @Select("SELECT COUNT(*) FROM post_like WHERE postId = #{postId}")
    int countByPostId(@Param("postId") Integer postId);
    
    @Delete("DELETE FROM post_like WHERE userId = #{userId} AND postId = #{postId}")
    int deleteByUserIdAndPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
    
    @Select("SELECT postId FROM post_like WHERE userId = #{userId}")
    List<Integer> selectLikedPostIdsByUserId(@Param("userId") Integer userId);
}
