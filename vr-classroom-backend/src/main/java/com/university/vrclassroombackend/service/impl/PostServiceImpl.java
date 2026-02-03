package com.university.vrclassroombackend.service.impl;

import com.university.vrclassroombackend.dto.PostCreateDTO;
import com.university.vrclassroombackend.dto.PostUpdateDTO;
import com.university.vrclassroombackend.model.Category;
import com.university.vrclassroombackend.model.Post;
import com.university.vrclassroombackend.repository.CategoryRepository;
import com.university.vrclassroombackend.repository.PostRepository;
import com.university.vrclassroombackend.service.PostService;
import com.university.vrclassroombackend.service.UserService;
import com.university.vrclassroombackend.vo.PostDetailVO;
import com.university.vrclassroombackend.vo.PostVO;
import com.university.vrclassroombackend.vo.UserPostVO;
import com.university.vrclassroombackend.vo.UserPublicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.university.vrclassroombackend.constant.AppConstants;
import static com.university.vrclassroombackend.constant.AppConstants.*;

@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public List<PostVO> getPublicPosts(Integer page, Integer categoryId, String keyword) {
        List<Post> posts;
        
        // 首先获取所有已发布的帖子
        List<Post> allPublishedPosts = postRepository.findByStatus(Post.STATUS_PUBLISHED);
        
        // 应用筛选条件
        if (keyword != null && !keyword.isEmpty()) {
            // 先按关键词搜索
            List<Post> keywordPosts = postRepository.searchPosts(keyword);
            if (categoryId != null) {
                // 同时按分类筛选
                posts = keywordPosts.stream()
                        .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(categoryId))
                        .collect(Collectors.toList());
            } else {
                posts = keywordPosts;
            }
        } else if (categoryId != null) {
            // 只按分类筛选
            posts = allPublishedPosts.stream()
                    .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        } else {
            // 无筛选条件，返回所有已发布帖子
            posts = allPublishedPosts;
        }
        
        // 分页处理
        int pageSize = Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, posts.size());
        if (start >= posts.size()) {
            return List.of();
        }
        posts = posts.subList(start, end);
        
        // 转换为VO
        return posts.stream().map(this::convertToPostVO).collect(Collectors.toList());
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
    public Integer createPost(PostCreateDTO dto, Integer authorId) {
        Post post = new Post();
        post.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        // 确保所有非空字段都有值
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
    public boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            return false;
        }
        
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setSummary(dto.getContent().length() > Post.SUMMARY_LENGTH ? dto.getContent().substring(0, Post.SUMMARY_LENGTH) : dto.getContent());
        post.setImages(dto.getImages());
        post.setCategoryId(dto.getCategoryId());
        post.setStatus(Post.STATUS_PENDING);
        
        postRepository.save(post);
        return true;
    }

    @Override
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
        
        // 分页处理
        int pageSize = Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, posts.size());
        if (start >= posts.size()) {
            return List.of();
        }
        posts = posts.subList(start, end);
        
        // 转换为VO
        return posts.stream().map(this::convertToUserPostVO).collect(Collectors.toList());
    }
    
    private PostVO convertToPostVO(Post post) {
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
        vo.setLiked(false); // 暂时默认未点赞
        
        // 获取分类名称
        if (post.getCategoryId() != null) {
            Category category = categoryRepository.findById(post.getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 获取作者信息
        UserPublicVO author = userService.getUserPublicInfo(post.getAuthorId());
        vo.setAuthor(author);
        
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
        
        // 若非作者本人则 status 和 rejectReason 返回 null
        if (currentUserId != null && post.getAuthorId() != null && post.getAuthorId().equals(currentUserId)) {
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
        } else {
            vo.setStatus(null);
            vo.setRejectReason(null);
        }
        
        // 获取分类名称
        if (post.getCategoryId() != null) {
            Category category = categoryRepository.findById(post.getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 获取作者信息
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
        vo.setLiked(false); // 暂时默认未点赞
        
        // 获取分类名称
        if (post.getCategoryId() != null) {
            Category category = categoryRepository.findById(post.getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        return vo;
    }
}
