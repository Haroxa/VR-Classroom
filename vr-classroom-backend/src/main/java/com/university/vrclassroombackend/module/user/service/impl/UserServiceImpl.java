package com.university.vrclassroombackend.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CollegeMapper collegeMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByOpenId(String openId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenId, openId);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public UserProfileVO getUserProfile(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId().toString());
        vo.setPhone(user.getPhone());
        vo.setName(user.getName());
        vo.setAvatar(user.getAvatar());
        vo.setCollegeId(user.getCollegeId());
        vo.setVerifyStatus(user.getVerifyStatus());
        
        if (user.getCollegeId() != null && !user.getCollegeId().isEmpty()) {
            try {
                Integer collegeIdInt = Integer.parseInt(user.getCollegeId());
                College college = collegeMapper.selectById(collegeIdInt);
                if (college != null) {
                    vo.setCollegeName(college.getName());
                }
            } catch (NumberFormatException e) {
            }
        }
        
        return vo;
    }

    @Override
    public UserPublicVO getUserPublicInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserPublicVO vo = new UserPublicVO();
        vo.setId(user.getId().toString());
        vo.setName(user.getName());
        vo.setAvatar(user.getAvatar());
        vo.setCollegeId(user.getCollegeId());
        vo.setVerified(user.getVerifyStatus() == 2);
        
        if (user.getCollegeId() != null && !user.getCollegeId().isEmpty()) {
            try {
                Integer collegeIdInt = Integer.parseInt(user.getCollegeId());
                College college = collegeMapper.selectById(collegeIdInt);
                if (college != null) {
                    vo.setCollegeName(college.getName());
                }
            } catch (NumberFormatException e) {
            }
        }
        
        return vo;
    }

    @Override
    public List<UserPostVO> getUserPosts(Integer userId, Integer page) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getAuthorId, userId);
        queryWrapper.orderByDesc(Post::getDate);
        
        List<Post> posts = postMapper.selectList(queryWrapper);
        List<UserPostVO> result = new ArrayList<>();
        
        for (Post post : posts) {
            UserPostVO vo = new UserPostVO();
            vo.setId(post.getId().toString());
            vo.setDate(post.getDate());
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            vo.setImages(new ArrayList<>());
            vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
            vo.setLikeCount(post.getLikeCount());
            vo.setShareCount(post.getShareCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
            vo.setCategoryName(null);
            vo.setLiked(false);
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public List<UserCommentVO> getUserComments(Integer userId, Integer page) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getCommenterId, userId);
        queryWrapper.orderByDesc(Comment::getDate);
        
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<UserCommentVO> result = new ArrayList<>();
        
        for (Comment comment : comments) {
            UserCommentVO vo = new UserCommentVO();
            vo.setId(comment.getId());
            vo.setDate(comment.getDate());
            vo.setContent(comment.getContent());
            vo.setLikeCount(comment.getLikeCount());
            vo.setStatus(comment.getStatus());
            vo.setRejectReason(comment.getRejectReason());
            vo.setLiked(false);
            vo.setRelatedPost(null);
            result.add(vo);
        }
        
        return result;
    }
}




