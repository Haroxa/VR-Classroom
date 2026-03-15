package com.university.vrclassroombackend.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "绑定手机号请求参数")
public class BindPhoneDTO {
    @Schema(description = "微信手机号code", required = true, example = "023456")
    private String phoneCode;

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
