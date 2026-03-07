package com.university.vrclassroombackend.module.common.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommonController {

    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private OssUtil ossUtil;

    /**
     * 获取OSS签名
     *
     * @deprecated OSS功能暂时弃用
     */
    @Deprecated
    @GetMapping("/oss/sign")
    public ResponseEntity<?> getOssSign() {
        Map<String, String> sign = ossUtil.generatePostSignature();
        logger.info("获取OSS签名成功");
        return ResponseEntity.ok().body(ApiResponse.success(sign));
    }
}
