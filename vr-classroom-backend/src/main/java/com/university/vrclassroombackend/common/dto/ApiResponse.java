package com.university.vrclassroombackend.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API响应通用格式")
public class ApiResponse<T> {
    @Schema(description = "响应码，0表示成功，其他表示失败", example = "0")
    private Integer code;
    
    @Schema(description = "响应消息", example = "success")
    private String msg;
    
    @Schema(description = "响应数据")
    private T data;
    
    public ApiResponse() {
        this.code = 0;
        this.msg = "success";
    }
    
    public ApiResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, "success", null);
    }
    
    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
    
    public static <T> ApiResponse<T> error(Integer code, String msg, T data) {
        return new ApiResponse<>(code, msg, data);
    }
    
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg, null);
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
