package com.university.vrclassroombackend.module.forum.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;

public interface PostService {
    IPage<PostVO> getPublicPosts(Integer page, Integer categoryId, String keyword);
    PostDetailVO getPostDetail(Integer postId, Integer currentUserId);
    Integer createPost(PostCreateDTO dto, Integer authorId);
    boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId);
    boolean deletePost(Integer postId, Integer authorId);
    IPage<com.university.vrclassroombackend.module.user.vo.UserPostVO> getUserPosts(Integer userId, Integer page);
    
    /**
     * 原子增加点赞数
     * @param postId 帖子ID
     * @param delta 增量（正数增加，负数减少）
     * @return 是否成功
     */
    boolean incrementLikeCount(Integer postId, int delta);
    
    /**
     * 原子增加评论数
     * @param postId 帖子ID
     * @param delta 增量（正数增加，负数减少）
     * @return 是否成功
     */
    boolean incrementCommentCount(Integer postId, int delta);
    
    /**
     * 原子增加分享数
     * @param postId 帖子ID
     * @param delta 增量（正数增加，负数减少）
     * @return 是否成功
     */
    boolean incrementShareCount(Integer postId, int delta);
}




