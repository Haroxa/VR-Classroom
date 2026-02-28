package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.module.forum.model.Comment;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    // 可以在这里添加自定义的查询方法
    IPage<Comment> selectByCommenterId(Page<Comment> page, Integer commenterId);
    List<Comment> selectByCommenterId(Integer commenterId);
    List<Comment> selectByPostId(Integer postId);
}
