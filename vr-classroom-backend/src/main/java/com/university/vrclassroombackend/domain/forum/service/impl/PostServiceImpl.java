package com.university.vrclassroombackend.domain.forum.service.impl;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.domain.common.model.Category;
import com.university.vrclassroombackend.domain.common.repository.CategoryRepository;
import com.university.vrclassroombackend.domain.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.domain.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.domain.forum.model.Post;
import com.university.vrclassroombackend.domain.forum.repository.PostRepository;
import com.university.vrclassroombackend.domain.forum.service.PostService;
import com.university.vrclassroombackend.domain.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.domain.forum.vo.PostVO;
import com.university.vrclassroombackend.domain.user.service.UserService;
import com.university.vrclassroombackend.domain.user.vo.UserPostVO;
import com.university.vrclassroombackend.domain.user.vo.UserPublicVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public List<PostVO> getPublicPosts(Integer page, Integer categoryId, String keyword) {
        Pageable pageable = PageRequest.of(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE);
        Page<Post> postsPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null) {
                Page<Post> keywordPosts = postRepository.searchPosts(keyword, pageable);
                postsPage = keywordPosts;
            } else {
                postsPage = postRepository.searchPosts(keyword, pageable);
            }
        } else if (categoryId != null) {
            Page<Post> allPublishedPosts = postRepository.findByStatus(Post.STATUS_PUBLISHED, pageable);
            postsPage = allPublishedPosts;
        } else {
            postsPage = postRepository.findByStatus(Post.STATUS_PUBLISHED, pageable);
        }
        
        List<Post> posts = postsPage.getContent();
        
        if (posts.isEmpty()) {
            return List.of();
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
                        id -> categoryRepository.findById(id).orElse(null),
                        (existing, replacement) -> existing
                ));
        
        return posts.stream()
                .map(post -> convertToPostVO(post, userMap, categoryMap))
                .collect(Collectors.toList());
    }

    @Override
    public PostDetailVO getPostDetail(Integer postId, Integer currentUserId) {
        Post post = postRepository.findById(postId).orElse(null);
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
        post.setImages(dto.getImages());
        post.setCategoryId(dto.getCategoryId() != null ? dto.getCategoryId() : AppConstants.Post.DEFAULT_CATEGORY_ID);
        post.setAuthorId(authorId != null ? authorId : 0);
        post.setLikeCount(0);
        post.setShareCount(0);
        post.setCommentCount(0);
        post.setStatus(Post.STATUS_PUBLISHED);
        
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Override
    @Transactional
    public boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId) {
        logger.info("开始更新帖子: postId={}, authorId={}", postId, authorId);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            logger.warn("更新帖子失败: 帖子不存在或无权限, postId={}, authorId={}", postId, authorId);
            return false;
        }
        
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setSummary(dto.getContent().length() > Post.SUMMARY_LENGTH ? dto.getContent().substring(0, Post.SUMMARY_LENGTH) : dto.getContent());
        post.setImages(dto.getImages());
        post.setCategoryId(dto.getCategoryId());
        post.setStatus(Post.STATUS_PENDING);
        
        postRepository.save(post);
        logger.info("更新帖子成功: postId={}, authorId={}", postId, authorId);
        return true;
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postId, Integer authorId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            return false;
        }
        
        post.setStatus(Post.STATUS_DELETED);
        postRepository.save(post);
        return true;
    }

    @Override
    public List<UserPostVO> getUserPosts(Integer userId, Integer page) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        
        int pageSize = AppConstants.Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, posts.size());
        if (start >= posts.size()) {
            return List.of();
        }
        posts = posts.subList(start, end);
        
        return posts.stream().map(this::convertToUserPostVO).collect(Collectors.toList());
    }
    
    private PostVO convertToPostVO(Post post, Map<Integer, UserPublicVO> userMap, Map<Integer, Category> categoryMap) {
        PostVO vo = new PostVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setImages(post.getImages());
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setLiked(false);
        
        if (post.getCategoryId() != null) {
            Category category = categoryMap.get(post.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        UserPublicVO author = userMap.get(post.getAuthorId());
        if (author != null) {
            vo.setAuthor(author);
        }
        
        return vo;
    }
    
    private PostDetailVO convertToPostDetailVO(Post post, Integer currentUserId) {
        PostDetailVO vo = new PostDetailVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setImages(post.getImages());
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        
        if (currentUserId != null && post.getAuthorId() != null && post.getAuthorId().equals(currentUserId)) {
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
        } else {
            vo.setStatus(null);
            vo.setRejectReason(null);
        }
        
        if (post.getCategoryId() != null) {
            Category category = categoryRepository.findById(post.getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        UserPublicVO author = userService.getUserPublicInfo(post.getAuthorId());
        vo.setAuthor(author);
        
        return vo;
    }
    
    private UserPostVO convertToUserPostVO(Post post) {
        UserPostVO vo = new UserPostVO();
        vo.setId(post.getId().toString());
        vo.setDate(post.getDate());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setImages(post.getImages());
        vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
        vo.setLikeCount(post.getLikeCount());
        vo.setShareCount(post.getShareCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setStatus(post.getStatus());
        vo.setRejectReason(post.getRejectReason());
        vo.setLiked(false);
        
        if (post.getCategoryId() != null) {
            Category category = categoryRepository.findById(post.getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        return vo;
    }
}
