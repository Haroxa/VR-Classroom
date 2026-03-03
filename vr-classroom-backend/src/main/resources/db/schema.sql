-- Active: 1771720784163@@127.0.0.1@3306@vr_classroom
-- 数据库初始化脚本
-- 只包含表结构创建，不包含数据插入
-- 数据插入操作请使用 mock-data/data.sql 文件

-- 选择数据库
USE vr_classroom;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 0;

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
    open_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    avatar VARCHAR(200) NOT NULL,
    college_id VARCHAR(10) NOT NULL,
    verify_status INT NOT NULL DEFAULT 0 COMMENT '认证状态：0-未认证，1-审核中，2-已认证'
);

-- 创建帖子表
CREATE TABLE IF NOT EXISTS post (
    id INT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    date VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    summary VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id INT NOT NULL,
    category_id INT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    share_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    reject_reason VARCHAR(200),
    FOREIGN KEY (author_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    INDEX idx_post_created_at (created_at DESC),
    INDEX idx_post_author_id (author_id),
    INDEX idx_post_category_id (category_id),
    INDEX idx_post_status (status)
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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    date VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    commenter_id INT NOT NULL,
    post_id INT NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0,
    reject_reason VARCHAR(200),
    FOREIGN KEY (commenter_id) REFERENCES user(id),
    FOREIGN KEY (post_id) REFERENCES post(id),
    INDEX idx_comment_created_at (created_at DESC),
    INDEX idx_comment_commenter_id (commenter_id),
    INDEX idx_comment_post_id (post_id),
    INDEX idx_comment_status (status)
);

-- 创建校区表
CREATE TABLE IF NOT EXISTS campus (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    sort_order INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 创建建筑表
CREATE TABLE IF NOT EXISTS building (
    id INT PRIMARY KEY AUTO_INCREMENT,
    campus_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    vr_model_url VARCHAR(255),
    sort_order INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (campus_id) REFERENCES campus(id),
    INDEX idx_building_campus_id (campus_id),
    INDEX idx_building_active (active)
);

-- 创建教室表
CREATE TABLE IF NOT EXISTS classroom (
    id INT PRIMARY KEY AUTO_INCREMENT,
    building_id INT NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    name VARCHAR(50),
    floor INT,
    description TEXT,
    vr_model_url VARCHAR(255),
    total_rows INT NOT NULL DEFAULT 0,
    total_cols INT NOT NULL DEFAULT 0,
    seat_count INT NOT NULL,
    claimed_count INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (building_id) REFERENCES building(id),
    INDEX idx_classroom_building_id (building_id),
    INDEX idx_classroom_active (active)
);

-- 创建座位表
CREATE TABLE IF NOT EXISTS seat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT NOT NULL,
    `row` INT NOT NULL,
    `col` INT NOT NULL,
    price INT NOT NULL DEFAULT 10000 COMMENT '座位价格(单位：分)',
    status INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    donor_id INT,
    claimed_at DATETIME,
    reserved_at DATETIME,
    reserve_expire_at DATETIME,
    FOREIGN KEY (room_id) REFERENCES classroom(id),
    INDEX idx_seat_room_id (room_id),
    INDEX idx_seat_status (status)
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    campus_id INT COMMENT '校区ID',
    building_id INT COMMENT '教学楼ID',
    room_id INT COMMENT '教室ID',
    amount INT NOT NULL COMMENT '订单总金额(单位：分)',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)',
    expires_at DATETIME NOT NULL COMMENT '订单超时时间（创建时间+10分钟）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_order_user_id (user_id),
    INDEX idx_order_campus_id (campus_id),
    INDEX idx_order_building_id (building_id),
    INDEX idx_order_room_id (room_id),
    INDEX idx_order_status (status),
    INDEX idx_order_created_at (created_at)
);

-- 创建订单座位关联表
CREATE TABLE IF NOT EXISTS order_seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    seat_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id),
    INDEX idx_order_seat_order_id (order_id),
    INDEX idx_order_seat_seat_id (seat_id)
);

-- 创建捐赠订单表
CREATE TABLE IF NOT EXISTS donation_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    donor_id INT NOT NULL,
    seat_id INT NOT NULL,
    tier_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    message TEXT,
    status INT NOT NULL DEFAULT 0,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    completed_at DATETIME,
    cancelled_at DATETIME,
    failed_at DATETIME,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (donor_id) REFERENCES user(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id),
    INDEX idx_donation_order_donor_id (donor_id),
    INDEX idx_donation_order_seat_id (seat_id),
    INDEX idx_donation_order_status (status),
    INDEX idx_donation_order_created_at (created_at)
);

-- 创建支付订单表
CREATE TABLE IF NOT EXISTS payment_order (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    order_no VARCHAR(50) UNIQUE NOT NULL,
    status INT NOT NULL DEFAULT 0,
    payment_method VARCHAR(20),
    transaction_id VARCHAR(100) UNIQUE,
    product_type VARCHAR(20) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    remark TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    completed_at DATETIME,
    cancelled_at DATETIME,
    failed_at DATETIME,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_payment_order_user_id (user_id),
    INDEX idx_payment_order_status (status),
    INDEX idx_payment_order_created_at (created_at),
    INDEX idx_payment_order_product (product_type, product_id)
);

-- 创建证书表
CREATE TABLE IF NOT EXISTS certificate (
    id INT PRIMARY KEY AUTO_INCREMENT,
    claim_id INT NOT NULL,
    donor_id INT NOT NULL,
    certificate_no VARCHAR(100) NOT NULL,
    certificate_url VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_certificate_donor_id (donor_id)
);

-- 重置外键检查
SET FOREIGN_KEY_CHECKS = 1;
