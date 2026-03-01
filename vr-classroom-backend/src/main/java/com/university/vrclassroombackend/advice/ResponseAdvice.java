package com.university.vrclassroombackend.advice;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 支持所有方法的响应包装
        return true;
    }
    
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, 
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType, 
                                 ServerHttpRequest request, ServerHttpResponse response) {
        // 跳过 Swagger 相关端点的响应包装
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }
        
        // 如果已经是 ApiResponse 类型，直接返回
        if (body instanceof ApiResponse) {
            return body;
        }
        
        // 如果返回类型是 String，需要特殊处理以避免类型转换错误
        if (body instanceof String) {
            return body;
        }
        
        // 否则包装成成功响应
        return ApiResponse.success(body);
    }
}
