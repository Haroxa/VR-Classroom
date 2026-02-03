package com.university.vrclassroombackend.constant;

public class AppConstants {
    
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static class HttpStatus {
        public static final int OK = 200;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
    
    public static class Auth {
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String USER_ID_ATTRIBUTE = "userId";
    }
    
    public static class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
    }
    
    public static class Post {
        public static final int STATUS_DELETED = -1;
        public static final int STATUS_PENDING = 0;
        public static final int STATUS_PUBLISHED = 1;
        public static final int STATUS_REJECTED = 2;
        public static final int SUMMARY_LENGTH = 50;
        public static final int DEFAULT_CATEGORY_ID = 1;
    }
    
    public static class ErrorMessage {
        public static final String UNAUTHORIZED_USER = "未认证用户";
        public static final String TOKEN_NOT_PROVIDED = "未提供认证token";
        public static final String INVALID_TOKEN = "无效的token";
        public static final String AUTH_FAILED = "认证失败";
        public static final String POST_NOT_FOUND = "帖子不存在";
        public static final String USER_NOT_FOUND = "用户不存在";
        public static final String CREATE_POST_FAILED = "创建帖子失败";
        public static final String GET_POST_DETAIL_FAILED = "获取帖子详情失败";
        public static final String UPDATE_POST_FAILED = "更新帖子失败";
        public static final String DELETE_POST_FAILED = "删除帖子失败";
        public static final String CREATE_COMMENT_FAILED = "创建评论失败";
        public static final String UPDATE_COMMENT_FAILED = "更新评论失败";
        public static final String DELETE_COMMENT_FAILED = "删除评论失败";
        public static final String CREATE_USER_FAILED = "创建用户失败";
        public static final String LOGIN_FAILED = "登录失败";
        public static final String GET_USER_PROFILE_FAILED = "获取用户信息失败";
        public static final String GET_USER_POSTS_FAILED = "获取用户帖子失败";
        public static final String GET_USER_COMMENTS_FAILED = "获取用户评论失败";
        public static final String GET_OSS_SIGN_FAILED = "获取OSS签名失败";
    }
}