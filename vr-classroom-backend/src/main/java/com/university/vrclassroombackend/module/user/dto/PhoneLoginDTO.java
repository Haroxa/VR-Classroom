package com.university.vrclassroombackend.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "手机号登录请求参数")
public class PhoneLoginDTO {
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
