package com.university.vrclassroombackend.service.impl;

import com.university.vrclassroombackend.dto.CommentCreateDTO;
import com.university.vrclassroombackend.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.model.Comment;
import com.university.vrclassroombackend.model.Post;
import com.university.vrclassroombackend.repository.CommentRepository;
import com.university.vrclassroombackend.repository.PostRepository;
import com.university.vrclassroombackend.service.CommentService;
import com.university.vrclassroombackend.service.UserService;
import com.university.vrclassroombackend.vo.CommentVO;
import com.university.vrclassroombackend.vo.RelatedPostVO;
import com.university.vrclassroombackend.vo.UserCommentVO;
import com.university.vrclassroombackend.vo.UserPublicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public List<CommentVO> getPostComments(Integer postId, Integer page) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        // 只返回已发布的评论
        comments = comments.stream().filter(c -> c.getStatus() == Post.STATUS_PUBLISHED).collect(Collectors.toList());
        
        // 分页处理
        int pageSize = Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, comments.size());
        if (start >= comments.size()) {
            return List.of();
        }
        comments = comments.subList(start, end);
        
        // 转换为VO
        return comments.stream().map(this::convertToCommentVO).collect(Collectors.toList());
    }

    @Override
    public Integer createComment(CommentCreateDTO dto, Integer commenterId) {
        Comment comment = new Comment();
        comment.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        comment.setContent(dto.getContent());
        comment.setCommenterId(commenterId);
        comment.setPostId(dto.getPostId());
        comment.setLikeCount(0);
        comment.setStatus(Post.STATUS_PUBLISHED);
        
        Comment savedComment = commentRepository.save(comment);
        
        // 更新帖子的评论数
        Post post = postRepository.findById(dto.getPostId()).orElse(null);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
        }
        
        return savedComment.getId();
    }

    @Override
    public boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            return false;
        }
        
        comment.setContent(dto.getContent());
        comment.setStatus(Post.STATUS_PENDING);
        commentRepository.save(comment);
        return true;
    }

    @Override
    public boolean deleteComment(Integer commentId, Integer commenterId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getCommenterId().equals(commenterId)) {
            return false;
        }
        
        comment.setStatus(Post.STATUS_DELETED);
        commentRepository.save(comment);
        
        // 更新帖子的评论数
        Post post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postRepository.save(post);
        }
        
        return true;
    }

    @Override
    public List<UserCommentVO> getUserComments(Integer userId, Integer page) {
        List<Comment> comments = commentRepository.findByCommenterId(userId);
        
        // 分页处理
        int pageSize = Pagination.DEFAULT_PAGE_SIZE;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, comments.size());
        if (start >= comments.size()) {
            return List.of();
        }
        comments = comments.subList(start, end);
        
        // 转换为VO
        return comments.stream().map(this::convertToUserCommentVO).collect(Collectors.toList());
    }
    
    private CommentVO convertToCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setDate(comment.getDate());
        vo.setContent(comment.getContent());
        vo.setCommenterId(comment.getCommenterId());
        vo.setLikeCount(comment.getLikeCount());
        vo.setLiked(false); // 暂时默认未点赞
        
        // 获取评论人信息
        UserPublicVO commenter = userService.getUserPublicInfo(comment.getCommenterId());
        vo.setCommenter(commenter);
        
        return vo;
    }
    
    private UserCommentVO convertToUserCommentVO(Comment comment) {
        UserCommentVO vo = new UserCommentVO();
        vo.setId(comment.getId());
        vo.setDate(comment.getDate());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setStatus(comment.getStatus());
        vo.setRejectReason(comment.getRejectReason());
        vo.setLiked(false); // 暂时默认未点赞
        
        // 获取关联的帖子信息
        Post post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post != null) {
            RelatedPostVO relatedPost = new RelatedPostVO();
            relatedPost.setId(post.getId());
            relatedPost.setDate(post.getDate());
            relatedPost.setTitle(post.getTitle());
            relatedPost.setStatus(post.getStatus());
            
            // 获取帖子作者信息
            UserPublicVO author = userService.getUserPublicInfo(post.getAuthorId());
            relatedPost.setAuthor(author);
            
            vo.setRelatedPost(relatedPost);
        }
        
        return vo;
    }
}
