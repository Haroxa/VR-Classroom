package com.university.vrclassroombackend.domain.user.service;

import com.university.vrclassroombackend.domain.user.model.User;
import com.university.vrclassroombackend.domain.user.vo.UserCommentVO;
import com.university.vrclassroombackend.domain.user.vo.UserPostVO;
import com.university.vrclassroombackend.domain.user.vo.UserProfileVO;
import com.university.vrclassroombackend.domain.user.vo.UserPublicVO;

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
