package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.dto.PostCreateDTO;
import com.university.vrclassroombackend.dto.PostUpdateDTO;
import com.university.vrclassroombackend.model.Category;
import com.university.vrclassroombackend.model.Post;
import com.university.vrclassroombackend.repository.CategoryRepository;
import com.university.vrclassroombackend.repository.PostRepository;
import com.university.vrclassroombackend.service.impl.PostServiceImpl;
import com.university.vrclassroombackend.vo.PostDetailVO;
import com.university.vrclassroombackend.vo.PostVO;
import com.university.vrclassroombackend.vo.UserPostVO;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("技术交流");

        testPost = new Post();
        testPost.setId(1);
        testPost.setDate("2026-02-01 10:00:00");
        testPost.setTitle("测试帖子");
        testPost.setContent("这是一个测试帖子的内容，用于测试帖子服务的功能是否正常");
        testPost.setSummary("这是一个测试帖子的内容，用于测试帖子服务的功能是否正常");
        testPost.setImages(Arrays.asList("image1.jpg", "image2.jpg"));
        testPost.setAuthorId(1);
        testPost.setCategoryId(1);
        testPost.setLikeCount(0);
        testPost.setShareCount(0);
        testPost.setCommentCount(0);
        testPost.setStatus(1);
    }

    @Test
    void testGetPublicPosts() {
        when(postRepository.findByStatus(1)).thenReturn(Arrays.asList(testPost));

        List<PostVO> result = postService.getPublicPosts(0, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findByStatus(1);
    }

    @Test
    void testGetPostDetail() {
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        PostDetailVO result = postService.getPostDetail(1, 1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试帖子", result.getTitle());
        verify(postRepository, times(1)).findById(1);
    }

    @Test
    void testCreatePost() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("新帖子");
        dto.setContent("新帖子的内容");
        dto.setImages(Arrays.asList("new-image.jpg"));
        dto.setLikeCount(0);

        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Integer result = postService.createPost(dto, 1);

        assertNotNull(result);
        assertEquals(1, result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePost() {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("更新后的标题");
        dto.setContent("更新后的内容");
        dto.setImages(Arrays.asList("updated-image.jpg"));
        dto.setCategoryId(1);

        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        boolean result = postService.updatePost(1, dto, 1);

        assertTrue(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePostUnauthorized() {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("更新后的标题");

        testPost.setAuthorId(2);
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        boolean result = postService.updatePost(1, dto, 1);

        assertFalse(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testDeletePost() {
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        boolean result = postService.deletePost(1, 1);

        assertTrue(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testDeletePostUnauthorized() {
        testPost.setAuthorId(2);
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        boolean result = postService.deletePost(1, 1);

        assertFalse(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testGetUserPosts() {
        when(postRepository.findByAuthorId(1)).thenReturn(Arrays.asList(testPost));

        List<UserPostVO> result = postService.getUserPosts(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findByAuthorId(1);
    }
}
