package com.university.vrclassroombackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.vrclassroombackend.dto.CommentCreateDTO;
import com.university.vrclassroombackend.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    void testGetPostComments() throws Exception {
        mockMvc.perform(get("/api/comments")
                        .param("postId", "1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testCreateComment() throws Exception {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setContent("这是一个测试评论");
        dto.setPostId(1);

        when(commentService.createComment(any(CommentCreateDTO.class), anyInt())).thenReturn(1);

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("1"));
    }

    @Test
    void testUpdateComment() throws Exception {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setContent("更新后的评论内容");

        mockMvc.perform(put("/api/comments/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/1")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("success"));
    }
}
