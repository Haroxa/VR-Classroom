package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.dto.CommentCreateDTO;
import com.university.vrclassroombackend.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.model.Comment;
import com.university.vrclassroombackend.model.Post;
import com.university.vrclassroombackend.repository.CommentRepository;
import com.university.vrclassroombackend.repository.PostRepository;
import com.university.vrclassroombackend.service.impl.CommentServiceImpl;
import com.university.vrclassroombackend.vo.CommentVO;
import com.university.vrclassroombackend.vo.UserCommentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

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
        testPost.setCommentCount(1); // 设置为1，这样删除评论时会调用postRepository.save

        testComment = new Comment();
        testComment.setId(1);
        testComment.setDate("2026-02-01 10:00:00");
        testComment.setContent("这是一个测试评论");
        testComment.setCommenterId(1);
        testComment.setPostId(1);
        testComment.setLikeCount(0);
        testComment.setStatus(1);
    }

    @Test
    void testGetPostComments() {
        when(commentRepository.findByPostId(1)).thenReturn(Arrays.asList(testComment));

        List<CommentVO> result = commentService.getPostComments(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentRepository, times(1)).findByPostId(1);
    }

    @Test
    void testCreateComment() {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setContent("新评论");
        dto.setPostId(1);

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        Integer result = commentService.createComment(dto, 1);

        assertNotNull(result);
        assertEquals(1, result);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdateComment() {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setContent("更新后的评论");

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        boolean result = commentService.updateComment(1, dto, 1);

        assertTrue(result);
        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testUpdateCommentUnauthorized() {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setContent("更新后的评论");

        testComment.setCommenterId(2);
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        boolean result = commentService.updateComment(1, dto, 1);

        assertFalse(result);
        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testDeleteComment() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        boolean result = commentService.deleteComment(1, 1);

        assertTrue(result);
        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).save(any(Comment.class));
        // postRepository.save会被调用，因为commentCount为1
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testDeleteCommentUnauthorized() {
        testComment.setCommenterId(2);
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        // 不需要mock postRepository.findById，因为commentCount为0时不会调用save
        // when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        boolean result = commentService.deleteComment(1, 1);

        assertFalse(result);
        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(postRepository, never()).findById(1);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testGetUserComments() {
        when(commentRepository.findByCommenterId(1)).thenReturn(Arrays.asList(testComment));

        List<UserCommentVO> result = commentService.getUserComments(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(commentRepository, times(1)).findByCommenterId(1);
    }
}
