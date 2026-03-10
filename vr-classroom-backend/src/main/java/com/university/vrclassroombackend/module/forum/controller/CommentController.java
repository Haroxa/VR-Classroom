package com.university.vrclassroombackend.module.forum.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.annotation.RateLimiter;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.service.CommentService;
import com.university.vrclassroombackend.module.forum.vo.CommentLikeActionVO;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "评论管理", description = "评论相关接口")
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;

    @GetMapping
    @Operation(summary = "获取帖子评论列表", description = "分页获取指定帖子的评论列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> getPostComments(
            @Parameter(description = "帖子ID", required = true, example = "1") @RequestParam Integer postId,
            @Parameter(description = "页码，默认1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认20", example = "20") @RequestParam(defaultValue = "20") Integer pageSize) {
        IPage<CommentVO> commentPage = commentService.getPostComments(postId, page, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) commentPage.getCurrent());
        data.put("total", (int) commentPage.getPages());
        data.put("records", commentPage.getRecords());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    @RateLimiter(limit = 10, timeout = 1)
    @Operation(summary = "创建评论", description = "创建新评论，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> createComment(
            jakarta.servlet.http.HttpServletRequest request,
            @Parameter(description = "评论创建参数", required = true) @Valid @RequestBody CommentCreateDTO dto) {
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
    @Operation(summary = "更新评论", description = "更新评论信息，需要认证和权限")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    public ResponseEntity<?> updateComment(
            @Parameter(description = "评论ID", required = true, example = "1") @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @Parameter(description = "评论更新参数", required = true) @Valid @RequestBody CommentUpdateDTO dto) {
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
    @Operation(summary = "删除评论", description = "删除评论，需要认证和权限")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "评论ID", required = true, example = "1") @PathVariable Integer id,
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

    @PostMapping("/{commentId}/likes")
    @Operation(summary = "点赞评论", description = "为评论点赞，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求错误")
    public ResponseEntity<?> likeComment(
            @Parameter(description = "评论ID", required = true, example = "1") @PathVariable Integer commentId,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("点赞评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        try {
            CommentLikeActionVO result = commentService.likeComment(commentId, userId);
            return ResponseEntity.ok().body(ApiResponse.success(result));
        } catch (RuntimeException e) {
            logger.warn("点赞评论失败: {}", e.getMessage());
            return ResponseEntity.ok().body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}/likes")
    @Operation(summary = "取消点赞评论", description = "取消对评论的点赞，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求错误")
    public ResponseEntity<?> unlikeComment(
            @Parameter(description = "评论ID", required = true, example = "1") @PathVariable Integer commentId,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("取消点赞评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        try {
            CommentLikeActionVO result = commentService.unlikeComment(commentId, userId);
            return ResponseEntity.ok().body(ApiResponse.success(result));
        } catch (RuntimeException e) {
            logger.warn("取消点赞评论失败: {}", e.getMessage());
            return ResponseEntity.ok().body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, e.getMessage()));
        }
    }
}




