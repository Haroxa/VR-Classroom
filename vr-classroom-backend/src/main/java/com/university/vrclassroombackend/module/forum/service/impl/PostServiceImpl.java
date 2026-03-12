package com.university.vrclassroombackend.module.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.model.PostLike;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostLikeMapper;
import com.university.vrclassroombackend.module.forum.service.PostService;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostLikeActionVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostLikeMapper postLikeMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取公开帖子列表
     */
    @Override
    public IPage<PostVO> getPublicPosts(Integer page, Integer pageSize, Integer categoryId, String keyword, Integer currentUserId) {
        int currentPage = page != null && page > 0 ? page : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : AppConstants.Pagination.DEFAULT_PAGE_SIZE;
        Page<Post> pageParam = new Page<>(currentPage, size);
        
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getStatus, Post.STATUS_PUBLISHED);
        
        if (categoryId != null) {
            queryWrapper.eq(Post::getCategoryId, categoryId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(qw -> qw.like(Post::getTitle, keyword).or().like(Post::getContent, keyword));
        }
        
        queryWrapper.orderByDesc(Post::getDate);
        
        IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);
        List<Post> posts = postPage.getRecords();
        
        if (posts.isEmpty()) {
            Page<PostVO> emptyPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
            emptyPage.setRecords(List.of());
            return emptyPage;
        }
        
        List<Integer> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        List<Integer> categoryIds = posts.stream()
                .map(Post::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, UserPublicVO> userMap = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));
        
        Map<Integer, Category> categoryMap = categoryIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        categoryMapper::selectById,
                        (existing, replacement) -> existing
                ));
        
        List<PostVO> voList = posts.stream()
                .map(post -> convertToPostVO(post, userMap, categoryMap, currentUserId))
                .collect(Collectors.toList());
        
        Page<PostVO> resultPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    /**
     * 获取帖子详情
     */
    @Override
    public PostDetailVO getPostDetail(Integer postId, Integer currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return null;
        }
        
        return convertToPostDetailVO(post, currentUserId);
    }

    @Override
    @Transactional
    public Integer createPost(PostCreateDTO dto, Integer authorId) {
        Post post = new Post();
        
        post.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        String content = dto.getContent() != null ? dto.getContent() : "";
        post.setContent(content);
        String summary = content.length() > Post.SUMMARY_LENGTH ? content.substring(0, Post.SUMMARY_LENGTH) : content;
        post.setSummary(summary);
        try {
            post.setImages(dto.getImages() != null ? objectMapper.writeValueAsString(dto.getImages()) : "[]");
        } catch (JsonProcessingException e) {
            logger.error("转换images为JSON失败", e);
            post.setImages("[]");
        }
        post.setCategoryId(dto.getCategoryId() != null ? dto.getCategoryId() : AppConstants.Post.DEFAULT_CATEGORY_ID);
        post.setAuthorId(authorId != null ? authorId : 0);
        post.setLikeCount(0);
        post.setShareCount(0);
        post.setCommentCount(0);
        post.setStatus(Post.STATUS_PENDING);
        post.setDate(LocalDateTime.now());
        
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    @Transactional
    public boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId) {
        logger.info("开始更新帖子 postId={}, authorId={}", postId, authorId);
        Post post = postMapper.selectById(postId);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            logger.warn("更新帖子失败: 帖子不存在或无权限 postId={}, authorId={}", postId, authorId);
            return false;
        }
        
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setSummary(dto.getContent().length() > Post.SUMMARY_LENGTH ? dto.getContent().substring(0, Post.SUMMARY_LENGTH) : dto.getContent());
        try {
            post.setImages(dto.getImages() != null ? objectMapper.writeValueAsString(dto.getImages()) : "[]");
        } catch (JsonProcessingException e) {
            logger.error("转换images为JSON失败", e);
            post.setImages("[]");
        }
        post.setCategoryId(dto.getCategoryId());
        post.setStatus(Post.STATUS_PENDING);
        
        postMapper.updateById(post);
        logger.info("更新帖子成功: postId={}, authorId={}", postId, authorId);
        return true;
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postId, Integer authorId) {
        Post post = postMapper.selectById(postId);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            return false;
        }
        
        post.setStatus(Post.STATUS_DELETED);
        postMapper.updateById(post);
        return true;
    }

    @Override
    public IPage<UserPostVO> getUserPosts(Integer userId, Integer page) {
        int currentPage = page != null && page > 0 ? page : 1;
        Page<Post> pageParam = new Page<>(currentPage, AppConstants.Pagination.DEFAULT_PAGE_SIZE);
        
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getAuthorId, userId);
        queryWrapper.orderByDesc(Post::getDate);
        
        IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);
        List<Post> posts = postPage.getRecords();
        
        List<UserPostVO> voList = posts.stream().map(this::convertToUserPostVO).collect(Collectors.toList());
        Page<UserPostVO> resultPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }
    
    private PostVO convertToPostVO(Post post, Map<Integer, UserPublicVO> userMap, Map<Integer, Category> categoryMap, Integer currentUserId) {
        PostVO vo = new PostVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        try {
            vo.setImages(post.getImages() != null ? objectMapper.readValue(post.getImages(), List.class) : null);
        } catch (JsonProcessingException e) {
            logger.error("解析images JSON失败", e);
            vo.setImages(null);
        }
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        
        // 设置是否被当前用户点赞
        if (currentUserId != null) {
            boolean isLiked = postLikeMapper.existsByUserIdAndPostId(currentUserId, post.getId());
            vo.setLiked(isLiked);
        } else {
            vo.setLiked(false);
        }
        
        if (post.getCategoryId() != null && post.getCategoryId() != 0) {
            Category category = categoryMap.get(post.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            } else {
                vo.setCategoryName("未分类");
            }
        } else {
            vo.setCategoryName("未分类");
        }
        
        UserPublicVO author = userMap.get(post.getAuthorId());
        if (author != null) {
            vo.setAuthor(author);
        } else {
            // 创建默认的用户信息，避免NPE
            UserPublicVO defaultAuthor = new UserPublicVO();
            defaultAuthor.setId("0");
            defaultAuthor.setName("未知用户");
            vo.setAuthor(defaultAuthor);
        }
        
        return vo;
    }
    
    private PostDetailVO convertToPostDetailVO(Post post, Integer currentUserId) {
        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        try {
            vo.setImages(post.getImages() != null ? objectMapper.readValue(post.getImages(), List.class) : null);
        } catch (JsonProcessingException e) {
            logger.error("解析images JSON失败", e);
            vo.setImages(null);
        }
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        
        // 设置是否被当前用户点赞
        if (currentUserId != null) {
            boolean isLiked = postLikeMapper.existsByUserIdAndPostId(currentUserId, post.getId());
            vo.setLiked(isLiked);
        } else {
            vo.setLiked(false);
        }
        
        if (currentUserId != null && post.getAuthorId() != null && post.getAuthorId().equals(currentUserId)) {
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
        } else {
            vo.setStatus(null);
            vo.setRejectReason(null);
        }
        
        if (post.getCategoryId() != null && post.getCategoryId() != 0) {
            Category category = categoryMapper.selectById(post.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            } else {
                vo.setCategoryName("未分类");
            }
        } else {
            vo.setCategoryName("未分类");
        }
        
        UserPublicVO author = userService.getUserPublicInfo(post.getAuthorId());
        if (author != null) {
            vo.setAuthor(author);
        } else {
            // 创建默认的用户信息，避免NPE
            UserPublicVO defaultAuthor = new UserPublicVO();
            defaultAuthor.setId("0");
            defaultAuthor.setName("未知用户");
            vo.setAuthor(defaultAuthor);
        }
        
        return vo;
    }
    
    private UserPostVO convertToUserPostVO(Post post) {
        UserPostVO vo = new UserPostVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        try {
            vo.setImages(post.getImages() != null ? objectMapper.readValue(post.getImages(), List.class) : null);
        } catch (JsonProcessingException e) {
            logger.error("解析images JSON失败", e);
            vo.setImages(null);
        }
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setStatus(post.getStatus());
        vo.setRejectReason(post.getRejectReason());
        vo.setLiked(false);
        
        if (post.getCategoryId() != null && post.getCategoryId() != 0) {
            Category category = categoryMapper.selectById(post.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            } else {
                vo.setCategoryName("未分类");
            }
        } else {
            vo.setCategoryName("未分类");
        }
        
        return vo;
    }
    
    @Override
    @Transactional
    public boolean incrementLikeCount(Integer postId, int delta) {
        if (postId == null) {
            return false;
        }
        int rows = postMapper.incrementLikeCount(postId, delta);
        return rows > 0;
    }
    
    @Override
    @Transactional
    public boolean incrementCommentCount(Integer postId, int delta) {
        if (postId == null) {
            return false;
        }
        int rows = postMapper.incrementCommentCount(postId, delta);
        return rows > 0;
    }
    
    @Override
    @Transactional
    public boolean incrementShareCount(Integer postId, int delta) {
        if (postId == null) {
            return false;
        }
        int rows = postMapper.incrementShareCount(postId, delta);
        return rows > 0;
    }

    @Override
    @Transactional
    public PostLikeActionVO likePost(Integer postId, Integer userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 检查是否已经点赞
        if (postLikeMapper.existsByUserIdAndPostId(userId, postId)) {
            throw new RuntimeException("已经点过赞了");
        }

        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        postLike.setCreatedAt(new Date());
        postLikeMapper.insert(postLike);

        // 增加帖子点赞数
        incrementLikeCount(postId, 1);

        // 构建返回结果
        PostLikeActionVO vo = new PostLikeActionVO();
        vo.setId(postLike.getId());
        vo.setLikeCount(post.getLikeCount() + 1);
        vo.setLiked(true);

        return vo;
    }

    @Override
    @Transactional
    public PostLikeActionVO unlikePost(Integer postId, Integer userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 检查是否已经点赞
        if (!postLikeMapper.existsByUserIdAndPostId(userId, postId)) {
            throw new RuntimeException("还没有点过赞");
        }

        // 删除点赞记录
        postLikeMapper.deleteByUserIdAndPostId(userId, postId);

        // 减少帖子点赞数
        incrementLikeCount(postId, -1);

        // 构建返回结果
        PostLikeActionVO vo = new PostLikeActionVO();
        vo.setLikeCount(post.getLikeCount() - 1);
        vo.setLiked(false);

        return vo;
    }

    @Override
    public IPage<PostVO> getLikedPosts(Integer userId, Integer page) {
        int currentPage = page != null && page > 0 ? page : 1;
        Page<Post> pageParam = new Page<>(currentPage, AppConstants.Pagination.DEFAULT_PAGE_SIZE);

        // 获取用户点赞的帖子ID列表
        List<Integer> postIds = postLikeMapper.selectLikedPostIdsByUserId(userId);
        if (postIds.isEmpty()) {
            Page<PostVO> emptyPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 查询帖子信息
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Post::getId, postIds);
        queryWrapper.orderByDesc(Post::getDate);

        IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);
        List<Post> posts = postPage.getRecords();

        if (posts.isEmpty()) {
            Page<PostVO> emptyPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<Integer> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> categoryIds = posts.stream()
                .map(Post::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, UserPublicVO> userMap = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));

        Map<Integer, Category> categoryMap = categoryIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        categoryMapper::selectById,
                        (existing, replacement) -> existing
                ));

        List<PostVO> voList = posts.stream()
                .map(post -> convertToPostVO(post, userMap, categoryMap, userId))
                .collect(Collectors.toList());

        Page<PostVO> resultPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }
}




