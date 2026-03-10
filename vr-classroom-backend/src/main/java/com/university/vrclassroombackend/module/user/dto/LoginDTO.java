package com.university.vrclassroombackend.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 微信登录DTO
 */
@Data
@Schema(description = "微信登录参数")
public class LoginDTO {
    /**
     * 微信登录码
     */
    @NotBlank(message = "微信登录码不能为空")
    @Size(max = 100, message = "微信登录码长度不能超过100个字符")
    @Schema(description = "微信登录码", example = "0a1b2c3d4e5f")
    private String loginCode;

    /**
     * 手机号授权码
     */
    @NotBlank(message = "手机号授权码不能为空")
    @Size(max = 100, message = "手机号授权码长度不能超过100个字符")
    @Schema(description = "手机号授权码", example = "0a1b2c3d4e5f")
    private String phoneCode;

    /**
     * 微信昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "微信昵称", example = "微信用户")
    private String nickName;

    /**
     * 微信头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    @Schema(description = "微信头像URL", example = "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTK2xQibX4S3pLbEicm94U4wQHicpibI9r2bKkib1ic4o0XQzQ/0")
    private String avatarUrl;
}
