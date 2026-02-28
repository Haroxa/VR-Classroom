package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import com.university.vrclassroombackend.module.user.service.impl.UserServiceImpl;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.module.user.vo.UserPublicVO;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private CollegeMapper collegeMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private College testCollege;

    @BeforeEach
    void setUp() {
        testCollege = new College();
        testCollege.setId(1);
        testCollege.setName("计算机学院");

        testUser = new User();
        testUser.setId(1);
        testUser.setPhone("13800138000");
        testUser.setOpenId("wx_openid_123");
        testUser.setName("测试用户");
        testUser.setAvatar("avatar.jpg");
        testUser.setCollegeId("1");
        testUser.setVerifyStatus(2);
    }

    @Test
    void testGetUserById() {
        when(userMapper.selectById(1)).thenReturn(testUser);

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("测试用户", result.getName());
        verify(userMapper, times(1)).selectById(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userMapper.selectById(999)).thenReturn(null);

        User result = userService.getUserById(999);

        assertNull(result);
        verify(userMapper, times(1)).selectById(999);
    }

    @Test
    void testGetUserByPhone() {
        when(userMapper.selectByPhone("13800138000")).thenReturn(testUser);

        User result = userService.getUserByPhone("13800138000");

        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
        verify(userMapper, times(1)).selectByPhone("13800138000");
    }

    @Test
    void testGetUserByOpenId() {
        when(userMapper.selectByOpenId("wx_openid_123")).thenReturn(testUser);

        User result = userService.getUserByOpenId("wx_openid_123");

        assertNotNull(result);
        assertEquals("wx_openid_123", result.getOpenId());
        verify(userMapper, times(1)).selectByOpenId("wx_openid_123");
    }

    @Test
    void testSaveUser() {
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.saveUser(testUser);

        assertNotNull(result);
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    void testGetUserProfile() {
        when(userMapper.selectById(1)).thenReturn(testUser);
        when(collegeMapper.selectById(1)).thenReturn(testCollege);

        UserProfileVO result = userService.getUserProfile(1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试用户", result.getName());
        assertEquals("计算机学院", result.getCollegeName());
        assertEquals(2, result.getVerifyStatus());
        verify(userMapper, times(1)).selectById(1);
        verify(collegeMapper, times(1)).selectById(1);
    }

    @Test
    void testGetUserPublicInfo() {
        when(userMapper.selectById(1)).thenReturn(testUser);
        when(collegeMapper.selectById(1)).thenReturn(testCollege);

        UserPublicVO result = userService.getUserPublicInfo(1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试用户", result.getName());
        assertEquals("计算机学院", result.getCollegeName());
        assertTrue(result.isVerified());
        verify(userMapper, times(1)).selectById(1);
        verify(collegeMapper, times(1)).selectById(1);
    }
}

