package com.university.vrclassroombackend.module.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.module.forum.model.Post;

import java.util.List;

public interface PostMapper extends BaseMapper<Post> {
    // 可以在这里添加自定义的查询方法
    IPage<Post> selectByStatus(Page<Post> page, Integer status);
    List<Post> selectByStatus(Integer status);
    IPage<Post> searchPosts(Page<Post> page, String keyword);
    List<Post> selectByAuthorId(Integer authorId);
}
