package com.university.vrclassroombackend.module.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.CommentLike;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.CommentLikeMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.service.CommentService;
import com.university.vrclassroombackend.module.forum.vo.CommentLikeActionVO;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.forum.vo.RelatedPostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@Service
public class CommentServiceImpl implements CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private UserService userService;

    /**
     * 获取帖子评论列表
     */
    @Override
    public IPage<CommentVO> getPostComments(Integer postId, Integer page, Integer pageSize) {
        int currentPage = page != null && page > 0 ? page : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : Pagination.DEFAULT_PAGE_SIZE;
        Page<Comment> pageParam = new Page<>(currentPage, size);
        
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                   .eq(Comment::getStatus, Post.STATUS_PUBLISHED)
                   .orderByDesc(Comment::getDate);
        
        IPage<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);
        List<Comment> comments = commentPage.getRecords();
        
        if (comments.isEmpty()) {
            Page<CommentVO> emptyPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
            emptyPage.setRecords(List.of());
            return emptyPage;
        }
        
        List<Integer> commenterIds = comments.stream()
                .map(Comment::getCommenterId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, UserPublicVO> userMap = commenterIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));
        
        List<CommentVO> voList = comments.stream()
                .map(comment -> convertToCommentVO(comment, userMap))
                .collect(Collectors.toList());
        
        Page<CommentVO> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }

    /**
     * 创建评论
     */
    @Override
    @Transactional
    public Integer createComment(CommentCreateDTO dto, Integer commenterId) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCommenterId(commenterId);
        comment.setPostId(dto.getPostId());
        comment.setLikeCount(0);
        comment.setStatus(Post.STATUS_PENDING);
        comment.setDate(java.time.LocalDateTime.now());
        
        commentMapper.insert(comment);
        
        // 使用原子操作增加评论数
        postMapper.incrementCommentCount(dto.getPostId(), 1);
        
        return comment.getId();
    }

    @Override
    @Transactional
    public boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId) {
        logger.info("开始更新评论 commentId={}, commenterId={}", commentId, commenterId);
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            logger.warn("更新评论失败: 评论不存在或无权限 commentId={}, commenterId={}", commentId, commenterId);
            return false;
        }
        
        comment.setContent(dto.getContent());
        comment.setStatus(Post.STATUS_PENDING);
        
        commentMapper.updateById(comment);
        logger.info("更新评论成功: commentId={}, commenterId={}", commentId, commenterId);
        return true;
    }

    /**
     * 删除评论
     */
    @Override
    @Transactional
    public boolean deleteComment(Integer commentId, Integer commenterId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            return false;
        }
        
        comment.setStatus(Post.STATUS_DELETED);
        commentMapper.updateById(comment);
        
        // 使用原子操作减少评论数
        postMapper.incrementCommentCount(comment.getPostId(), -1);
        
        return true;
    }

    @Override
    public IPage<UserCommentVO> getUserComments(Integer userId, Integer page) {
        int currentPage = page != null && page > 0 ? page : 1;
        Page<Comment> pageParam = new Page<>(currentPage, Pagination.DEFAULT_PAGE_SIZE);
        
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getCommenterId, userId);
        queryWrapper.orderByDesc(Comment::getDate);
        
        IPage<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);
        List<Comment> comments = commentPage.getRecords();
        
        if (comments.isEmpty()) {
            Page<UserCommentVO> emptyPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
            emptyPage.setRecords(List.of());
            return emptyPage;
        }
        
        List<Integer> postIds = comments.stream()
                .map(Comment::getPostId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, Post> postMap = postIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        postMapper::selectById,
                        (existing, replacement) -> existing
                ));
        
        List<Integer> authorIds = postMap.values().stream()
                .filter(post -> post != null)
                .map(Post::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, UserPublicVO> userMap = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));
        
        List<UserCommentVO> voList = comments.stream()
                .map(comment -> convertToUserCommentVO(comment, postMap, userMap))
                .collect(Collectors.toList());
        
        Page<UserCommentVO> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }
    
    private CommentVO convertToCommentVO(Comment comment, Map<Integer, UserPublicVO> userMap) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setDate(comment.getDate());
        vo.setContent(comment.getContent());
        vo.setCommenterId(comment.getCommenterId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setLiked(false);
        
        UserPublicVO commenter = userMap.get(comment.getCommenterId());
        if (commenter != null) {
            vo.setCommenter(commenter);
        }
        
        return vo;
    }
    
    private UserCommentVO convertToUserCommentVO(Comment comment, Map<Integer, Post> postMap, Map<Integer, UserPublicVO> userMap) {
        UserCommentVO vo = new UserCommentVO();
        vo.setId(comment.getId());
        vo.setDate(comment.getDate());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setStatus(comment.getStatus());
        vo.setRejectReason(comment.getRejectReason());
        vo.setLiked(false);
        
        Post post = postMap.get(comment.getPostId());
        if (post != null) {
            RelatedPostVO relatedPost = new RelatedPostVO();
            relatedPost.setId(post.getId());
            relatedPost.setTitle(post.getTitle());
            relatedPost.setStatus(post.getStatus());
            
            vo.setRelatedPost(relatedPost);
        }
        
        return vo;
    }

    @Override
    @Transactional
    public CommentLikeActionVO likeComment(Integer commentId, Integer userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 检查是否已经点赞
        if (commentLikeMapper.existsByUserIdAndCommentId(userId, commentId)) {
            throw new RuntimeException("已经点过赞了");
        }

        // 创建点赞记录
        CommentLike commentLike = new CommentLike();
        commentLike.setUserId(userId);
        commentLike.setCommentId(commentId);
        commentLike.setCreatedAt(new Date());
        commentLikeMapper.insert(commentLike);

        // 增加评论点赞数
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentMapper.updateById(comment);

        // 构建返回结果
        CommentLikeActionVO vo = new CommentLikeActionVO();
        vo.setId(commentLike.getId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setLiked(true);

        return vo;
    }

    @Override
    @Transactional
    public CommentLikeActionVO unlikeComment(Integer commentId, Integer userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 检查是否已经点赞
        if (!commentLikeMapper.existsByUserIdAndCommentId(userId, commentId)) {
            throw new RuntimeException("还没有点过赞");
        }

        // 删除点赞记录
        commentLikeMapper.deleteByUserIdAndCommentId(userId, commentId);

        // 减少评论点赞数
        if (comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentMapper.updateById(comment);
        }

        // 构建返回结果
        CommentLikeActionVO vo = new CommentLikeActionVO();
        vo.setLikeCount(comment.getLikeCount());
        vo.setLiked(false);

        return vo;
    }

    @Override
    public IPage<UserCommentVO> getLikedComments(Integer userId, Integer page) {
        int currentPage = page != null && page > 0 ? page : 1;
        Page<Comment> pageParam = new Page<>(currentPage, Pagination.DEFAULT_PAGE_SIZE);

        // 获取用户点赞的评论ID列表
        List<Integer> commentIds = commentLikeMapper.selectLikedCommentIdsByUserId(userId);
        if (commentIds.isEmpty()) {
            Page<UserCommentVO> emptyPage = new Page<>(pageParam.getCurrent(), pageParam.getSize(), 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 查询评论信息
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Comment::getId, commentIds);
        queryWrapper.orderByDesc(Comment::getDate);

        IPage<Comment> commentPage = commentMapper.selectPage(pageParam, queryWrapper);
        List<Comment> comments = commentPage.getRecords();

        if (comments.isEmpty()) {
            Page<UserCommentVO> emptyPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<Integer> postIds = comments.stream()
                .map(Comment::getPostId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Post> postMap = postIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        postMapper::selectById,
                        (existing, replacement) -> existing
                ));

        List<Integer> authorIds = postMap.values().stream()
                .filter(post -> post != null)
                .map(Post::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, UserPublicVO> userMap = authorIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        userService::getUserPublicInfo,
                        (existing, replacement) -> existing
                ));

        List<UserCommentVO> voList = comments.stream()
                .map(comment -> convertToUserCommentVO(comment, postMap, userMap))
                .collect(Collectors.toList());

        Page<UserCommentVO> resultPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }
}




