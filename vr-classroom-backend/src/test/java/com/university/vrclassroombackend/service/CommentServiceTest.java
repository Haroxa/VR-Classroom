package com.university.vrclassroombackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.service.impl.CommentServiceImpl;
import com.university.vrclassroombackend.module.forum.vo.CommentVO;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment testComment;
    private Post testPost;
    private UserPublicVO testUserPublicVO;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("测试帖子");
        testPost.setAuthorId(1);
        testPost.setCommentCount(1);

        testComment = new Comment();
        testComment.setId(1);
        testComment.setDate(java.time.LocalDateTime.of(2026, 2, 1, 10, 0, 0));
        testComment.setContent("这是一个测试评论");
        testComment.setCommenterId(1);
        testComment.setPostId(1);
        testComment.setLikeCount(0);
        testComment.setStatus(1);
        
        testUserPublicVO = new UserPublicVO();
        testUserPublicVO.setId("1");
        testUserPublicVO.setName("测试用户");
    }

    @Test
    void testGetPostComments() {
        Page<Comment> commentPage = new Page<>(1, 10);
        commentPage.setRecords(Arrays.asList(testComment));
        commentPage.setTotal(1);
        
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(commentPage);
        when(userService.getUserPublicInfo(1)).thenReturn(testUserPublicVO);

        IPage<CommentVO> result = commentService.getPostComments(1, 1, 20, 1);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        verify(commentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testCreateComment() {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setContent("新评论");
        dto.setPostId(1);

        // 模拟帖子存在
        when(postMapper.selectById(1)).thenReturn(testPost);
        
        // 使用ArgumentCaptor捕获insert的Comment对象，并设置id
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentMapper.insert(commentCaptor.capture())).thenAnswer(invocation -> {
            Comment capturedComment = commentCaptor.getValue();
            capturedComment.setId(100); // 模拟数据库生成的id
            return 1;
        });
        when(postMapper.incrementCommentCount(1, 1)).thenReturn(1);

        Integer result = commentService.createComment(dto, 1);

        assertNotNull(result);
        assertEquals(100, result);
        verify(commentMapper, times(1)).insert(any(Comment.class));
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, times(1)).incrementCommentCount(1, 1);
    }

    @Test
    void testUpdateComment() {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setContent("更新后的评论");

        when(commentMapper.selectById(1)).thenReturn(testComment);
        when(commentMapper.updateById(any(Comment.class))).thenReturn(1);

        boolean result = commentService.updateComment(1, dto, 1);

        assertTrue(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, times(1)).updateById(any(Comment.class));
    }

    @Test
    void testUpdateCommentUnauthorized() {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setContent("更新后的评论");

        testComment.setCommenterId(2);
        when(commentMapper.selectById(1)).thenReturn(testComment);

        boolean result = commentService.updateComment(1, dto, 1);

        assertFalse(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, never()).updateById(any(Comment.class));
    }

    @Test
    void testDeleteComment() {
        when(commentMapper.selectById(1)).thenReturn(testComment);
        when(commentMapper.updateById(any(Comment.class))).thenReturn(1);
        when(postMapper.incrementCommentCount(1, -1)).thenReturn(1);

        boolean result = commentService.deleteComment(1, 1);

        assertTrue(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, times(1)).updateById(any(Comment.class));
        // deleteComment使用incrementCommentCount而不是updateById
        verify(postMapper, times(1)).incrementCommentCount(1, -1);
    }

    @Test
    void testDeleteCommentUnauthorized() {
        testComment.setCommenterId(2);
        when(commentMapper.selectById(1)).thenReturn(testComment);

        boolean result = commentService.deleteComment(1, 1);

        assertFalse(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, never()).updateById(any(Comment.class));
        verify(postMapper, never()).incrementCommentCount(anyInt(), anyInt());
    }

    @Test
    void testGetUserComments() {
        Page<Comment> commentPage = new Page<>(1, 10);
        commentPage.setRecords(Arrays.asList(testComment));
        commentPage.setTotal(1);
        
        when(commentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(commentPage);
        when(postMapper.selectById(1)).thenReturn(testPost);
        when(userService.getUserPublicInfo(1)).thenReturn(testUserPublicVO);

        IPage<UserCommentVO> result = commentService.getUserComments(1, 1);

        assertNotNull(result);
        assertNotNull(result.getRecords());
        verify(commentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        verify(postMapper, times(1)).selectById(1);
    }
}
