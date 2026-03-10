package com.university.vrclassroombackend.module.forum.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.annotation.RateLimiter;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.service.PostService;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostLikeActionVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "帖子管理", description = "帖子相关接口")
public class PostController {
    
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private PostService postService;

    @GetMapping
    @Operation(summary = "获取公开帖子列表", description = "分页获取公开帖子列表，支持分类和关键词筛选")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> getPublicPosts(
            @Parameter(description = "页码，默认1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认20", example = "20") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "分类ID，可选", example = "1") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "关键词，可选", example = "VR技术") @RequestParam(required = false) String keyword) {
        IPage<PostVO> postPage = postService.getPublicPosts(page, pageSize, categoryId, keyword);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) postPage.getCurrent());
        data.put("total", (int) postPage.getPages());
        data.put("records", postPage.getRecords());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    @RateLimiter(limit = 5, timeout = 1)
    @Operation(summary = "创建帖子", description = "创建新帖子，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> createPost(
            jakarta.servlet.http.HttpServletRequest request,
            @Parameter(description = "帖子创建参数", required = true) @Valid @RequestBody PostCreateDTO dto) {
        Integer authorId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (authorId == null) {
            logger.warn("创建帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        Integer postId = postService.createPost(dto, authorId);
        logger.info("创建帖子成功: postId={}, authorId={}", postId, authorId);
        return ResponseEntity.ok().body(ApiResponse.success(postId.toString()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取帖子详情", description = "根据ID获取帖子详情")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "帖子不存在")
    public ResponseEntity<?> getPostDetail(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        PostDetailVO post = postService.getPostDetail(id, currentUserId);
        if (post == null) {
            logger.warn("获取帖子详情失败: 帖子不存在 postId={}", id);
            return ResponseEntity.ok().body(ApiResponse.error(AppConstants.HttpStatusCode.NOT_FOUND, AppConstants.ErrorMessage.POST_NOT_FOUND));
        }
        return ResponseEntity.ok().body(ApiResponse.success(post));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新帖子", description = "更新帖子信息，需要认证和权限")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    public ResponseEntity<?> updatePost(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @Parameter(description = "帖子更新参数", required = true) @Valid @RequestBody PostUpdateDTO dto) {
        Integer authorId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (authorId == null) {
            logger.warn("更新帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        boolean result = postService.updatePost(id, dto, authorId);
        if (!result) {
            logger.warn("更新帖子失败: 帖子不存在或无权限 postId={}, authorId={}", id, authorId);
            return ResponseEntity.status(AppConstants.HttpStatusCode.FORBIDDEN)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.FORBIDDEN, "帖子不存在或无权限"));
        }
        logger.info("更新帖子成功: postId={}, authorId={}", id, authorId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除帖子", description = "删除帖子，需要认证和权限")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "无权限")
    public ResponseEntity<?> deletePost(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer authorId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (authorId == null) {
            logger.warn("删除帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        boolean result = postService.deletePost(id, authorId);
        if (!result) {
            logger.warn("删除帖子失败: 帖子不存在或无权限 postId={}, authorId={}", id, authorId);
            return ResponseEntity.status(AppConstants.HttpStatusCode.FORBIDDEN)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.FORBIDDEN, "帖子不存在或无权限"));
        }
        logger.info("删除帖子成功: postId={}, authorId={}", id, authorId);
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    @PostMapping("/{postId}/likes")
    @Operation(summary = "点赞帖子", description = "为帖子点赞，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求错误")
    public ResponseEntity<?> likePost(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer postId,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("点赞帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        try {
            PostLikeActionVO result = postService.likePost(postId, userId);
            return ResponseEntity.ok().body(ApiResponse.success(result));
        } catch (RuntimeException e) {
            logger.warn("点赞帖子失败: {}", e.getMessage());
            return ResponseEntity.ok().body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, e.getMessage()));
        }
    }

    @DeleteMapping("/{postId}/likes")
    @Operation(summary = "取消点赞帖子", description = "取消对帖子的点赞，需要认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求错误")
    public ResponseEntity<?> unlikePost(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer postId,
            jakarta.servlet.http.HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("取消点赞帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        try {
            PostLikeActionVO result = postService.unlikePost(postId, userId);
            return ResponseEntity.ok().body(ApiResponse.success(result));
        } catch (RuntimeException e) {
            logger.warn("取消点赞帖子失败: {}", e.getMessage());
            return ResponseEntity.ok().body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, e.getMessage()));
        }
    }
}




