package com.university.vrclassroombackend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 * 用于调用微信相关接口
 */
@Component
public class WechatUtil {

    private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    @Value("${wechat.code2SessionUrl}")
    private String code2SessionUrl;

    /**
     * 使用微信授权码获取 openId 和 sessionKey
     * @param code 微信授权码
     * @return 包含 openId 和 sessionKey 的 Map
     * @throws Exception 异常信息
     */
    public Map<String, String> getOpenIdAndSessionKey(String code) throws Exception {
        // 构建请求 URL
        String urlStr = code2SessionUrl + "?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        logger.info("微信 code2Session 请求 URL: {}", urlStr);

        // 发送 HTTP GET 请求
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        // 解析响应
        logger.info("微信 code2Session 响应: {}", response.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());

        // 检查是否有错误
        if (jsonNode.has("errcode")) {
            int errCode = jsonNode.get("errcode").asInt();
            String errMsg = jsonNode.get("errmsg").asText();
            logger.error("微信 code2Session 失败: errcode={}, errmsg={}", errCode, errMsg);
            throw new Exception("微信登录失败: " + errMsg);
        }

        // 提取 openId 和 sessionKey
        Map<String, String> result = new HashMap<>();
        result.put("openId", jsonNode.get("openid").asText());
        if (jsonNode.has("session_key")) {
            result.put("sessionKey", jsonNode.get("session_key").asText());
        }
        if (jsonNode.has("unionid")) {
            result.put("unionId", jsonNode.get("unionid").asText());
        }

        return result;
    }
}
