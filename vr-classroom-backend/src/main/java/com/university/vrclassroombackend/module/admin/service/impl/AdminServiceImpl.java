package com.university.vrclassroombackend.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import com.university.vrclassroombackend.module.admin.vo.*;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CollegeMapper collegeMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<PostAuditVO> getPosts(Integer page, Integer status, Integer categoryId, String keyword) {
        // 构建分页
        Page<Post> pageQuery = new Page<>(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE);
        
        // 构建查询条件
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Post::getCreatedAt);
        
        // 状态条件
        if (status != null) {
            queryWrapper.eq(Post::getStatus, status);
        }
        
        // 分类ID条件
        if (categoryId != null) {
            queryWrapper.eq(Post::getCategoryId, categoryId);
        }
        
        // 关键词条件
        if (keyword != null) {
            queryWrapper.and(wrapper -> {
                wrapper.like(Post::getTitle, keyword)
                       .or()
                       .like(Post::getContent, keyword);
            });
        }
        
        // 查询帖子列表
        IPage<Post> postsPage = postMapper.selectPage(pageQuery, queryWrapper);
        List<Post> posts = postsPage.getRecords();
        
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
                Category category = categoryMapper.selectById(post.getCategoryId());
                vo.setCategoryName(category != null ? category.getName() : "未知分类");
            } else {
                vo.setCategoryName("未知分类");
            }
            
            // 获取作者信息
            User user = userMapper.selectById(post.getAuthorId());
            if (user != null) {
                UserPublicVO userPublicVO = new UserPublicVO();
                userPublicVO.setId(user.getId().toString());
                userPublicVO.setName(user.getName());
                userPublicVO.setAvatar(user.getAvatar());
                userPublicVO.setCollegeId(user.getCollegeId());
                
                // 获取学院名称
                College college = null;
                try {
                    if (user.getCollegeId() != null) {
                        college = collegeMapper.selectById(Integer.parseInt(user.getCollegeId()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("学院ID格式错误: collegeId={}", user.getCollegeId());
                }
                userPublicVO.setCollegeName(college != null ? college.getName() : "未知学院");
                
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
        // 构建分页
        Page<Comment> pageQuery = new Page<>(page, AppConstants.Pagination.DEFAULT_PAGE_SIZE);
        
        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Comment::getCreatedAt);
        
        // 状态条件
        if (status != null) {
            queryWrapper.eq(Comment::getStatus, status);
        }
        
        // 查询评论列表
        IPage<Comment> commentsPage = commentMapper.selectPage(pageQuery, queryWrapper);
        List<Comment> comments = commentsPage.getRecords();
        
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
            User user = userMapper.selectById(comment.getCommenterId());
            if (user != null) {
                UserPublicVO userPublicVO = new UserPublicVO();
                userPublicVO.setId(user.getId().toString());
                userPublicVO.setName(user.getName());
                userPublicVO.setAvatar(user.getAvatar());
                userPublicVO.setCollegeId(user.getCollegeId());
                
                // 获取学院名称
                College college = null;
                try {
                    if (user.getCollegeId() != null) {
                        college = collegeMapper.selectById(Integer.parseInt(user.getCollegeId()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("学院ID格式错误: collegeId={}", user.getCollegeId());
                }
                userPublicVO.setCollegeName(college != null ? college.getName() : "未知学院");
                
                // 判断是否认证
                userPublicVO.setVerified(user.getVerifyStatus() == 2);
                
                vo.setCommenter(userPublicVO);
            }
            
            // 获取关联帖子信息
            Post post = postMapper.selectById(comment.getPostId());
            if (post != null) {
                RelatedPostVO relatedPostVO = new RelatedPostVO();
                relatedPostVO.setId(post.getId().toString());
                relatedPostVO.setDate(post.getCreatedAt().format(formatter));
                relatedPostVO.setTitle(post.getTitle());
                relatedPostVO.setStatus(post.getStatus());
                
                // 获取帖子作者信息
                User postUser = userMapper.selectById(post.getAuthorId());
                if (postUser != null) {
                    UserPublicVO postUserPublicVO = new UserPublicVO();
                    postUserPublicVO.setId(postUser.getId().toString());
                    postUserPublicVO.setName(postUser.getName());
                    postUserPublicVO.setAvatar(postUser.getAvatar());
                    postUserPublicVO.setCollegeId(postUser.getCollegeId());
                    
                    // 获取学院名称
                    College postCollege = null;
                    try {
                        if (postUser.getCollegeId() != null) {
                            postCollege = collegeMapper.selectById(Integer.parseInt(postUser.getCollegeId()));
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("学院ID格式错误: collegeId={}", postUser.getCollegeId());
                    }
                    postUserPublicVO.setCollegeName(postCollege != null ? postCollege.getName() : "未知学院");
                    
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
        Post post = postMapper.selectById(postId);
        if (post != null) {
            // 更新状态和驳回理由
            post.setStatus(dto.getStatus());
            post.setRejectReason(dto.getRejectReason());
            // 保存更新
            postMapper.updateById(post);
        } else {
            throw new RuntimeException("帖子不存在: postId=" + postId);
        }
    }

    @Override
    @Transactional
    public void auditComment(Integer commentId, CommentAuditDTO dto) {
        // 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment != null) {
            // 更新状态和驳回理由
            comment.setStatus(dto.getStatus());
            comment.setRejectReason(dto.getRejectReason());
            // 保存更新
            commentMapper.updateById(comment);
        } else {
            throw new RuntimeException("评论不存在: commentId=" + commentId);
        }
    }
}
