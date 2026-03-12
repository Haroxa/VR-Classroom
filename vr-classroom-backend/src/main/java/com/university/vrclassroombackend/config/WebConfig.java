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
                        "/api/posts/**", 
                        "/api/comments/**", 

                        "/api/users/profile", 
                        "/api/users/posts", 
                        "/api/users/comments", 
                        "/api/users/liked-posts",
                        "/api/users/liked-comments",

                        "/api/oss/sign", 

                        "/api/orders", 
                        "/api/orders/**",

                        "/api/images/post"
                )
                .excludePathPatterns(
                        "/api/users/login", 
                        "/api/users/login/phone", 
                        "/api/users", 
                        "/api/users/all",

                        "/api/test/**", 

                        "/api/rooms/*/seats"

                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，使上传的图片可以通过URL访问
        registry.addResourceHandler(staticPath)
                .addResourceLocations(staticLocation);
    }
}
