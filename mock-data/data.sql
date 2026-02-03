DROP DATABASE vr_classroom;

CREATE DATABASE  vr_classroom CHARACTER SET gbk COLLATE gbk_chinese_ci;

USE vr_classroom;

-- 删除现有表（按外键依赖顺序）
SET FOREIGN_KEY_CHECKS = 0;

-- 删除评论表
DROP TABLE IF EXISTS comment;

-- 删除帖子表
DROP TABLE IF EXISTS post;

-- 删除用户表
DROP TABLE IF EXISTS user;

-- 删除分类表
DROP TABLE IF EXISTS category;

-- 删除学院表
DROP TABLE IF EXISTS college;

SET FOREIGN_KEY_CHECKS = 1;

-- 创建学院表
CREATE TABLE IF NOT EXISTS college (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 创建分类表
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) UNIQUE NOT NULL,
    openId VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    avatar VARCHAR(200) NOT NULL,
    collegeId VARCHAR(10) NOT NULL,
    verifyStatus INT NOT NULL DEFAULT 0
);

-- 创建帖子表
CREATE TABLE IF NOT EXISTS post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    summary VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    authorId INT NOT NULL,
    categoryId INT NOT NULL,
    likeCount INT NOT NULL DEFAULT 0,
    shareCount INT NOT NULL DEFAULT 0,
    commentCount INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    rejectReason VARCHAR(200),
    FOREIGN KEY (authorId) REFERENCES user(id),
    FOREIGN KEY (categoryId) REFERENCES category(id)
);

-- 创建帖子图片表（对应@ElementCollection注解）
CREATE TABLE IF NOT EXISTS post_images (
    post_id INT NOT NULL,
    images VARCHAR(200) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    commenterId INT NOT NULL,
    postId INT NOT NULL,
    likeCount INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    rejectReason VARCHAR(200),
    FOREIGN KEY (commenterId) REFERENCES user(id),
    FOREIGN KEY (postId) REFERENCES post(id)
);

-- 插入学院数据
INSERT INTO college (id, name) VALUES
(1, '计算机学院'),
(2, '电子信息学院'),
(3, '机械工程学院'),
(4, '经济管理学院'),
(5, '人文学院');

-- 插入分类数据
INSERT INTO category (id, name) VALUES
(1, '未分类'),
(2, '技术分享'),
(3, '求职经验'),
(4, '校园生活'),
(5, '校友聚会'),
(6, '其他');

-- 删除id>=0
-- DELETE FROM category WHERE id >= 0;

-- 重置id
-- ALTER TABLE category AUTO_INCREMENT = 0;  

-- 插入用户数据
INSERT INTO user (id, phone, openId, name, avatar, collegeId, verifyStatus) VALUES
(1, '13800138001', 'wx_openid_001', '张三', 'assets/avatar_001.png', '1', 2),
(2, '13800138002', 'wx_openid_002', '李四', 'assets/avatar_002.png', '2', 2),
(3, '13800138003', 'wx_openid_003', '王五', 'assets/avatar_003.png', '3', 1),
(4, '13800138004', 'wx_openid_004', '赵六', 'assets/avatar_004.png', '4', 0),
(5, '13800138005', 'wx_openid_005', '钱七', 'assets/avatar_005.png', '5', 2);

-- 删除id>=6用户
-- DELETE FROM user WHERE id >= 6;

-- 重置id
-- ALTER TABLE user AUTO_INCREMENT = 6;    

-- 插入帖子数据
INSERT INTO post (id, date, title, summary, content, authorId, categoryId, likeCount, shareCount, commentCount, status, rejectReason) VALUES
(1, '2026-02-01 10:00:00', 'Spring Boot 3.0 新特性详解', 'Spring Boot 3.0 带来了很多新特性，包括对 Java 17 的支持、GraalVM 原生镜像的改进等...', 'Spring Boot 3.0 带来了很多新特性，包括对 Java 17 的支持、GraalVM 原生镜像的改进、Jakarta EE 的迁移等。本文将详细介绍这些新特性，并通过示例代码展示如何使用它们。', 1, 1, 15, 5, 8, 1, NULL),
(2, '2026-02-01 11:30:00', '阿里巴巴实习面试经验分享', '本文分享了我在阿里巴巴实习面试的全过程，包括简历准备、技术面试、HR面试等环节...', '本文分享了我在阿里巴巴实习面试的全过程，包括简历准备、技术面试、HR面试等环节的经验和技巧。希望对正在找实习或工作的同学有所帮助。', 2, 2, 23, 12, 15, 1, NULL),
(3, '2026-02-01 14:20:00', '校园图书馆新馆开放了', '学校图书馆新馆终于开放了，环境非常好，有很多新的设施和资源...', '学校图书馆新馆终于开放了，环境非常好，有很多新的设施和资源。本文分享了新馆的一些照片和使用体验。', 3, 3, 8, 3, 5, 0, NULL),
(4, '2026-02-01 16:45:00', '10周年校友聚会通知', '母校将举办10周年校友聚会，欢迎各位校友参加...', '母校将举办10周年校友聚会，欢迎各位校友参加。本文包含聚会时间、地点、流程等详细信息。', 4, 4, 12, 8, 10, 2, '内容需要进一步审核'),
(5, '2026-02-01 19:10:00', '如何高效学习计算机专业课程', '作为一名计算机专业的学生，如何高效学习专业课程是一个重要的问题...', '作为一名计算机专业的学生，如何高效学习专业课程是一个重要的问题。本文分享了一些学习方法和技巧，希望对大家有所帮助。', 5, 1, 20, 6, 12, 1, NULL);

-- 插入评论数据
INSERT INTO comment (id, date, content, commenterId, postId, likeCount, status, rejectReason) VALUES
(1, '2026-02-01 10:30:00', '非常详细的分享，学到了很多！', 2, 1, 3, 1, NULL),
(2, '2026-02-01 10:45:00', '请问 Spring Boot 3.0 对性能有什么提升吗？', 3, 1, 2, 1, NULL),
(3, '2026-02-01 11:15:00', '感谢分享，对我准备面试很有帮助！', 1, 2, 4, 1, NULL),
(4, '2026-02-01 11:45:00', '请问面试中主要考察哪些技术点？', 4, 2, 1, 1, NULL),
(5, '2026-02-01 14:40:00', '新图书馆看起来很棒，周末去看看！', 5, 3, 2, 0, NULL);

-- 插入帖子图片数据
INSERT INTO post_images (post_id, images) VALUES
-- 帖子1的图片
(1, 'assets/posts/spring-boot-1.jpg'),
(1, 'assets/posts/spring-boot-2.jpg'),
(1, 'assets/posts/spring-boot-3.jpg'),
-- 帖子2的图片
(2, 'assets/posts/interview-1.jpg'),
(2, 'assets/posts/interview-2.jpg'),
-- 帖子3的图片
(3, 'assets/posts/library-1.jpg'),
(3, 'assets/posts/library-2.jpg'),
(3, 'assets/posts/library-3.jpg'),
(3, 'assets/posts/library-4.jpg'),
-- 帖子4的图片
(4, 'assets/posts/alumni-1.jpg'),
-- 帖子5的图片
(5, 'assets/posts/study-1.jpg'),
(5, 'assets/posts/study-2.jpg');