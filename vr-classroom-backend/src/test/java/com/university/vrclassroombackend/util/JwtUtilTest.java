package com.university.vrclassroombackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private Integer testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1;
        // 手动设置JWT配置
        jwtUtil.setSecret("vr-classroom-local-secret-key-2026-hmac-sha256-secure-token");
        jwtUtil.setExpiration(86400L);
        // 初始化JwtUtil
        jwtUtil.init();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(testUserId);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        System.out.println("Generated token: " + token);
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtUtil.generateToken(testUserId);
        Integer extractedUserId = jwtUtil.getUserIdFromToken(token);
        assertEquals(testUserId, extractedUserId);
        System.out.println("Extracted user ID: " + extractedUserId);
    }

    @Test
    void testVerifyToken() {
        String token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzczMTk5NTkyLCJleHAiOjE3NzMyODU5OTJ9.gGFuaBMvvlGXj3ti6jwxB6dOE8f9N3s6lHdhumwCpC-fn1T9YzdB5lTWtLnoQXJF";
        Integer extractedUserId = jwtUtil.getUserIdFromToken(token);
        assertEquals(extractedUserId != null, true);
        System.out.println("Tested Token: " + token);
        System.out.println("Extracted user ID: " + extractedUserId);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(testUserId);
        boolean isValid = jwtUtil.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        String invalidToken = "invalid_token";
        boolean isValid = jwtUtil.validateToken(invalidToken);
        assertFalse(isValid);
    }
}
