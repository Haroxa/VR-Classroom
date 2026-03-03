package com.university.vrclassroombackend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    @Value("${wechat.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${wechat.phoneNumberUrl}")
    private String phoneNumberUrl;

    private String cachedAccessToken;
    private long accessTokenExpireTime;

    /**
     * 使用微信授权码获取 openId 和 sessionKey
     * @param code 微信授权码 (loginCode)
     * @return 包含 openId 和 sessionKey 的 Map
     * @throws Exception 异常信息
     */
    public Map<String, String> getOpenIdAndSessionKey(String code) throws Exception {
        String urlStr = code2SessionUrl + "?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        logger.info("微信 code2Session 请求 URL: {}", urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        logger.info("微信 code2Session 响应: {}", response.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());

        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            int errCode = jsonNode.get("errcode").asInt();
            String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
            logger.error("微信 code2Session 失败: errcode={}, errmsg={}", errCode, errMsg);
            throw new Exception("微信登录失败: " + errMsg);
        }

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

    /**
     * 获取微信 access_token
     * @return access_token
     * @throws Exception 异常信息
     */
    public String getAccessToken() throws Exception {
        if (cachedAccessToken != null && System.currentTimeMillis() < accessTokenExpireTime) {
            return cachedAccessToken;
        }

        String urlStr = accessTokenUrl + "?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        logger.info("微信获取 access_token 请求 URL: {}", urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        logger.info("微信获取 access_token 响应: {}", response.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());

        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            int errCode = jsonNode.get("errcode").asInt();
            String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
            logger.error("微信获取 access_token 失败: errcode={}, errmsg={}", errCode, errMsg);
            throw new Exception("获取 access_token 失败: " + errMsg);
        }

        cachedAccessToken = jsonNode.get("access_token").asText();
        int expiresIn = jsonNode.has("expires_in") ? jsonNode.get("expires_in").asInt() : 7200;
        accessTokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;

        return cachedAccessToken;
    }

    /**
     * 使用 phoneCode 获取用户手机号
     * @param phoneCode 微信手机号授权码
     * @return 用户手机号
     * @throws Exception 异常信息
     */
    public String getPhoneNumber(String phoneCode) throws Exception {
        String accessToken = getAccessToken();

        String urlStr = phoneNumberUrl + "?access_token=" + accessToken;
        logger.info("微信获取手机号请求 URL: {}", urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);

        String requestBody = "{\"code\":\"" + phoneCode + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        logger.info("微信获取手机号响应: {}", response.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());

        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
            int errCode = jsonNode.get("errcode").asInt();
            String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
            logger.error("微信获取手机号失败: errcode={}, errmsg={}", errCode, errMsg);
            throw new Exception("获取手机号失败: " + errMsg);
        }

        JsonNode phoneInfo = jsonNode.get("phone_info");
        if (phoneInfo == null || !phoneInfo.has("phoneNumber")) {
            throw new Exception("获取手机号失败: 响应数据异常");
        }

        return phoneInfo.get("phoneNumber").asText();
    }
}
