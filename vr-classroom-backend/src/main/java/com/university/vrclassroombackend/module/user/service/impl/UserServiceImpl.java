package com.university.vrclassroombackend.module.user.service.impl;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.repository.CommentRepository;
import com.university.vrclassroombackend.module.forum.repository.PostRepository;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.repository.UserRepository;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CollegeRepository collegeRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public User getUserByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserProfileVO getUserProfile(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
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
                College college = collegeRepository.findById(collegeIdInt).orElse(null);
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
        User user = userRepository.findById(userId).orElse(null);
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
                College college = collegeRepository.findById(collegeIdInt).orElse(null);
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
        List<Post> posts = postRepository.findByAuthorId(userId);
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
        Pageable pageable = PageRequest.of(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE);
        Page<Comment> commentsPage = commentRepository.findByCommenterId(userId, pageable);
        List<Comment> comments = commentsPage.getContent();
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




