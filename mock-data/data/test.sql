-- Active: 1771720784163@@127.0.0.1@3306@vr_classroom_test

USE vr_classroom_test;

SELECT * FROM seat WHERE id IN (54, 78, 79);

-- 设置所有座位状态为锁定或者已售出的重置为可用，并且重置版本号为0
UPDATE seat SET status = 1, version = 0 WHERE status IN (2, 3);
