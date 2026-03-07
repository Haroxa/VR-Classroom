package com.university.vrclassroombackend.module.forum.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.service.CommentService;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
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
            @RequestParam(defaultValue = "1") Integer page) {
        IPage<CommentVO> commentPage = commentService.getPostComments(postId, page);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) commentPage.getCurrent());
        data.put("total", (int) commentPage.getPages());
        data.put("records", commentPage.getRecords());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody CommentCreateDTO dto) {
        Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (commenterId == null) {
            logger.warn("创建评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        Integer commentId = commentService.createComment(dto, commenterId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", commentId);
        logger.info("创建评论成功: commentId={}, postId={}, commenterId={}", commentId, dto.getPostId(), commenterId);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody CommentUpdateDTO dto) {
        Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (commenterId == null) {
            logger.warn("更新评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        boolean result = commentService.updateComment(id, dto, commenterId);
        if (!result) {
            logger.warn("更新评论失败: 评论不存在或无权限 commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.status(AppConstants.HttpStatusCode.FORBIDDEN)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.FORBIDDEN, "评论不存在或无权限"));
        }
        logger.info("更新评论成功: commentId={}, commenterId={}", id, commenterId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer commenterId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (commenterId == null) {
            logger.warn("删除评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        boolean result = commentService.deleteComment(id, commenterId);
        if (!result) {
            logger.warn("删除评论失败: 评论不存在或无权限 commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.status(AppConstants.HttpStatusCode.FORBIDDEN)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.FORBIDDEN, "评论不存在或无权限"));
        }
        logger.info("删除评论成功: commentId={}, commenterId={}", id, commenterId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}




