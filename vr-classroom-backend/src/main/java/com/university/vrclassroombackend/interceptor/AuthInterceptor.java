package com.university.vrclassroombackend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String path = request.getRequestURI();
            String method = request.getMethod();
            
            // 移除上下文路径（如果有）
            String contextPath = request.getContextPath();
            if (path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }
            
            // 检查是否为公开路径（无需强制认证）
            if (isPublicPath(path, method)) {
                // 处理公开路径的可选认证
                handleOptionalAuth(request);
                return true;
            }
            
            // 非公开路径需要强制认证
            return handleRequiredAuth(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.AUTH_FAILED + ": " + e.getMessage())));
            return false;
        }
    }
    
    /**
     * 检查是否为公开路径
     */
    private boolean isPublicPath(String path, String method) {
        // 公开GET接口
        return (path.equals("/api/posts") && method.equals("GET")) ||
               (path.startsWith("/api/posts/") && method.equals("GET")) ||
               (path.equals("/api/comments") && method.equals("GET"));
    }
    
    /**
     * 处理公开路径的可选认证
     */
    private void handleOptionalAuth(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(Auth.BEARER_PREFIX)) {
            try {
                token = token.substring(Auth.BEARER_PREFIX.length());
                if (jwtUtil.validateToken(token)) {
                    Integer userId = jwtUtil.getUserIdFromToken(token);
                    request.setAttribute(Auth.USER_ID_ATTRIBUTE, userId);
                }
            } catch (Exception e) {
                // token验证失败不影响访问，只是不设置userId
            }
        }
    }
    
    /**
     * 处理需要强制认证的路径
     */
    private boolean handleRequiredAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith(Auth.BEARER_PREFIX)) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_NOT_PROVIDED)));
            return false;
        }
        
        token = token.substring(Auth.BEARER_PREFIX.length()); // 移除"Bearer "前缀
        
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_TOKEN)));
            return false;
        }
        
        // 将用户ID存入请求属性
        Integer userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute(Auth.USER_ID_ATTRIBUTE, userId);
        
        return true;
    }
}