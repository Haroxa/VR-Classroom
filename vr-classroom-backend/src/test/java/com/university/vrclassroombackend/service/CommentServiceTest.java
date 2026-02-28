package com.university.vrclassroombackend.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("测试帖子");
        testPost.setCommentCount(1); // 设置评论数为1，这样删除评论时会调用postMapper.updateById

        testComment = new Comment();
        testComment.setId(1);
        testComment.setCreatedAt(LocalDateTime.of(2026, 2, 1, 10, 0, 0));
        testComment.setContent("这是一个测试评论");
        testComment.setCommenterId(1);
        testComment.setPostId(1);
        testComment.setLikeCount(0);
        testComment.setStatus(1);
    }

    @Test
    void testGetPostComments() {
        when(commentMapper.selectByPostId(1)).thenReturn(Arrays.asList(testComment));

        List<CommentVO> result = commentService.getPostComments(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentMapper, times(1)).selectByPostId(1);
    }

    @Test
    void testCreateComment() {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setContent("新评论");
        dto.setPostId(1);

        when(commentMapper.insert(any(Comment.class))).thenReturn(1);
        when(postMapper.selectById(1)).thenReturn(testPost);
        when(postMapper.updateById(any(Post.class))).thenReturn(1);

        Integer result = commentService.createComment(dto, 1);

        assertNotNull(result);
        verify(commentMapper, times(1)).insert(any(Comment.class));
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, times(1)).updateById(any(Post.class));
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
        when(postMapper.selectById(1)).thenReturn(testPost);
        when(commentMapper.updateById(any(Comment.class))).thenReturn(1);
        when(postMapper.updateById(any(Post.class))).thenReturn(1);

        boolean result = commentService.deleteComment(1, 1);

        assertTrue(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, times(1)).updateById(any(Comment.class));
        // postMapper.updateById会被调用，因为commentCount大于0
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, times(1)).updateById(any(Post.class));
    }

    @Test
    void testDeleteCommentUnauthorized() {
        testComment.setCommenterId(2);
        when(commentMapper.selectById(1)).thenReturn(testComment);
        // 不需要mock postMapper.selectById，因为commentCount为0时不会调用updateById
        // when(postMapper.selectById(1)).thenReturn(testPost);

        boolean result = commentService.deleteComment(1, 1);

        assertFalse(result);
        verify(commentMapper, times(1)).selectById(1);
        verify(commentMapper, never()).updateById(any(Comment.class));
        verify(postMapper, never()).selectById(1);
        verify(postMapper, never()).updateById(any(Post.class));
    }

    @Test
    void testGetUserComments() {
        when(commentMapper.selectByCommenterId(1)).thenReturn(Arrays.asList(testComment));

        List<UserCommentVO> result = commentService.getUserComments(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentMapper, times(1)).selectByCommenterId(1);
    }
}

