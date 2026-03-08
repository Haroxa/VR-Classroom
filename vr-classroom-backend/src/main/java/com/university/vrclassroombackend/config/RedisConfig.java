package com.university.vrclassroombackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import jakarta.annotation.PostConstruct;

/**
 * Redis 配置类
 * 用于配置 RedisTemplate，方便在代码中使用 Redis
 */
@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void checkConnection() {
        logger.info("=== 开始检查Redis连接 ===");
        
        // 加载环境变量到System Property
        logger.info("=== 开始加载环境变量到System Property ===");
        try {
            // 从Environment中获取环境变量并设置到System Property
            String[] envKeys = {
                "SPRING_REDIS_HOST",
                "SPRING_REDIS_PORT",
                "SPRING_REDIS_PASSWORD",
                "SPRING_REDIS_DATABASE",
            };
            
            for (String key : envKeys) {
                String value = environment.getProperty(key);
                if (value != null) {
                    System.setProperty(key, value);
                    logger.info("设置环境变量: {}={}", key, key.contains("password") ? "***" : value);
                }
            }
            logger.info("=== 环境变量加载完成 ===");
        } catch (Exception e) {
            logger.error("加载环境变量失败: {}", e.getMessage());
            e.printStackTrace();
        }
        
        try {
            if (redisTemplate == null) {
                logger.error("StringRedisTemplate 为 null");
                return;
            }
            
            RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
            if (factory == null) {
                logger.error("RedisConnectionFactory 为 null");
                return;
            }
            
            logger.info("RedisConnectionFactory 类型: {}", factory.getClass().getName());
            
            // 尝试连接
            logger.info("尝试连接到Redis服务器...");
            String result = factory.getConnection().ping();
            logger.info("Redis 连接成功: {}", result);
        } catch (Exception e) {
            logger.error("Redis 连接失败: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            logger.info("=== Redis连接检查完成 ===");
        }
    }
    
    @Bean
    public static RedisConnectionFactory redisConnectionFactory(
            @Value("${SPRING_REDIS_HOST}") String redisHost,
            @Value("${SPRING_REDIS_PORT}") int redisPort,
            @Value("${SPRING_REDIS_PASSWORD}") String redisPassword,
            @Value("${SPRING_REDIS_DATABASE}") int redisDatabase) {
        Logger logger = LoggerFactory.getLogger(RedisConfig.class);
        logger.info("=== 创建Redis连接工厂 ===");
        logger.info("Redis配置: host={}, port={}, password={}, database={}", redisHost, redisPort, redisPassword != null && redisPassword.contains("*") ? "***" : redisPassword, redisDatabase);
        
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(redisPassword);
        config.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        // 设置键序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        
        // 配置 ObjectMapper 以支持 Java 8 日期时间类型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // 设置值序列化器
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        
        // 设置哈希键序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        
        // 设置哈希值序列化器
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        
        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();
        
        return redisTemplate;
    }
}
