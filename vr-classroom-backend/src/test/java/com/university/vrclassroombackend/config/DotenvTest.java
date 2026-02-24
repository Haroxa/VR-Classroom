package com.university.vrclassroombackend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * dotenv-java 测试类
 * 用于手动加载环境变量到 System Property 并打印确认加载成功
 */
public class DotenvTest {

    @Test
    public void testDotenvLoad() {
        System.out.println("=== 开始测试 dotenv-java 加载环境变量 ===");
        
        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure()
                .directory("P:\\CODE\\School\\ZDD\\Task\\VR教室\\vr-classroom-backend")
                .load();
        
        System.out.println("✅ .env 文件加载成功");
        System.out.println("环境变量数量: " + dotenv.entries().size());
        System.out.println("\n=== 环境变量详情 ===");
        
        // 遍历并打印所有环境变量
        for (io.github.cdimascio.dotenv.DotenvEntry entry : dotenv.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 解析变量引用
            if (value.startsWith("${") && value.endsWith("}")) {
                String refKey = value.substring(2, value.length() - 1);
                String refValue = dotenv.get(refKey);
                if (refValue != null) {
                    value = refValue;
                    System.out.println(key + "=" + value + " (解析自 " + refKey + ")");
                } else {
                    System.out.println(key + "=" + value + " (无法解析引用: " + refKey + ")");
                }
            } else {
                System.out.println(key + "=" + value);
            }
            
            // 将环境变量设置到 System Property
            System.setProperty(key, value);
        }
        
        System.out.println("\n=== 验证 System Property ===");
        
        // 验证几个关键的环境变量是否已设置到 System Property
        String[] keyNames = {
            "SPRING_REDIS_HOST",
            "SPRING_REDIS_PORT", 
            "SPRING_REDIS_PASSWORD",
            "SPRING_REDIS_DATABASE",
            "REDIS_HOST",
            "REDIS_PORT",
            "REDIS_PASSWORD",
            "REDIS_DATABASE"
        };
        
        for (String key : keyNames) {
            String value = System.getProperty(key);
            if (value != null) {
                System.out.println("✅ " + key + "=" + value);
            } else {
                System.out.println("❌ " + key + "=null");
            }
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
}
