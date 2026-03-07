package com.university.vrclassroombackend.integration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import com.university.vrclassroombackend.module.admin.vo.CommentAuditVO;
import com.university.vrclassroombackend.module.admin.vo.PostAuditVO;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminService 集成测试
 * <p>
 * 测试管理员服务的核心功能，包括：
 * 1. 帖子审核
 * 2. 评论审核
 * 3. 获取帖子列表
 * 4. 获取评论列表
 * </p>
 *
 * @author VR Classroom Team
 * @since 1.0.0
 */
class AdminServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    private Post testPost;
    private Comment testComment;
    private Integer testUserId = 99999;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();

        // 清理测试数据
        commentMapper.delete(new LambdaQueryWrapper<>());
        postMapper.delete(new LambdaQueryWrapper<>());

        // 创建测试帖子
        testPost = new Post();
        testPost.setTitle("测试帖子标题");
        testPost.setSummary("测试帖子摘要");
        testPost.setContent("测试帖子内容");
        testPost.setAuthorId(testUserId);
        testPost.setCategoryId(1);
        testPost.setStatus(0); // 待审核
        testPost.setDate(LocalDateTime.now());
        postMapper.insert(testPost);

        // 创建测试评论
        testComment = new Comment();
        testComment.setPostId(testPost.getId());
        testComment.setCommenterId(testUserId);
        testComment.setContent("测试评论内容");
        testComment.setStatus(0); // 待审核
        testComment.setDate(LocalDateTime.now());
        commentMapper.insert(testComment);
    }

    @Test
    void testAuditPostApprove() {
        PostAuditDTO auditDTO = new PostAuditDTO();
        auditDTO.setStatus(1); // 审核通过

        adminService.auditPost(testPost.getId(), auditDTO);

        // 验证帖子状态已更新
        Post updatedPost = postMapper.selectById(testPost.getId());
        assertEquals(1, updatedPost.getStatus());
    }

    @Test
    void testAuditPostReject() {
        PostAuditDTO auditDTO = new PostAuditDTO();
        auditDTO.setStatus(2); // 审核拒绝
        auditDTO.setRejectReason("内容违规");

        adminService.auditPost(testPost.getId(), auditDTO);

        // 验证帖子状态已更新
        Post updatedPost = postMapper.selectById(testPost.getId());
        assertEquals(2, updatedPost.getStatus());
    }

    @Test
    void testAuditPostNotFound() {
        PostAuditDTO auditDTO = new PostAuditDTO();
        auditDTO.setStatus(1);

        assertThrows(BusinessException.class, () -> adminService.auditPost(999999, auditDTO));
    }

    @Test
    void testAuditPostDeleted() {
        // 将帖子标记为已删除
        testPost.setStatus(-1);
        postMapper.updateById(testPost);

        PostAuditDTO auditDTO = new PostAuditDTO();
        auditDTO.setStatus(1);

        assertThrows(BusinessException.class, () -> adminService.auditPost(testPost.getId(), auditDTO));
    }

    @Test
    void testAuditCommentApprove() {
        CommentAuditDTO auditDTO = new CommentAuditDTO();
        auditDTO.setStatus(1); // 审核通过

        adminService.auditComment(testComment.getId(), auditDTO);

        // 验证评论状态已更新
        Comment updatedComment = commentMapper.selectById(testComment.getId());
        assertEquals(1, updatedComment.getStatus());
    }

    @Test
    void testAuditCommentReject() {
        CommentAuditDTO auditDTO = new CommentAuditDTO();
        auditDTO.setStatus(2); // 审核拒绝
        auditDTO.setRejectReason("内容违规");

        adminService.auditComment(testComment.getId(), auditDTO);

        // 验证评论状态已更新
        Comment updatedComment = commentMapper.selectById(testComment.getId());
        assertEquals(2, updatedComment.getStatus());
    }

    @Test
    void testAuditCommentNotFound() {
        CommentAuditDTO auditDTO = new CommentAuditDTO();
        auditDTO.setStatus(1);

        assertThrows(BusinessException.class, () -> adminService.auditComment(999999, auditDTO));
    }

    @Test
    void testAuditCommentDeleted() {
        // 将评论标记为已删除
        testComment.setStatus(-1);
        commentMapper.updateById(testComment);

        CommentAuditDTO auditDTO = new CommentAuditDTO();
        auditDTO.setStatus(1);

        assertThrows(BusinessException.class, () -> adminService.auditComment(testComment.getId(), auditDTO));
    }

    @Test
    void testGetPosts() {
        // 创建多个测试帖子
        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setTitle("测试帖子" + i);
            post.setSummary("测试摘要" + i);
            post.setContent("测试内容" + i);
            post.setAuthorId(testUserId);
            post.setCategoryId(1);
            post.setStatus(0);
            post.setDate(LocalDateTime.now());
            postMapper.insert(post);
        }

        IPage<PostAuditVO> result = adminService.getPosts(1, 0, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
    }

    @Test
    void testGetPostsByStatus() {
        // 创建不同状态的帖子
        Post pendingPost = new Post();
        pendingPost.setTitle("待审核帖子");
        pendingPost.setSummary("待审核摘要");
        pendingPost.setContent("内容");
        pendingPost.setAuthorId(testUserId);
        pendingPost.setCategoryId(1);
        pendingPost.setStatus(0);
        pendingPost.setDate(LocalDateTime.now());
        postMapper.insert(pendingPost);

        Post approvedPost = new Post();
        approvedPost.setTitle("已通过帖子");
        approvedPost.setSummary("已通过摘要");
        approvedPost.setContent("内容");
        approvedPost.setAuthorId(testUserId);
        approvedPost.setCategoryId(1);
        approvedPost.setStatus(1);
        approvedPost.setDate(LocalDateTime.now());
        postMapper.insert(approvedPost);

        // 查询待审核帖子
        IPage<PostAuditVO> pendingResult = adminService.getPosts(1, 0, null, null);
        assertTrue(pendingResult.getRecords().stream()
                .anyMatch(p -> p.getId().equals(pendingPost.getId())));

        // 查询已通过帖子
        IPage<PostAuditVO> approvedResult = adminService.getPosts(1, 1, null, null);
        assertTrue(approvedResult.getRecords().stream()
                .anyMatch(p -> p.getId().equals(approvedPost.getId())));
    }

    @Test
    void testGetComments() {
        // 创建多个测试评论
        for (int i = 0; i < 5; i++) {
            Comment comment = new Comment();
            comment.setPostId(testPost.getId());
            comment.setCommenterId(testUserId);
            comment.setContent("测试评论" + i);
            comment.setStatus(0);
            comment.setDate(LocalDateTime.now());
            commentMapper.insert(comment);
        }

        IPage<CommentAuditVO> result = adminService.getComments(1, 0);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
    }

    @Test
    void testGetCommentsByStatus() {
        // 创建不同状态的评论
        Comment pendingComment = new Comment();
        pendingComment.setPostId(testPost.getId());
        pendingComment.setCommenterId(testUserId);
        pendingComment.setContent("待审核评论");
        pendingComment.setStatus(0);
        pendingComment.setDate(LocalDateTime.now());
        commentMapper.insert(pendingComment);

        Comment approvedComment = new Comment();
        approvedComment.setPostId(testPost.getId());
        approvedComment.setCommenterId(testUserId);
        approvedComment.setContent("已通过评论");
        approvedComment.setStatus(1);
        approvedComment.setDate(LocalDateTime.now());
        commentMapper.insert(approvedComment);

        // 查询待审核评论
        IPage<CommentAuditVO> pendingResult = adminService.getComments(1, 0);
        assertTrue(pendingResult.getRecords().stream()
                .anyMatch(c -> c.getId().equals(pendingComment.getId())));

        // 查询已通过评论
        IPage<CommentAuditVO> approvedResult = adminService.getComments(1, 1);
        assertTrue(approvedResult.getRecords().stream()
                .anyMatch(c -> c.getId().equals(approvedComment.getId())));
    }
}
