package com.university.vrclassroombackend.module.user.service;

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
    List<UserPostVO> getUserPosts(Integer userId, Integer page);
    List<UserCommentVO> getUserComments(Integer userId, Integer page);
}




