package com.university.vrclassroombackend.module.forum.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.vo.CommentLikeActionVO;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;

public interface CommentService {
    IPage<CommentVO> getPostComments(Integer postId, Integer page, Integer pageSize);
    Integer createComment(CommentCreateDTO dto, Integer commenterId);
    boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId);
    boolean deleteComment(Integer commentId, Integer commenterId);
    IPage<UserCommentVO> getUserComments(Integer userId, Integer page);
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞操作结果
     */
    CommentLikeActionVO likeComment(Integer commentId, Integer userId);
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 取消点赞操作结果
     */
    CommentLikeActionVO unlikeComment(Integer commentId, Integer userId);
    
    /**
     * 获取用户点赞的评论列表
     * @param userId 用户ID
     * @param page 页码
     * @return 评论列表
     */
    IPage<UserCommentVO> getLikedComments(Integer userId, Integer page);
}




