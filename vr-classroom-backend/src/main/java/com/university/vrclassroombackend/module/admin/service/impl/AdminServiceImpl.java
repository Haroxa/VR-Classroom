package com.university.vrclassroombackend.module.admin.service.impl;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import com.university.vrclassroombackend.module.admin.vo.*;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.repository.PostRepository;
import com.university.vrclassroombackend.module.forum.repository.CommentRepository;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.repository.CollegeRepository;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.repository.CategoryRepository;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 后台管理服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CollegeRepository collegeRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<PostAuditVO> getPosts(Integer page, Integer status, Integer categoryId, String keyword) {
        // 构建排序和分页
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE, sort);
        
        // 使用Specification构建动态查询条件
        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 状态条件
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // 分类ID条件
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            
            // 关键词条件
            if (keyword != null) {
                Predicate titlePredicate = criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
                Predicate contentPredicate = criteriaBuilder.like(root.get("content"), "%" + keyword + "%");
                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 查询帖子列表
        List<Post> posts = postRepository.findAll(spec, pageable).getContent();
        
        // 转换为VO
        List<PostAuditVO> postAuditVOs = new ArrayList<>();
        for (Post post : posts) {
            PostAuditVO vo = new PostAuditVO();
            vo.setId(post.getId().toString());
            vo.setDate(post.getCreatedAt().format(formatter));
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            vo.setCategoryId(post.getCategoryId() != null ? post.getCategoryId().toString() : null);
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());
            
            // 获取分类名称
            if (post.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryRepository.findById(post.getCategoryId());
                vo.setCategoryName(categoryOptional.map(Category::getName).orElse("未知分类"));
            } else {
                vo.setCategoryName("未知分类");
            }
            
            // 获取作者信息
            Optional<User> userOptional = userRepository.findById(post.getAuthorId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserPublicVO userPublicVO = new UserPublicVO();
                userPublicVO.setId(user.getId().toString());
                userPublicVO.setName(user.getName());
                userPublicVO.setAvatar(user.getAvatar());
                userPublicVO.setCollegeId(user.getCollegeId());
                
                // 获取学院名称
                Optional<College> collegeOptional = Optional.empty();
                try {
                    if (user.getCollegeId() != null) {
                        collegeOptional = collegeRepository.findById(Integer.parseInt(user.getCollegeId()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("学院ID格式错误: collegeId={}", user.getCollegeId());
                }
                userPublicVO.setCollegeName(collegeOptional.map(College::getName).orElse("未知学院"));
                
                // 判断是否认证
                userPublicVO.setVerified(user.getVerifyStatus() == 2);
                
                vo.setAuthor(userPublicVO);
            }
            
            postAuditVOs.add(vo);
        }
        
        return postAuditVOs;
    }

    @Override
    public List<CommentAuditVO> getComments(Integer page, Integer status) {
        // 构建排序和分页
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE, sort);
        
        // 查询评论列表
        List<Comment> comments;
        if (status != null) {
            comments = commentRepository.findByStatus(status, pageable).getContent();
        } else {
            comments = commentRepository.findAll(pageable).getContent();
        }
        
        // 转换为VO
        List<CommentAuditVO> commentAuditVOs = new ArrayList<>();
        for (Comment comment : comments) {
            CommentAuditVO vo = new CommentAuditVO();
            vo.setId(comment.getId().toString());
            vo.setDate(comment.getCreatedAt().format(formatter));
            vo.setContent(comment.getContent());
            vo.setStatus(comment.getStatus());
            vo.setRejectReason(comment.getRejectReason());
            
            // 获取评论人信息
            Optional<User> userOptional = userRepository.findById(comment.getCommenterId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserPublicVO userPublicVO = new UserPublicVO();
                userPublicVO.setId(user.getId().toString());
                userPublicVO.setName(user.getName());
                userPublicVO.setAvatar(user.getAvatar());
                userPublicVO.setCollegeId(user.getCollegeId());
                
                // 获取学院名称
                Optional<College> collegeOptional = Optional.empty();
                try {
                    if (user.getCollegeId() != null) {
                        collegeOptional = collegeRepository.findById(Integer.parseInt(user.getCollegeId()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("学院ID格式错误: collegeId={}", user.getCollegeId());
                }
                userPublicVO.setCollegeName(collegeOptional.map(College::getName).orElse("未知学院"));
                
                // 判断是否认证
                userPublicVO.setVerified(user.getVerifyStatus() == 2);
                
                vo.setCommenter(userPublicVO);
            }
            
            // 获取关联帖子信息
            Optional<Post> postOptional = postRepository.findById(comment.getPostId());
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                RelatedPostVO relatedPostVO = new RelatedPostVO();
                relatedPostVO.setId(post.getId().toString());
                relatedPostVO.setDate(post.getCreatedAt().format(formatter));
                relatedPostVO.setTitle(post.getTitle());
                relatedPostVO.setStatus(post.getStatus());
                
                // 获取帖子作者信息
                Optional<User> postUserOptional = userRepository.findById(post.getAuthorId());
                if (postUserOptional.isPresent()) {
                    User postUser = postUserOptional.get();
                    UserPublicVO postUserPublicVO = new UserPublicVO();
                    postUserPublicVO.setId(postUser.getId().toString());
                    postUserPublicVO.setName(postUser.getName());
                    postUserPublicVO.setAvatar(postUser.getAvatar());
                    postUserPublicVO.setCollegeId(postUser.getCollegeId());
                    
                    // 获取学院名称
                    Optional<College> postCollegeOptional = Optional.empty();
                    try {
                        if (postUser.getCollegeId() != null) {
                            postCollegeOptional = collegeRepository.findById(Integer.parseInt(postUser.getCollegeId()));
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("学院ID格式错误: collegeId={}", postUser.getCollegeId());
                    }
                    postUserPublicVO.setCollegeName(postCollegeOptional.map(College::getName).orElse("未知学院"));
                    
                    // 判断是否认证
                    postUserPublicVO.setVerified(postUser.getVerifyStatus() == 2);
                    
                    relatedPostVO.setAuthor(postUserPublicVO);
                }
                
                vo.setRelatedPost(relatedPostVO);
            }
            
            commentAuditVOs.add(vo);
        }
        
        return commentAuditVOs;
    }

    @Override
    @Transactional
    public void auditPost(Integer postId, PostAuditDTO dto) {
        // 查询帖子
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            // 更新状态和驳回理由
            post.setStatus(dto.getStatus());
            post.setRejectReason(dto.getRejectReason());
            // 保存更新
            postRepository.save(post);
        } else {
            throw new RuntimeException("帖子不存在: postId=" + postId);
        }
    }

    @Override
    @Transactional
    public void auditComment(Integer commentId, CommentAuditDTO dto) {
        // 查询评论
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            // 更新状态和驳回理由
            comment.setStatus(dto.getStatus());
            comment.setRejectReason(dto.getRejectReason());
            // 保存更新
            commentRepository.save(comment);
        } else {
            throw new RuntimeException("评论不存在: commentId=" + commentId);
        }
    }
}
