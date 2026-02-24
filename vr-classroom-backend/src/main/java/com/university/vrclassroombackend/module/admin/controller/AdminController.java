package com.university.vrclassroombackend.module.admin.controller;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.vo.PostAuditVO;
import com.university.vrclassroombackend.module.admin.vo.CommentAuditVO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private AdminService adminService;

    /**
     * 获取帖子列表（后台审核用）
     * @param page 页码，每页固定20条数据
     * @param status 状态，没传则查全部
     * @param categoryId 分类id，没传则查全部
     * @param keyword 搜索关键词
     * @return 帖子列表
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {
        try {
            List<PostAuditVO> posts = adminService.getPosts(page, status, categoryId, keyword);
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", posts.size() / AppConstants.Pagination.DEFAULT_PAGE_SIZE + 1);
            data.put("records", posts);
            logger.info("获取帖子列表成功: page={}, status={}, categoryId={}, keyword={}", page, status, categoryId, keyword);
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取帖子列表失败: page={}, status={}, categoryId={}, keyword={}", page, status, categoryId, keyword, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, "获取帖子列表失败"));
        }
    }

    /**
     * 获取评论列表（后台审核用）
     * @param page 页码，每页固定20条数据
     * @param status 状态，没传则查全部
     * @return 评论列表
     */
    @GetMapping("/comments")
    public ResponseEntity<?> getComments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) Integer status) {
        try {
            List<CommentAuditVO> comments = adminService.getComments(page, status);
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", comments.size() / AppConstants.Pagination.DEFAULT_PAGE_SIZE + 1);
            data.put("records", comments);
            logger.info("获取评论列表成功: page={}, status={}", page, status);
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取评论列表失败: page={}, status={}", page, status, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, "获取评论列表失败"));
        }
    }

    /**
     * 审核帖子
     * @param id 帖子ID
     * @param dto 审核信息
     * @return 审核结果
     */
    @PatchMapping("/posts/{id}")
    public ResponseEntity<?> auditPost(
            @PathVariable String id,
            @RequestBody PostAuditDTO dto) {
        try {
            adminService.auditPost(Integer.parseInt(id), dto);
            logger.info("审核帖子成功: postId={}, status={}", id, dto.getStatus());
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("审核帖子失败: postId={}, status={}", id, dto.getStatus(), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, "审核帖子失败"));
        }
    }

    /**
     * 审核评论
     * @param id 评论ID
     * @param dto 审核信息
     * @return 审核结果
     */
    @PatchMapping("/comments/{id}")
    public ResponseEntity<?> auditComment(
            @PathVariable String id,
            @RequestBody CommentAuditDTO dto) {
        try {
            adminService.auditComment(Integer.parseInt(id), dto);
            logger.info("审核评论成功: commentId={}, status={}", id, dto.getStatus());
            return ResponseEntity.ok().body(ApiResponse.success());
        } catch (Exception e) {
            logger.error("审核评论失败: commentId={}, status={}", id, dto.getStatus(), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, "审核评论失败"));
        }
    }
}
