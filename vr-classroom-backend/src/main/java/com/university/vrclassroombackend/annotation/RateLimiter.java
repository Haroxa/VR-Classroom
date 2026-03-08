package com.university.vrclassroombackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * <p>
 * 使用Redis实现令牌桶算法的接口限流
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    
    /**
     * 限流阈值（每秒允许的请求数）
     */
    int limit() default 100;
    
    /**
     * 获取令牌超时时间（秒）
     */
    int timeout() default 1;
    
    /**
     * 限流key的前缀
     */
    String keyPrefix() default "rate_limit:";
}
