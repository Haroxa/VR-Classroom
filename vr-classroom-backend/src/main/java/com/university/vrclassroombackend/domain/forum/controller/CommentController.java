package com.university.vrclassroombackend.domain.forum.controller;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.domain.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.domain.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.domain.forum.service.CommentService;
import com.university.vrclassroombackend.domain.forum.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getPostComments(
            @RequestParam Integer postId,
            @RequestParam(defaultValue = "0") Integer page) {
        List<CommentVO> comments = commentService.getPostComments(postId, page);
        Map<String, Object> data = new HashMap<>();
        data.put("current", page);
        data.put("total", comments.size() / AppConstants.Pagination.DEFAULT_PAGE_SIZE + 1);
        data.put("records", comments);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody CommentCreateDTO dto) {
        try {
            Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("创建评论失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            Integer commentId = commentService.createComment(dto, commenterId);
            Map<String, Object> data = new HashMap<>();
            data.put("id", commentId);
            logger.info("创建评论成功: commentId={}, postId={}, commenterId={}", commentId, dto.getPostId(), commenterId);
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("创建评论失败: postId={}, commenterId={}", dto.getPostId(), request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.CREATE_COMMENT_FAILED));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody CommentUpdateDTO dto) {
        try {
            Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("更新评论失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            commentService.updateComment(id, dto, commenterId);
            logger.info("更新评论成功: commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("更新评论失败: commentId={}, commenterId={}", id, request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.UPDATE_COMMENT_FAILED));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("删除评论失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            commentService.deleteComment(id, commenterId);
            logger.info("删除评论成功: commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("删除评论失败: commentId={}, commenterId={}", id, request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.DELETE_COMMENT_FAILED));
        }
    }
}
