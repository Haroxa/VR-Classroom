package com.university.vrclassroombackend.controller;

import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.dto.CommentCreateDTO;
import com.university.vrclassroombackend.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.service.CommentService;
import com.university.vrclassroombackend.vo.CommentVO;
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
@RequestMapping("/api/comments")
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
        data.put("total", comments.size() / Pagination.DEFAULT_PAGE_SIZE + 1);
        data.put("records", comments);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody CommentCreateDTO dto) {
        try {
            Integer commenterId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("创建评论失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            Integer commentId = commentService.createComment(dto, commenterId);
            Map<String, Object> data = new HashMap<>();
            data.put("id", commentId);
            logger.info("创建评论成功: commentId={}, postId={}, commenterId={}", commentId, dto.getPostId(), commenterId);
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("创建评论失败: postId={}, commenterId={}", dto.getPostId(), request.getAttribute(Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_COMMENT_FAILED));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody CommentUpdateDTO dto) {
        try {
            Integer commenterId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("更新评论失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            commentService.updateComment(id, dto, commenterId);
            logger.info("更新评论成功: commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("更新评论失败: commentId={}, commenterId={}", id, request.getAttribute(Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UPDATE_COMMENT_FAILED));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer id,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer commenterId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (commenterId == null) {
                logger.warn("删除评论失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            commentService.deleteComment(id, commenterId);
            logger.info("删除评论成功: commentId={}, commenterId={}", id, commenterId);
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("删除评论失败: commentId={}, commenterId={}", id, request.getAttribute(Auth.USER_ID_ATTRIBUTE), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.DELETE_COMMENT_FAILED));
        }
    }
}
