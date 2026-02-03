package com.university.vrclassroombackend.controller;

import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.util.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.university.vrclassroombackend.constant.AppConstants.*;

@RestController
@RequestMapping("/api")
public class CommonController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);
    
    @Autowired
    private OssUtil ossUtil;

    @GetMapping("/oss/sign")
    public ResponseEntity<?> getOssSign() {
        try {
            Map<String, String> sign = ossUtil.generatePostSignature();
            logger.info("获取OSS签名成功");
            return ResponseEntity.ok().body(ApiResponse.success(sign));
        } catch (Exception e) {
            logger.error("获取OSS签名失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.GET_OSS_SIGN_FAILED));
        }
    }
}
