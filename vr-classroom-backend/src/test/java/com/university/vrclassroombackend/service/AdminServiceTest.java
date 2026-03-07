package com.university.vrclassroombackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.service.impl.AdminServiceImpl;
import com.university.vrclassroombackend.module.admin.vo.CommentAuditVO;
import com.university.vrclassroombackend.module.admin.vo.PostAuditVO;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService 单元测试")
class AdminServiceTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CollegeMapper collegeMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Post testPost;
    private Comment testComment;
    private PostAuditDTO postAuditDTO;
    private CommentAuditDTO commentAuditDTO;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("测试帖子");
        testPost.setContent("测试内容");
        testPost.setSummary("测试摘要");
        testPost.setAuthorId(1);
        testPost.setCategoryId(1);
        testPost.setStatus(0);
        testPost.setDate(LocalDateTime.now());
        testPost.setLikeCount(0);
        testPost.setCommentCount(0);

        testComment = new Comment();
        testComment.setId(1);
        testComment.setContent("测试评论");
        testComment.setPostId(1);
        testComment.setCommenterId(1);
        testComment.setStatus(0);
        testComment.setDate(LocalDateTime.now());

        postAuditDTO = new PostAuditDTO();
        postAuditDTO.setStatus(1);

        commentAuditDTO = new CommentAuditDTO();
        commentAuditDTO.setStatus(1);
    }

    @Nested
    @DisplayName("分页功能测试")
    class PaginationTests {

        @Test
        @DisplayName("获取帖子列表-默认分页")
        void testGetPostsDefaultPagination() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());
            mockPage.setTotal(0);

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.getCurrent());
            assertEquals(20, result.getSize());
            verify(postMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取帖子列表-指定页码")
        void testGetPostsSpecificPage() {
            Page<Post> mockPage = new Page<>(2, 20);
            mockPage.setRecords(new ArrayList<>());
            mockPage.setTotal(50);

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(2, null, null, null);

            assertNotNull(result);
            assertEquals(2, result.getCurrent());
        }

        @Test
        @DisplayName("获取帖子列表-按状态筛选")
        void testGetPostsWithStatus() {
            Page<Post> mockPage = new Page<>(1, 20);
            List<Post> posts = new ArrayList<>();
            testPost.setStatus(0);
            posts.add(testPost);
            mockPage.setRecords(posts);
            mockPage.setTotal(1);

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, 0, null, null);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("获取帖子列表-按分类筛选")
        void testGetPostsWithCategory() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, null, 1, null);

            assertNotNull(result);
            verify(postMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取帖子列表-按关键词搜索")
        void testGetPostsWithKeyword() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, null, null, "测试");

            assertNotNull(result);
            verify(postMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取帖子列表-组合筛选条件")
        void testGetPostsWithMultipleFilters() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, 0, 1, "测试");

            assertNotNull(result);
            verify(postMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取评论列表-默认分页")
        void testGetCommentsDefaultPagination() {
            Page<Comment> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<CommentAuditVO> result = adminService.getComments(1, null);

            assertNotNull(result);
            assertEquals(1, result.getCurrent());
            verify(commentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("获取评论列表-按状态筛选")
        void testGetCommentsWithStatus() {
            Page<Comment> mockPage = new Page<>(1, 20);
            List<Comment> comments = new ArrayList<>();
            testComment.setStatus(0);
            comments.add(testComment);
            mockPage.setRecords(comments);

            when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<CommentAuditVO> result = adminService.getComments(1, 0);

            assertNotNull(result);
        }

        @Test
        @DisplayName("获取评论列表-空结果")
        void testGetCommentsEmptyResult() {
            Page<Comment> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());
            mockPage.setTotal(0);

            when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<CommentAuditVO> result = adminService.getComments(1, null);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }
    }

    @Nested
    @DisplayName("帖子审核测试")
    class PostAuditTests {

        @Test
        @DisplayName("审核帖子-审核通过")
        void testAuditPostApprove() {
            when(postMapper.selectById(1)).thenReturn(testPost);
            when(postMapper.updateById(any(Post.class))).thenReturn(1);

            postAuditDTO.setStatus(1);
            adminService.auditPost(1, postAuditDTO);

            verify(postMapper, times(1)).updateById(any(Post.class));
            assertEquals(1, testPost.getStatus());
        }

        @Test
        @DisplayName("审核帖子-审核拒绝")
        void testAuditPostReject() {
            when(postMapper.selectById(1)).thenReturn(testPost);
            when(postMapper.updateById(any(Post.class))).thenReturn(1);

            postAuditDTO.setStatus(2);
            postAuditDTO.setRejectReason("内容违规");
            adminService.auditPost(1, postAuditDTO);

            verify(postMapper, times(1)).updateById(any(Post.class));
            assertEquals(2, testPost.getStatus());
        }

        @Test
        @DisplayName("审核帖子-帖子不存在")
        void testAuditPostNotFound() {
            when(postMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> adminService.auditPost(999, postAuditDTO));
            verify(postMapper, never()).updateById(any(Post.class));
        }

        @Test
        @DisplayName("审核帖子-帖子已删除")
        void testAuditPostDeleted() {
            testPost.setStatus(-1);
            when(postMapper.selectById(1)).thenReturn(testPost);

            assertThrows(BusinessException.class, () -> adminService.auditPost(1, postAuditDTO));
            verify(postMapper, never()).updateById(any(Post.class));
        }

        @Test
        @DisplayName("审核帖子-更新失败")
        void testAuditPostUpdateFailed() {
            when(postMapper.selectById(1)).thenReturn(testPost);
            when(postMapper.updateById(any(Post.class))).thenReturn(0);

            assertDoesNotThrow(() -> adminService.auditPost(1, postAuditDTO));
        }
    }

    @Nested
    @DisplayName("评论审核测试")
    class CommentAuditTests {

        @Test
        @DisplayName("审核评论-审核通过")
        void testAuditCommentApprove() {
            when(commentMapper.selectById(1)).thenReturn(testComment);
            when(commentMapper.updateById(any(Comment.class))).thenReturn(1);

            commentAuditDTO.setStatus(1);
            adminService.auditComment(1, commentAuditDTO);

            verify(commentMapper, times(1)).updateById(any(Comment.class));
            assertEquals(1, testComment.getStatus());
        }

        @Test
        @DisplayName("审核评论-审核拒绝")
        void testAuditCommentReject() {
            when(commentMapper.selectById(1)).thenReturn(testComment);
            when(commentMapper.updateById(any(Comment.class))).thenReturn(1);

            commentAuditDTO.setStatus(2);
            commentAuditDTO.setRejectReason("内容违规");
            adminService.auditComment(1, commentAuditDTO);

            verify(commentMapper, times(1)).updateById(any(Comment.class));
            assertEquals(2, testComment.getStatus());
        }

        @Test
        @DisplayName("审核评论-评论不存在")
        void testAuditCommentNotFound() {
            when(commentMapper.selectById(999)).thenReturn(null);

            assertThrows(BusinessException.class, () -> adminService.auditComment(999, commentAuditDTO));
            verify(commentMapper, never()).updateById(any(Comment.class));
        }

        @Test
        @DisplayName("审核评论-评论已删除")
        void testAuditCommentDeleted() {
            testComment.setStatus(-1);
            when(commentMapper.selectById(1)).thenReturn(testComment);

            assertThrows(BusinessException.class, () -> adminService.auditComment(1, commentAuditDTO));
            verify(commentMapper, never()).updateById(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("分页-页码为0")
        void testPaginationPageZero() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(0, null, null, null);

            assertNotNull(result);
        }

        @Test
        @DisplayName("分页-负数页码")
        void testPaginationNegativePage() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(-1, null, null, null);

            assertNotNull(result);
        }

        @Test
        @DisplayName("分页-超大页码")
        void testPaginationLargePage() {
            Page<Post> mockPage = new Page<>(9999, 20);
            mockPage.setRecords(new ArrayList<>());
            mockPage.setTotal(0);

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(9999, null, null, null);

            assertNotNull(result);
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("关键词搜索-空字符串")
        void testKeywordSearchEmpty() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, null, null, "");

            assertNotNull(result);
        }

        @Test
        @DisplayName("关键词搜索-特殊字符")
        void testKeywordSearchSpecialChars() {
            Page<Post> mockPage = new Page<>(1, 20);
            mockPage.setRecords(new ArrayList<>());

            when(postMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

            IPage<PostAuditVO> result = adminService.getPosts(1, null, null, "测试%_特殊字符");

            assertNotNull(result);
        }
    }
}
