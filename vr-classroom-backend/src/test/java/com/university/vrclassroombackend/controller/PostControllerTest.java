package com.university.vrclassroombackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.vrclassroombackend.dto.PostCreateDTO;
import com.university.vrclassroombackend.dto.PostUpdateDTO;
import com.university.vrclassroombackend.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    void testGetPublicPosts() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("categoryId", "1")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testCreatePost() throws Exception {
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("测试帖子");
        dto.setContent("这是一个测试帖子的内容");
        dto.setImages(Arrays.asList("image1.jpg", "image2.jpg"));
        dto.setLikeCount(0);

        when(postService.createPost(any(PostCreateDTO.class), anyInt())).thenReturn(1);

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("1"));
    }

    @Test
    void testGetPostDetail() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testUpdatePost() throws Exception {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("更新后的标题");
        dto.setContent("更新后的内容");
        dto.setImages(Arrays.asList("image1.jpg"));
        dto.setCategoryId(1);

        mockMvc.perform(put("/api/posts/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/1")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }
}
