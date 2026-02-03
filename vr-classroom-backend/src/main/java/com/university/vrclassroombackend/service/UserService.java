package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.model.User;
import com.university.vrclassroombackend.vo.UserCommentVO;
import com.university.vrclassroombackend.vo.UserPostVO;
import com.university.vrclassroombackend.vo.UserProfileVO;
import com.university.vrclassroombackend.vo.UserPublicVO;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);
    User getUserByPhone(String phone);
    User getUserByOpenId(String openId);
    User saveUser(User user);
    UserProfileVO getUserProfile(Integer userId);
    UserPublicVO getUserPublicInfo(Integer userId);
    List<UserPostVO> getUserPosts(Integer userId, Integer page);
    List<UserCommentVO> getUserComments(Integer userId, Integer page);
}
