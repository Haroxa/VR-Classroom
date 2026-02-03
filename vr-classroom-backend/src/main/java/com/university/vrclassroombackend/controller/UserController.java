package com.university.vrclassroombackend.controller;

import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.dto.LoginDTO;
import com.university.vrclassroombackend.model.User;
import com.university.vrclassroombackend.service.UserService;
import com.university.vrclassroombackend.util.JwtUtil;
import com.university.vrclassroombackend.vo.UserProfileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (user.getOpenId() == null || user.getOpenId().isEmpty()) {
                user.setOpenId("default_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000));
            }
            
            User savedUser = userService.saveUser(user);
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedUser.getId());
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("创建用户失败: phone={}, openId={}", user.getPhone(), user.getOpenId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.CREATE_USER_FAILED));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            User user = userService.getUserByPhone(dto.getPhone());
            if (user == null) {
                logger.warn("登录失败: 用户不存在, phone={}", dto.getPhone());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.USER_NOT_FOUND));
            }
            
            String token = jwtUtil.generateToken(user.getId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userService.getUserProfile(user.getId()));
            
            logger.info("用户登录成功: userId={}, phone={}", user.getId(), dto.getPhone());
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("登录失败: phone={}", dto.getPhone(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.LOGIN_FAILED));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户信息失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            UserProfileVO profile = userService.getUserProfile(userId);
            return ResponseEntity.ok().body(ApiResponse.success(profile));
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.GET_USER_PROFILE_FAILED));
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(jakarta.servlet.http.HttpServletRequest request,
                                        @RequestParam(defaultValue = "0") Integer page) {
        try {
            Integer userId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户帖子失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", 1);
            data.put("records", userService.getUserPosts(userId, page));
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取用户帖子失败: page={}", page, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.GET_USER_POSTS_FAILED));
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getUserComments(jakarta.servlet.http.HttpServletRequest request,
                                           @RequestParam(defaultValue = "0") Integer page) {
        try {
            Integer userId = (Integer) request.getAttribute(Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户评论失败: 未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_USER));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", 1);
            data.put("records", userService.getUserComments(userId, page));
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取用户评论失败: page={}", page, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.GET_USER_COMMENTS_FAILED));
        }
    }
}
