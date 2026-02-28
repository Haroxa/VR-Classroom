package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.service.impl.PostServiceImpl;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
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
class PostServiceTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private CategoryMapper categoryMapper;

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
        testPost.setCreatedAt(LocalDateTime.of(2026, 2, 1, 10, 0, 0));
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
        when(postMapper.selectByStatus(1)).thenReturn(Arrays.asList(testPost));

        List<PostVO> result = postService.getPublicPosts(0, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postMapper, times(1)).selectByStatus(1);
    }

    @Test
    void testGetPostDetail() {
        when(postMapper.selectById(1)).thenReturn(testPost);

        PostDetailVO result = postService.getPostDetail(1, 1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试帖子", result.getTitle());
        verify(postMapper, times(1)).selectById(1);
    }

    @Test
    void testCreatePost() {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("新帖子");
        dto.setContent("新帖子的内容");
        dto.setImages(Arrays.asList("new-image.jpg"));
        dto.setLikeCount(0);

        when(postMapper.insert(any(Post.class))).thenReturn(1);

        Integer result = postService.createPost(dto, 1);

        assertNotNull(result);
        verify(postMapper, times(1)).insert(any(Post.class));
    }

    @Test
    void testUpdatePost() {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("更新后的标题");
        dto.setContent("更新后的内容");
        dto.setImages(Arrays.asList("updated-image.jpg"));
        dto.setCategoryId(1);

        when(postMapper.selectById(1)).thenReturn(testPost);
        when(postMapper.updateById(any(Post.class))).thenReturn(1);

        boolean result = postService.updatePost(1, dto, 1);

        assertTrue(result);
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, times(1)).updateById(any(Post.class));
    }

    @Test
    void testUpdatePostUnauthorized() {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("更新后的标题");

        testPost.setAuthorId(2);
        when(postMapper.selectById(1)).thenReturn(testPost);

        boolean result = postService.updatePost(1, dto, 1);

        assertFalse(result);
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, never()).updateById(any(Post.class));
    }

    @Test
    void testDeletePost() {
        when(postMapper.selectById(1)).thenReturn(testPost);
        when(postMapper.updateById(any(Post.class))).thenReturn(1);

        boolean result = postService.deletePost(1, 1);

        assertTrue(result);
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, times(1)).updateById(any(Post.class));
    }

    @Test
    void testDeletePostUnauthorized() {
        testPost.setAuthorId(2);
        when(postMapper.selectById(1)).thenReturn(testPost);

        boolean result = postService.deletePost(1, 1);

        assertFalse(result);
        verify(postMapper, times(1)).selectById(1);
        verify(postMapper, never()).updateById(any(Post.class));
    }

    @Test
    void testGetUserPosts() {
        when(postMapper.selectByAuthorId(1)).thenReturn(Arrays.asList(testPost));

        List<UserPostVO> result = postService.getUserPosts(1, 0);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postMapper, times(1)).selectByAuthorId(1);
    }
}

