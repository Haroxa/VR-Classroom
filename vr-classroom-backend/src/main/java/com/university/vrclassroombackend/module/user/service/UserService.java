package com.university.vrclassroombackend.module.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);
    User getUserByPhone(String phone);
    User getUserByOpenId(String openId);
    User saveUser(User user);
    User updateUser(User user);
    UserProfileVO getUserProfile(Integer userId);
    UserPublicVO getUserPublicInfo(Integer userId);
    IPage<UserPostVO> getUserPosts(Integer userId, Integer page, Integer pageSize);
    IPage<UserCommentVO> getUserComments(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 获取用户点赞的帖子列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    IPage<UserPostVO> getLikedPosts(Integer userId, Integer page, Integer pageSize);
    
    /**
     * 获取用户点赞的评论列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    IPage<UserCommentVO> getLikedComments(Integer userId, Integer page, Integer pageSize);
}




