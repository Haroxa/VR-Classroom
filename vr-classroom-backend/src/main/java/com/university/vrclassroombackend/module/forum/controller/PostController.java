package com.university.vrclassroombackend.module.forum.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.annotation.RateLimiter;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.service.PostService;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;
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
public class PostController {
    
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> getPublicPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        IPage<PostVO> postPage = postService.getPublicPosts(page, categoryId, keyword);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) postPage.getCurrent());
        data.put("total", (int) postPage.getPages());
        data.put("records", postPage.getRecords());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    @RateLimiter(limit = 5, timeout = 1)
    public ResponseEntity<?> createPost(
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody PostCreateDTO dto) {
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
    public ResponseEntity<?> getPostDetail(
            @PathVariable Integer id,
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
    public ResponseEntity<?> updatePost(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody PostUpdateDTO dto) {
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
    public ResponseEntity<?> deletePost(
            @PathVariable Integer id,
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
}




