package com.university.vrclassroombackend.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OssUtil {
    
    @Value("${oss.endpoint}")
    private String endpoint;
    
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    
    @Value("${oss.bucketName}")
    private String bucketName;

    public Map<String, String> generatePostSignature() {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 设置过期时间为1小时
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            
            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", "user-uploads/");
            respMap.put("host", "https://" + bucketName + "." + endpoint);
            respMap.put("expire", String.valueOf(expiration.getTime() / 1000));
            
            return respMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ossClient.shutdown();
        }
    }
}
