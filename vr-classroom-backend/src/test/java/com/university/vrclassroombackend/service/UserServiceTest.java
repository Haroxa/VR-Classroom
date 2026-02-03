package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.model.College;
import com.university.vrclassroombackend.model.User;
import com.university.vrclassroombackend.repository.CollegeRepository;
import com.university.vrclassroombackend.repository.UserRepository;
import com.university.vrclassroombackend.service.impl.UserServiceImpl;
import com.university.vrclassroombackend.vo.UserProfileVO;
import com.university.vrclassroombackend.vo.UserPublicVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollegeRepository collegeRepository;

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
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("测试用户", result.getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        User result = userService.getUserById(999);

        assertNull(result);
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void testGetUserByPhone() {
        when(userRepository.findByPhone("13800138000")).thenReturn(testUser);

        User result = userService.getUserByPhone("13800138000");

        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
        verify(userRepository, times(1)).findByPhone("13800138000");
    }

    @Test
    void testGetUserByOpenId() {
        when(userRepository.findByOpenId("wx_openid_123")).thenReturn(testUser);

        User result = userService.getUserByOpenId("wx_openid_123");

        assertNotNull(result);
        assertEquals("wx_openid_123", result.getOpenId());
        verify(userRepository, times(1)).findByOpenId("wx_openid_123");
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.saveUser(testUser);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testGetUserProfile() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(collegeRepository.findById(1)).thenReturn(Optional.of(testCollege));

        UserProfileVO result = userService.getUserProfile(1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试用户", result.getName());
        assertEquals("计算机学院", result.getCollegeName());
        assertEquals(2, result.getVerifyStatus());
        verify(userRepository, times(1)).findById(1);
        verify(collegeRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserPublicInfo() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(collegeRepository.findById(1)).thenReturn(Optional.of(testCollege));

        UserPublicVO result = userService.getUserPublicInfo(1);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试用户", result.getName());
        assertEquals("计算机学院", result.getCollegeName());
        assertTrue(result.isVerified());
        verify(userRepository, times(1)).findById(1);
        verify(collegeRepository, times(1)).findById(1);
    }
}
