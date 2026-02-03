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