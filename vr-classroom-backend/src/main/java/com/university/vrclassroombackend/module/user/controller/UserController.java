package com.university.vrclassroombackend.module.user.controller;

import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.user.dto.LoginDTO;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.util.JwtUtil;
import com.university.vrclassroombackend.util.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private WechatUtil wechatUtil;

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
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.CREATE_USER_FAILED));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            User user = userService.getUserByPhone(dto.getPhone());
            if (user == null) {
                logger.warn("登录失败: 用户不存在 phone={}", dto.getPhone());
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.USER_NOT_FOUND));
            }
            
            String token = jwtUtil.generateToken(user.getId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userService.getUserProfile(user.getId()));
            
            logger.info("用户登录成功: userId={}, phone={}", user.getId(), dto.getPhone());
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("登录失败: phone={}", dto.getPhone(), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.LOGIN_FAILED));
        }
    }
    
    @PostMapping("/wechat/login")
    public ResponseEntity<?> wechatLogin(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null || code.isEmpty()) {
                logger.warn("微信登录失败: 缺少 code 参数");
                return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST).body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 code 参数"));
            }
            
            // 调用微信接口，使用 code 换取 openId 和 sessionKey
            Map<String, String> wechatResult = wechatUtil.getOpenIdAndSessionKey(code);
            String openId = wechatResult.get("openId");
            if (openId == null || openId.isEmpty()) {
                logger.warn("微信登录失败: 获取 openId 失败");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, "微信登录失败: 获取 openId 失败"));
            }
            logger.info("微信登录成功获取 openId: {}", openId);
            
            // 根据 openId 查找用户
            User user = userService.getUserByOpenId(openId);
            if (user == null) {
                // 如果用户不存在，创建新用户
                user = new User();
                user.setOpenId(openId);
                user.setName("微信用户");
                user.setAvatar("assets/default_avatar.png");
                user.setVerifyStatus(0);
                user = userService.saveUser(user);
                logger.info("微信登录成功: 新用户 userId={}, openId={}", user.getId(), openId);
            } else {
                logger.info("微信登录成功: 老用户 userId={}, openId={}", user.getId(), openId);
            }
            
            // 生成 JWT token
            String token = jwtUtil.generateToken(user.getId());
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userService.getUserProfile(user.getId()));
            
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("微信登录失败", e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, "微信登录失败"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(jakarta.servlet.http.HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户信息失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            UserProfileVO profile = userService.getUserProfile(userId);
            return ResponseEntity.ok().body(ApiResponse.success(profile));
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.GET_USER_PROFILE_FAILED));
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(jakarta.servlet.http.HttpServletRequest request,
                                        @RequestParam(defaultValue = "0") Integer page) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户帖子失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", 1);
            data.put("records", userService.getUserPosts(userId, page));
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取用户帖子失败: page={}", page, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.GET_USER_POSTS_FAILED));
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getUserComments(jakarta.servlet.http.HttpServletRequest request,
                                           @RequestParam(defaultValue = "0") Integer page) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("获取用户评论失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED).body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("current", page);
            data.put("total", 1);
            data.put("records", userService.getUserComments(userId, page));
            return ResponseEntity.ok().body(ApiResponse.success(data));
        } catch (Exception e) {
            logger.error("获取用户评论失败: page={}", page, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR).body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, AppConstants.ErrorMessage.GET_USER_COMMENTS_FAILED));
        }
    }
}




