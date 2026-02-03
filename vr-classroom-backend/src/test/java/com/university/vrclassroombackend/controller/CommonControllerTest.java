package com.university.vrclassroombackend.controller;

import com.university.vrclassroombackend.util.OssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OssUtil ossUtil;

    @Test
    void testGetOssSign() throws Exception {
        Map<String, String> mockSign = new HashMap<>();
        mockSign.put("accessid", "test-access-id");
        mockSign.put("policy", "test-policy");
        mockSign.put("signature", "test-signature");
        mockSign.put("dir", "user-uploads/");
        mockSign.put("host", "https://test-bucket.oss-cn-wuhan-lr.aliyuncs.com");
        mockSign.put("expire", "1234567890");

        when(ossUtil.generatePostSignature()).thenReturn(mockSign);

        mockMvc.perform(get("/api/oss/sign"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists());
    }
}
