package com.university.vrclassroombackend.module.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.user.dto.LoginDTO;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.module.user.service.UserService;
import com.university.vrclassroombackend.module.user.vo.UserCommentVO;
import com.university.vrclassroombackend.module.user.vo.UserPostVO;
import com.university.vrclassroombackend.module.user.vo.UserProfileVO;
import com.university.vrclassroombackend.util.JwtUtil;
import com.university.vrclassroombackend.util.WechatUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
        if (user.getOpenId() == null || user.getOpenId().isEmpty()) {
            user.setOpenId("default_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000));
        }

        User savedUser = userService.saveUser(user);
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedUser.getId());
        logger.info("创建用户成功: userId={}", savedUser.getId());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        String loginCode = dto.getLoginCode();
        String phoneCode = dto.getPhoneCode();

        if (loginCode == null || loginCode.isEmpty()) {
            logger.warn("登录失败: 缺少 loginCode 参数");
            return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 loginCode 参数"));
        }

        if (phoneCode == null || phoneCode.isEmpty()) {
            logger.warn("登录失败: 缺少 phoneCode 参数");
            return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 phoneCode 参数"));
        }

        String openId;
        String phone;
        try {
            Map<String, String> wechatResult = wechatUtil.getOpenIdAndSessionKey(loginCode);
            openId = wechatResult.get("openId");
            if (openId == null || openId.isEmpty()) {
                logger.warn("登录失败: 获取 openId 失败");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, "登录失败: 获取 openId 失败"));
            }
            logger.info("登录成功获取 openId: {}", openId);

            phone = wechatUtil.getPhoneNumber(phoneCode);
            if (phone == null || phone.isEmpty()) {
                logger.warn("登录失败: 获取手机号失败");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, "登录失败: 获取手机号失败"));
            }
            logger.info("登录成功获取手机号: {}", phone);
        } catch (Exception e) {
            logger.error("微信登录失败: loginCode={}", dto.getLoginCode(), e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, "微信登录失败: " + e.getMessage()));
        }

        User user = userService.getUserByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setPhone(phone);
            if (dto.getNickName() != null && !dto.getNickName().isEmpty()) {
                user.setName(dto.getNickName());
            } else {
                user.setName("微信用户" + phone.substring(7));
            }
            if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().isEmpty()) {
                user.setAvatar(dto.getAvatarUrl());
            } else {
                user.setAvatar("assets/default_avatar.png");
            }
            user.setVerifyStatus(0);
            user = userService.saveUser(user);
            logger.info("登录成功: 新用户 userId={}, openId={}, phone={}, name={}", user.getId(), openId, phone, user.getName());
        } else {
            if (!phone.equals(user.getPhone())) {
                user.setPhone(phone);
            }
            if (dto.getNickName() != null && !dto.getNickName().isEmpty() && !"微信用户".equals(user.getName())) {
                user.setName(dto.getNickName());
            }
            if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().isEmpty() && "assets/default_avatar.png".equals(user.getAvatar())) {
                user.setAvatar(dto.getAvatarUrl());
            }
            userService.updateUser(user);
            logger.info("登录成功: 老用户 userId={}, openId={}, phone={}", user.getId(), openId, phone);
        }

        String token = jwtUtil.generateToken(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userService.getUserProfile(user.getId()));

        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @PostMapping("/login/phone")
    public ResponseEntity<?> loginByPhone(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        if (phone == null || phone.isEmpty()) {
            logger.warn("手机号登录失败: 缺少 phone 参数");
            return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 phone 参数"));
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            logger.warn("手机号登录失败: 手机号格式不正确 phone={}", phone);
            return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "手机号格式不正确"));
        }

        User user = userService.getUserByPhone(phone);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setName("用户" + phone.substring(7));
            user.setAvatar("assets/default_avatar.png");
            user.setVerifyStatus(0);
            user.setOpenId("phone_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000));
            user = userService.saveUser(user);
            logger.info("手机号登录成功: 新用户 userId={}, phone={}", user.getId(), phone);
        } else {
            logger.info("手机号登录成功: 老用户 userId={}, phone={}", user.getId(), phone);
        }

        String token = jwtUtil.generateToken(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userService.getUserProfile(user.getId()));

        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("获取用户信息失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        UserProfileVO profile = userService.getUserProfile(userId);
        logger.info("获取用户信息成功: userId={}", userId);
        return ResponseEntity.ok().body(ApiResponse.success(profile));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(HttpServletRequest request,
                                          @RequestParam(defaultValue = "1") Integer page) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("获取用户帖子失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        IPage<UserPostVO> postPage = userService.getUserPosts(userId, page);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) postPage.getCurrent());
        data.put("total", (int) postPage.getPages());
        data.put("records", postPage.getRecords());
        logger.info("获取用户帖子成功: userId={}, page={}", userId, page);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getUserComments(HttpServletRequest request,
                                             @RequestParam(defaultValue = "1") Integer page) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("获取用户评论失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }
        IPage<UserCommentVO> commentPage = userService.getUserComments(userId, page);
        Map<String, Object> data = new HashMap<>();
        data.put("current", (int) commentPage.getCurrent());
        data.put("total", (int) commentPage.getPages());
        data.put("records", commentPage.getRecords());
        logger.info("获取用户评论成功: userId={}, page={}", userId, page);
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }
}
