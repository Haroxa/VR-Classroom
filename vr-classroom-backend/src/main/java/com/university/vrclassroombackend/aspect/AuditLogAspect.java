package com.university.vrclassroombackend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计日志切面
 * <p>
 * 使用AOP实现统一的审计日志记录
 * </p>
 *
 * @author University VR Classroom Team
 * @since 1.0.0
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);

    /**
     * JSON序列化器
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param objectMapper JSON序列化器
     */
    @Autowired
    public AuditLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 定义切点：所有带有AuditLog注解的方法
     */
    @Pointcut("@annotation(com.university.vrclassroombackend.aspect.AuditLog)")
    public void auditLogPointcut() {
    }

    /**
     * 环绕通知：记录方法执行前后的信息
     *
     * @param joinPoint 连接点
     * @param auditLog  审计日志注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("auditLogPointcut() && @annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        HttpServletRequest request = getRequest();
        String ip = getIpAddress(request);
        String uri = request != null ? request.getRequestURI() : "unknown";
        String method = request != null ? request.getMethod() : "unknown";
        
        // 获取用户ID
        Integer userId = getUserId(request);
        
        // 记录方法开始执行
        logger.info("审计日志开始 - 操作: {}, 模块: {}, 用户ID: {}, IP: {}, URI: {}, 方法: {}",
                auditLog.operation(), auditLog.module(), userId, ip, uri, method);
        
        // 记录参数
        if (auditLog.logParams()) {
            String params = getParamsJson(joinPoint.getArgs());
            logger.info("审计日志参数 - 操作: {}, 参数: {}", auditLog.operation(), params);
        }
        
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录返回值
            if (auditLog.logResult() && result != null) {
                String resultJson = getResultJson(result);
                logger.info("审计日志返回值 - 操作: {}, 返回值: {}", auditLog.operation(), resultJson);
            }
            
            return result;
        } catch (Throwable e) {
            // 记录异常
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            logger.error("审计日志异常 - 操作: {}, 模块: {}, 用户ID: {}, IP: {}, 耗时: {}ms, 异常: {}",
                    auditLog.operation(), auditLog.module(), userId, ip, executionTime, e.getMessage(), e);
            throw e;
        } finally {
            // 记录方法执行结束
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            logger.info("审计日志结束 - 操作: {}, 模块: {}, 用户ID: {}, IP: {}, 耗时: {}ms",
                    auditLog.operation(), auditLog.module(), userId, ip, executionTime);
        }
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }

    /**
     * 获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return (Integer) request.getAttribute("userId");
    }

    /**
     * 将参数转换为JSON字符串
     *
     * @param args 参数数组
     * @return JSON字符串
     */
    private String getParamsJson(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            logger.warn("参数序列化失败: {}", e.getMessage());
            return "参数序列化失败";
        }
    }

    /**
     * 将返回值转换为JSON字符串
     *
     * @param result 返回值
     * @return JSON字符串
     */
    private String getResultJson(Object result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            logger.warn("返回值序列化失败: {}", e.getMessage());
            return "返回值序列化失败";
        }
    }
}
