package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.forum.model.PostLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostLikeMapper extends BaseMapper<PostLike> {
    boolean existsByUserIdAndPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
    int countByPostId(@Param("postId") Integer postId);
    int deleteByUserIdAndPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
    List<Integer> selectLikedPostIdsByUserId(@Param("userId") Integer userId);
}
