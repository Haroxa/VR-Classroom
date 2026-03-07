package com.university.vrclassroombackend.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解
 * <p>
 * 用于标记需要进行审计日志记录的方法
 * </p>
 *
 * @author University VR Classroom Team
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    
    /**
     * 操作名称
     *
     * @return 操作名称
     */
    String operation() default "";
    
    /**
     * 操作模块
     *
     * @return 操作模块
     */
    String module() default "";
    
    /**
     * 是否记录参数
     *
     * @return true记录参数，false不记录
     */
    boolean logParams() default false;
    
    /**
     * 是否记录返回值
     *
     * @return true记录返回值，false不记录
     */
    boolean logResult() default false;
}
