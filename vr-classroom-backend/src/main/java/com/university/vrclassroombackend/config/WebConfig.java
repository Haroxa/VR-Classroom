package com.university.vrclassroombackend.config;

import com.university.vrclassroombackend.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Value("${file.static.path:/assets/**}")
    private String staticPath;

    @Value("${file.static.location:file:./uploads/}")
    private String staticLocation;
    
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOrigins("http://localhost:8080", "http://localhost:5173")
    //             .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    //             .allowedHeaders("*")
    //             .allowCredentials(true);
    // }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/api/posts", // 排除GET请求
                        "/api/posts/**", // 排除GET请求
                        "/api/comments", // 排除GET请求
                        "/api/comments/**", // 排除GET请求
                        "/api/users/profile", 
                        "/api/users/posts", 
                        "/api/users/comments", 
                        "/api/images/**" // 排除POST /api/images/post
                )
                .excludePathPatterns(
                        // 登录相关
                        "/api/users/login", 
                        "/api/users/login/phone", 
                        "/api/users", 
                        
                        // 测试接口
                        "/api/test/**", 
                        
                        // 教室座位
                        "/api/rooms/*/seats", 
                        
                        // 公开帖子列表
                        "/api/posts", 
                        
                        // 帖子详情
                        "/api/posts/*", 
                        
                        // 评论列表
                        "/api/comments", 
                        
                        // 图片上传
                        "/api/images/post", 
                        
                        // 点赞/取消点赞
                        "/api/posts/*/likes", 
                        "/api/comments/*/likes", 
                        
                        // 订单相关
                        "/api/orders", 
                        "/api/orders/*"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，使上传的图片可以通过URL访问
        registry.addResourceHandler(staticPath)
                .addResourceLocations(staticLocation);
    }
}
