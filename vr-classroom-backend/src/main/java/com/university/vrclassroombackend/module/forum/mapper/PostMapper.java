package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.module.forum.model.Post;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PostMapper extends BaseMapper<Post> {
    // 可以在这里添加自定义的查询方法
    IPage<Post> selectByStatus(Page<Post> page, Integer status);
    List<Post> selectByStatus(Integer status);
    IPage<Post> searchPosts(Page<Post> page, String keyword);
    List<Post> selectByAuthorId(Integer authorId);
    
    /**
     * 原子增加点赞数
     */
    @Update("UPDATE post SET likeCount = likeCount + #{delta} WHERE id = #{postId}")
    int incrementLikeCount(@Param("postId") Integer postId, @Param("delta") int delta);
    
    /**
     * 原子增加评论数
     */
    @Update("UPDATE post SET commentCount = commentCount + #{delta} WHERE id = #{postId}")
    int incrementCommentCount(@Param("postId") Integer postId, @Param("delta") int delta);
    
    /**
     * 原子增加分享数
     */
    @Update("UPDATE post SET shareCount = shareCount + #{delta} WHERE id = #{postId}")
    int incrementShareCount(@Param("postId") Integer postId, @Param("delta") int delta);
}
