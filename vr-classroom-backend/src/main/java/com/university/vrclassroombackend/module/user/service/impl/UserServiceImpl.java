package com.university.vrclassroombackend.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.CommentLikeMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostLikeMapper;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.forum.vo.RelatedPostVO;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CollegeMapper collegeMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostLikeMapper postLikeMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentLikeMapper commentLikeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    /**
     * 获取用户资料
     */
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

    /**
     * 获取用户公开信息
     */
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
    public IPage<UserPostVO> getUserPosts(Integer userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getAuthorId, userId);
        queryWrapper.orderByDesc(Post::getDate);
        
        Page<Post> pageParam = new Page<>(page, pageSize);
        IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);
        List<Post> posts = postPage.getRecords();
        List<UserPostVO> result = new ArrayList<>();
        
        for (Post post : posts) {
            UserPostVO vo = new UserPostVO();
            vo.setId(post.getId().toString());
            vo.setDate(post.getDate());
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            // 解析images字段
            try {
                vo.setImages(post.getImages() != null ? objectMapper.readValue(post.getImages(), List.class) : new ArrayList<>());
            } catch (JsonProcessingException e) {
                vo.setImages(new ArrayList<>());
            }
            vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
            vo.setLikeCount(post.getLikeCount());
            vo.setShareCount(post.getShareCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
            vo.setCategoryName(getCategoryName(post.getCategoryId()));
            vo.setLiked(false);
            result.add(vo);
        }
        
        Page<UserPostVO> resultPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }
    
    private String getCategoryName(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            return "未分类";
        }
        Category category = categoryMapper.selectById(categoryId);
        return category != null ? category.getName() : "未分类";
    }

    @Override
    public IPage<UserCommentVO> getUserComments(Integer userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getCommenterId, userId);
        queryWrapper.orderByDesc(Comment::getDate);
        
        Page<Comment> pageParam = new Page<>(page, pageSize);
        IPage<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);
        List<Comment> comments = commentPage.getRecords();
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
            
            // 查询相关帖子信息
            if (comment.getPostId() != null) {
                Post post = postMapper.selectById(comment.getPostId());
                if (post != null) {
                    RelatedPostVO relatedPost = new RelatedPostVO();
                    relatedPost.setId(post.getId());
                    relatedPost.setTitle(post.getTitle());
                    relatedPost.setStatus(post.getStatus());
                    vo.setRelatedPost(relatedPost);
                } else {
                    vo.setRelatedPost(null);
                }
            } else {
                vo.setRelatedPost(null);
            }
            
            // 检查当前评论是否被用户点赞
            boolean isLiked = commentLikeMapper.existsByUserIdAndCommentId(userId, comment.getId());
            vo.setLiked(isLiked);
            
            result.add(vo);
        }
        
        Page<UserCommentVO> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }

    @Override
    public IPage<UserPostVO> getLikedPosts(Integer userId, Integer page, Integer pageSize) {
        // 获取用户点赞的帖子ID列表
        List<Integer> postIds = postLikeMapper.selectLikedPostIdsByUserId(userId);
        if (postIds.isEmpty()) {
            Page<UserPostVO> emptyPage = new Page<>(page, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 查询帖子信息
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Post::getId, postIds);
        queryWrapper.orderByDesc(Post::getDate);

        Page<Post> pageParam = new Page<>(page, pageSize);
        IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);
        List<Post> posts = postPage.getRecords();
        List<UserPostVO> result = new ArrayList<>();

        // 批量获取作者信息
        List<Integer> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, UserPublicVO> authorMap = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));
        
        for (Post post : posts) {
            UserPostVO vo = new UserPostVO();
            vo.setId(post.getId().toString());
            vo.setDate(post.getDate());
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            // 解析images字段
            try {
                vo.setImages(post.getImages() != null ? objectMapper.readValue(post.getImages(), List.class) : new ArrayList<>());
            } catch (JsonProcessingException e) {
                vo.setImages(new ArrayList<>());
            }
            vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
            vo.setLikeCount(post.getLikeCount());
            vo.setShareCount(post.getShareCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
            vo.setCategoryName(getCategoryName(post.getCategoryId()));
            vo.setLiked(true);
            
            // 设置作者信息
            if (post.getAuthorId() != null) {
                vo.setAuthor(authorMap.get(post.getAuthorId()));
            }
            
            result.add(vo);
        }

        Page<UserPostVO> resultPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }

    @Override
    public IPage<UserCommentVO> getLikedComments(Integer userId, Integer page, Integer pageSize) {
        // 获取用户点赞的评论ID列表
        List<Integer> commentIds = commentLikeMapper.selectLikedCommentIdsByUserId(userId);
        if (commentIds.isEmpty()) {
            Page<UserCommentVO> emptyPage = new Page<>(page, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        // 查询评论信息
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Comment::getId, commentIds);
        queryWrapper.orderByDesc(Comment::getDate);

        Page<Comment> pageParam = new Page<>(page, pageSize);
        IPage<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);
        List<Comment> comments = commentPage.getRecords();
        List<UserCommentVO> result = new ArrayList<>();

        for (Comment comment : comments) {
            UserCommentVO vo = new UserCommentVO();
            vo.setId(comment.getId());
            vo.setDate(comment.getDate());
            vo.setContent(comment.getContent());
            vo.setLikeCount(comment.getLikeCount());
            vo.setStatus(comment.getStatus());
            vo.setRejectReason(comment.getRejectReason());
            vo.setLiked(true);
            
            // 查询相关帖子信息
            if (comment.getPostId() != null) {
                Post post = postMapper.selectById(comment.getPostId());
                if (post != null) {
                    RelatedPostVO relatedPost = new RelatedPostVO();
                    relatedPost.setId(post.getId());
                    relatedPost.setTitle(post.getTitle());
                    relatedPost.setStatus(post.getStatus());
                    vo.setRelatedPost(relatedPost);
                } else {
                    vo.setRelatedPost(null);
                }
            } else {
                vo.setRelatedPost(null);
            }
            
            result.add(vo);
        }

        Page<UserCommentVO> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }

    @Override
    public IPage<UserProfileVO> getAllUsers(Integer page, Integer pageSize) {
        // 查询所有用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(User::getId);
        
        Page<User> pageParam = new Page<>(page, pageSize);
        IPage<User> userPage = userMapper.selectPage(pageParam, queryWrapper);
        List<User> users = userPage.getRecords();
        List<UserProfileVO> result = new ArrayList<>();
        
        for (User user : users) {
            UserProfileVO vo = getUserProfile(user.getId());
            if (vo != null) {
                result.add(vo);
            }
        }
        
        Page<UserProfileVO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        resultPage.setRecords(result);
        return resultPage;
    }
}




