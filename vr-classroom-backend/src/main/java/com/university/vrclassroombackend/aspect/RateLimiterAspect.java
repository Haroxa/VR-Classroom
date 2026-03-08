package com.university.vrclassroombackend.aspect;

import com.university.vrclassroombackend.annotation.RateLimiter;
import com.university.vrclassroombackend.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流切面
 * <p>
 * 使用Redis Lua脚本实现令牌桶算法的接口限流
 * </p>
 */
@Aspect
@Component
public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    private DefaultRedisScript<Long> rateLimitScript;

    @PostConstruct
    public void init() {
        rateLimitScript = new DefaultRedisScript<>();
        rateLimitScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/rate_limit.lua")));
        rateLimitScript.setResultType(Long.class);
    }

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String key = rateLimiter.keyPrefix() + className + ":" + methodName;
        
        int limit = rateLimiter.limit();
        int timeout = rateLimiter.timeout();
        
        Long result = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(timeout)
        );
        
        if (result == null || result == 0) {
            logger.warn("接口限流触发: className={}, methodName={}, limit={}", className, methodName, limit);
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
        
        return joinPoint.proceed();
    }
}
