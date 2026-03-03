package com.university.vrclassroombackend.config;

import com.university.vrclassroombackend.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/posts/**", "/api/comments/**", "/api/users/profile", "/api/users/posts", "/api/users/comments", "/api/oss/sign", "/api/payment/create", "/api/payment/orders", "/api/payment/orders/**/cancel", "/api/donation/create", "/api/donation/orders", "/api/orders", "/api/orders/**")
                .excludePathPatterns("/api/users/login", "/api/users/login/phone", "/api/users", "/api/test/**", "/api/payment/orders/*", "/api/payment/callback", "/api/donation/orders/*", "/api/rooms/*/seats");
    }
}
