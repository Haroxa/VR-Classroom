package com.university.vrclassroombackend.module.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.vo.PostAuditVO;
import com.university.vrclassroombackend.module.admin.vo.CommentAuditVO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员管理", description = "管理员相关接口")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private AdminService adminService;

    /**
     * 获取帖子列表（后台审核用）
     * @param page 页码，每页固定20条数据，从1开始
     * @param status 状态，没传则查全部
     * @param categoryId 分类id，没传则查全部
     * @param keyword 搜索关键词
     * @return 帖子列表
     */
    @GetMapping("/posts")
    @Operation(summary = "获取帖子列表", description = "后台审核用的帖子列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> getPosts(
            @Parameter(description = "页码，默认1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认20", example = "20") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态，没传则查全部", example = "0") @RequestParam(required = false) Integer status,
            @Parameter(description = "分类id，没传则查全部", example = "1") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "搜索关键词", example = "VR技术") @RequestParam(required = false) String keyword) {
        IPage<PostAuditVO> postsPage = adminService.getPosts(page, pageSize, status, categoryId, keyword);
        Map<String, Object> data = new HashMap<>();
        data.put("current", postsPage.getCurrent());
        data.put("total", postsPage.getPages());
        data.put("records", postsPage.getRecords());
        logger.info("获取帖子列表成功: page={}, status={}, categoryId={}, keyword={}", page, status, categoryId, keyword);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    /**
     * 获取评论列表（后台审核用）
     * @param page 页码，每页固定20条数据，从1开始
     * @param status 状态，没传则查全部
     * @return 评论列表
     */
    @GetMapping("/comments")
    @Operation(summary = "获取评论列表", description = "后台审核用的评论列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> getComments(
            @Parameter(description = "页码，默认1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认20", example = "20") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态，没传则查全部", example = "0") @RequestParam(required = false) Integer status) {
        IPage<CommentAuditVO> commentsPage = adminService.getComments(page, pageSize, status);
        Map<String, Object> data = new HashMap<>();
        data.put("current", commentsPage.getCurrent());
        data.put("total", commentsPage.getPages());
        data.put("records", commentsPage.getRecords());
        logger.info("获取评论列表成功: page={}, status={}", page, status);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    /**
     * 审核帖子
     * @param id 帖子ID
     * @param dto 审核信息
     * @return 审核结果
     */
    @PatchMapping("/posts/{id}")
    @Operation(summary = "审核帖子", description = "审核帖子状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> auditPost(
            @Parameter(description = "帖子ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "审核信息", required = true) @Valid @RequestBody PostAuditDTO dto) {
        adminService.auditPost(id, dto);
        logger.info("审核帖子成功: postId={}, status={}", id, dto.getStatus());
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    /**
     * 审核评论
     * @param id 评论ID
     * @param dto 审核信息
     * @return 审核结果
     */
    @PatchMapping("/comments/{id}")
    @Operation(summary = "审核评论", description = "审核评论状态")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> auditComment(
            @Parameter(description = "评论ID", required = true, example = "1") @PathVariable Integer id,
            @Parameter(description = "审核信息", required = true) @Valid @RequestBody CommentAuditDTO dto) {
        adminService.auditComment(id, dto);
        logger.info("审核评论成功: commentId={}, status={}", id, dto.getStatus());
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
