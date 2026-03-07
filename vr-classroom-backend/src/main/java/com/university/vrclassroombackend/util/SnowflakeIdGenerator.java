package com.university.vrclassroombackend.util;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;

/**
 * 雪花算法ID生成器
 * 生成64位唯一ID：1位符号位 + 41位时间戳 + 10位机器标识 + 12位序列号
 */
@Component
public class SnowflakeIdGenerator {
    
    // 起始时间戳 (2024-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1704067200000L;
    
    // 机器ID位数
    private static final long WORKER_ID_BITS = 10L;
    // 序列号位数
    private static final long SEQUENCE_BITS = 12L;
    
    // 最大机器ID
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 最大序列号
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // 机器ID左移位数
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 时间戳左移位数
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    // 机器ID
    private final long workerId;
    // 序列号
    private long sequence = 0L;
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;
    
    public SnowflakeIdGenerator() {
        this.workerId = generateWorkerId();
    }
    
    /**
     * 生成机器ID
     */
    private long generateWorkerId() {
        try {
            // 尝试从MAC地址生成
            long id = generateWorkerIdFromMac();
            if (id >= 0 && id <= MAX_WORKER_ID) {
                return id;
            }
        } catch (Exception e) {
            // 忽略异常，使用随机数
        }
        
        // 使用随机数作为机器ID
        return new SecureRandom().nextInt((int) MAX_WORKER_ID + 1);
    }
    
    /**
     * 从MAC地址生成机器ID
     */
    private long generateWorkerIdFromMac() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }
            byte[] mac = networkInterface.getHardwareAddress();
            if (mac != null && mac.length > 0) {
                // 取MAC地址的最后两个字节
                long id = ((mac[mac.length - 2] & 0xFF) << 8) | (mac[mac.length - 1] & 0xFF);
                return id & MAX_WORKER_ID;
            }
        }
        // 如果没有MAC地址，使用IP地址
        InetAddress localhost = InetAddress.getLocalHost();
        byte[] address = localhost.getAddress();
        long id = ((address[address.length - 2] & 0xFF) << 8) | (address[address.length - 1] & 0xFF);
        return id & MAX_WORKER_ID;
    }
    
    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimestamp();
        
        // 如果当前时间小于上次生成时间，说明时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + 
                    (lastTimestamp - timestamp) + " milliseconds");
        }
        
        // 如果是同一时间生成的，则序列号+1
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列号重置
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // 生成ID：时间戳部分 | 机器ID部分 | 序列号部分
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 获取当前时间戳（毫秒）
     */
    private long currentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 从ID中提取时间戳
     */
    public static LocalDateTime extractTimestamp(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
    }
}
