package com.university.vrclassroombackend.module.forum.service.impl;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.repository.CommentRepository;
import com.university.vrclassroombackend.module.forum.repository.PostRepository;
import com.university.vrclassroombackend.module.forum.service.CommentService;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.forum.vo.RelatedPostVO;
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

import static com.university.vrclassroombackend.constant.AppConstants.*;

@Service
public class CommentServiceImpl implements CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public List<CommentVO> getPostComments(Integer postId, Integer page) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        comments = comments.stream().filter(c -> c.getStatus() == Post.STATUS_PUBLISHED).collect(Collectors.toList());
        
        int pageSize = Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, comments.size());
        if (start >= comments.size()) {
            return List.of();
        }
        comments = comments.subList(start, end);
        
        if (comments.isEmpty()) {
            return List.of();
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
        
        return comments.stream()
                .map(comment -> convertToCommentVO(comment, userMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Integer createComment(CommentCreateDTO dto, Integer commenterId) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCommenterId(commenterId);
        comment.setPostId(dto.getPostId());
        comment.setLikeCount(0);
        comment.setStatus(Post.STATUS_PUBLISHED);
        
        Comment savedComment = commentRepository.save(comment);
        
        Post post = postRepository.findById(dto.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        }
        
        return savedComment.getId();
    }

    @Override
    @Transactional
    public boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId) {
        logger.info("开始更新评论 commentId={}, commenterId={}", commentId, commenterId);
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            logger.warn("更新评论失败: 评论不存在或无权限 commentId={}, commenterId={}", commentId, commenterId);
            return false;
        }
        
        comment.setContent(dto.getContent());
        comment.setStatus(Post.STATUS_PENDING);
        commentRepository.save(comment);
        logger.info("更新评论成功: commentId={}, commenterId={}", commentId, commenterId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId, Integer commenterId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            return false;
        }
        
        comment.setStatus(Post.STATUS_DELETED);
        commentRepository.save(comment);
        
        Post post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postRepository.save(post);
        }
        
        return true;
    }

    @Override
    public List<UserCommentVO> getUserComments(Integer userId, Integer page) {
        Pageable pageable = PageRequest.of(page, Pagination.DEFAULT_PAGE_SIZE);
        Page<Comment> commentsPage = commentRepository.findByCommenterId(userId, pageable);
        List<Comment> comments = commentsPage.getContent();
        
        if (comments.isEmpty()) {
            return List.of();
        }
        
        List<Integer> postIds = comments.stream()
                .map(Comment::getPostId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Integer, Post> postMap = postIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> postRepository.findById(id).orElse(null),
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
        
        return comments.stream()
                .map(comment -> convertToUserCommentVO(comment, postMap, userMap))
                .collect(Collectors.toList());
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
            relatedPost.setDate(post.getDate());
            relatedPost.setTitle(post.getTitle());
            relatedPost.setStatus(post.getStatus());
            
            UserPublicVO author = userMap.get(post.getAuthorId());
            if (author != null) {
                relatedPost.setAuthor(author);
            }
            
            vo.setRelatedPost(relatedPost);
        }
        
        return vo;
    }
}




