package com.university.vrclassroombackend.controller;

import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.dto.PostCreateDTO;
import com.university.vrclassroombackend.dto.PostUpdateDTO;
import com.university.vrclassroombackend.service.PostService;
import com.university.vrclassroombackend.vo.PostDetailVO;
import com.university.vrclassroombackend.vo.PostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> getPublicPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        List<PostVO> posts = postService.getPublicPosts(page, categoryId, keyword);
        Map<String, Object> data = new HashMap<>();
        data.put("current", page);
        data.put("total", posts.size() / Pagination.DEFAULT_PAGE_SIZE + 1);
        data.put("records", posts);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody PostCreateDTO dto) {
        try {
            Integer authorId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (authorId == null) {
                logger.warn("创建帖子失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            Integer postId = postService.createPost(dto, authorId);
            logger.info("创建帖子成功: postId={}, authorId={}", postId, authorId);
            return ResponseEntity.ok().body(ApiResponse.success(postId.toString()));
        } catch (Exception e) {
            logger.error("创建帖子失败: authorId={}, title={}", request.getAttribute(Auth.USER_ID_ATTRIBUTE), dto != null ? dto.getTitle() : null, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_POST_FAILED));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer currentUserId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            PostDetailVO post = postService.getPostDetail(id, currentUserId);
            if (post == null) {
                logger.warn("获取帖子详情失败: 帖子不存在, postId={}", id);
                return ResponseEntity.ok().body(ApiResponse.error(HttpStatus.NOT_FOUND, ErrorMessage.POST_NOT_FOUND));
            }
            return ResponseEntity.ok().body(ApiResponse.success(post));
        } catch (Exception e) {
            logger.error("获取帖子详情失败: postId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.GET_POST_DETAIL_FAILED));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody PostUpdateDTO dto) {
        try {
            Integer authorId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (authorId == null) {
                logger.warn("更新帖子失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            postService.updatePost(id, dto, authorId);
            logger.info("更新帖子成功: postId={}, authorId={}", id, authorId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("更新帖子失败: postId={}, authorId={}", id, request.getAttribute(Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UPDATE_POST_FAILED));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer authorId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (authorId == null) {
                logger.warn("删除帖子失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            postService.deletePost(id, authorId);
            logger.info("删除帖子成功: postId={}, authorId={}", id, authorId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("删除帖子失败: postId={}, authorId={}", id, request.getAttribute(Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.DELETE_POST_FAILED));
        }
    }
}
