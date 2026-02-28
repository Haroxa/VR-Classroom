-- 测试数据初始化脚本
-- 用于开发和测试环境的数据插入
-- 请确保在执行此脚本前已创建所有表结构

-- 使用vr_classroom数据库
USE vr_classroom;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据（如果存在）
TRUNCATE TABLE certificate;
TRUNCATE TABLE payment_order;
TRUNCATE TABLE donation_order;
TRUNCATE TABLE seat;
TRUNCATE TABLE classroom;
TRUNCATE TABLE building;
TRUNCATE TABLE campus;
TRUNCATE TABLE comment;
TRUNCATE TABLE post_images;
TRUNCATE TABLE post;
TRUNCATE TABLE user;
TRUNCATE TABLE category;
TRUNCATE TABLE college;

-- 重置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入学院数据
INSERT INTO college (name) VALUES
('计算机科学与技术学院'),
('电子工程学院'),
('机械工程学院'),
('经济管理学院'),
('人文学院'),
('外国语学院');

-- 插入分类数据
INSERT INTO category (name) VALUES
('技术分享'),
('学习交流'),
('校园生活'),
('活动通知'),
('求助问答');

-- 插入校区数据
INSERT INTO campus (name, description, sort_order, active) VALUES
('主校区', '学校的主要校区，包含大部分学院和教学楼', 1, TRUE),
('东校区', '主要为理工科实验室和教学楼', 2, TRUE),
('西校区', '主要为文科和艺术学院', 3, TRUE);

-- 插入建筑数据
INSERT INTO building (campus_id, name, description, sort_order, active) VALUES
(1, '第一教学楼', '主要用于本科生教学', 1, TRUE),
(1, '第二教学楼', '主要用于研究生教学', 2, TRUE),
(1, '图书馆', '学校的主要图书馆', 3, TRUE),
(2, '实验楼', '理工科实验室', 1, TRUE),
(3, '艺术楼', '艺术学院教学楼', 1, TRUE);

-- 插入教室数据
INSERT INTO classroom (building_id, name, floor, seat_count, active) VALUES
(1, '101教室', 1, 50, TRUE),
(1, '102教室', 1, 50, TRUE),
(1, '201教室', 2, 100, TRUE),
(1, '202教室', 2, 100, TRUE),
(2, '301教室', 3, 80, TRUE),
(2, '302教室', 3, 80, TRUE),
(3, '阅览室', 1, 200, TRUE),
(4, '物理实验室', 1, 30, TRUE),
(5, '美术教室', 1, 40, TRUE);

-- 插入座位数据（为101教室创建座位）
INSERT INTO seat (classroom_id, seat_row, seat_column, status) VALUES
(1, '1', '1', 0),
(1, '1', '2', 0),
(1, '1', '3', 0),
(1, '1', '4', 0),
(1, '1', '5', 0),
(1, '2', '1', 0),
(1, '2', '2', 0),
(1, '2', '3', 0),
(1, '2', '4', 0),
(1, '2', '5', 0),
(1, '3', '1', 0),
(1, '3', '2', 0),
(1, '3', '3', 0),
(1, '3', '4', 0),
(1, '3', '5', 0),
(1, '4', '1', 0),
(1, '4', '2', 0),
(1, '4', '3', 0),
(1, '4', '4', 0),
(1, '4', '5', 0),
(1, '5', '1', 0),
(1, '5', '2', 0),
(1, '5', '3', 0),
(1, '5', '4', 0),
(1, '5', '5', 0),
(1, '6', '1', 0),
(1, '6', '2', 0),
(1, '6', '3', 0),
(1, '6', '4', 0),
(1, '6', '5', 0),
(1, '7', '1', 0),
(1, '7', '2', 0),
(1, '7', '3', 0),
(1, '7', '4', 0),
(1, '7', '5', 0),
(1, '8', '1', 0),
(1, '8', '2', 0),
(1, '8', '3', 0),
(1, '8', '4', 0),
(1, '8', '5', 0),
(1, '9', '1', 0),
(1, '9', '2', 0),
(1, '9', '3', 0),
(1, '9', '4', 0),
(1, '9', '5', 0),
(1, '10', '1', 0),
(1, '10', '2', 0),
(1, '10', '3', 0),
(1, '10', '4', 0),
(1, '10', '5', 0);

-- 插入用户数据
INSERT INTO user (phone, open_id, name, avatar, college_id, verify_status) VALUES
('13800138001', 'openid_001', '张三', 'https://example.com/avatar1.jpg', '1', 0),
('13800138002', 'openid_002', '李四', 'https://example.com/avatar2.jpg', '2', 1),
('13800138003', 'openid_003', '王五', 'https://example.com/avatar3.jpg', '3', 2);

-- 插入帖子数据
INSERT INTO post (date, title, summary, content, author_id, category_id, status) VALUES
('2026-02-24 10:00:00', '欢迎使用VR教室系统', '这是一个全新的VR教室系统，为大家提供沉浸式的学习体验', '大家好，欢迎使用我们的VR教室系统！\n\n这个系统可以让你在虚拟环境中体验真实的教室场景，与其他同学互动交流。\n\n希望大家能够喜欢并积极参与！', 1, 3, 1),
('2026-02-24 11:00:00', '如何使用VR设备', '详细介绍VR设备的使用方法和注意事项', '使用VR设备时需要注意以下几点：\n\n1. 确保设备电量充足\n2. 佩戴舒适，调整好头带\n3. 保持周围环境安全，有足够的活动空间\n4. 首次使用时建议有人陪同\n\n希望大家能够安全、愉快地使用VR设备！', 2, 1, 1),
('2026-02-24 12:00:00', 'VR教室系统功能介绍', '介绍VR教室系统的主要功能和使用方法', '我们的VR教室系统具有以下功能：\n\n1. 虚拟教室场景\n2. 实时互动交流\n3. 3D教学内容展示\n4. 座位捐赠系统\n\n更多功能正在开发中，敬请期待！', 3, 2, 1);

-- 插入帖子图片数据
INSERT INTO post_images (post_id, images) VALUES
(1, 'https://example.com/images/vr-classroom-1.jpg'),
(1, 'https://example.com/images/vr-classroom-2.jpg'),
(2, 'https://example.com/images/vr-device-1.jpg'),
(3, 'https://example.com/images/vr-features-1.jpg'),
(3, 'https://example.com/images/vr-features-2.jpg');

-- 插入评论数据
INSERT INTO comment (date, content, commenter_id, post_id, status) VALUES
('2026-02-24 10:30:00', '太好了，终于有这样的系统了！', 2, 1, 1),
('2026-02-24 10:45:00', '期待早日体验', 3, 1, 1),
('2026-02-24 11:30:00', '非常详细的介绍，谢谢分享', 1, 2, 1),
('2026-02-24 12:30:00', '功能很强大，希望能够顺利使用', 2, 3, 1);

-- 插入证书数据
INSERT INTO certificate (claim_id, donor_id, certificate_no, certificate_url) VALUES
(1, 1, 'CERT20260224001', 'https://example.com/certificates/CERT20260224001.pdf'),
(2, 2, 'CERT20260224002', 'https://example.com/certificates/CERT20260224002.pdf'),
(3, 3, 'CERT20260224003', 'https://example.com/certificates/CERT20260224003.pdf');

-- 插入捐赠订单数据
INSERT INTO donation_order (donor_id, seat_id, tier_id, amount, message, status, order_no, created_at) VALUES
(1, 1, 1, 100.00, '支持VR教室建设', 1, 'DON20260224001', '2026-02-24 09:00:00'),
(2, 2, 2, 200.00, '为教育事业贡献一份力量', 1, 'DON20260224002', '2026-02-24 10:00:00'),
(3, 3, 3, 500.00, '希望VR教室能够帮助更多学生', 0, 'DON20260224003', '2026-02-24 11:00:00');

-- 插入支付订单数据
INSERT INTO payment_order (user_id, amount, order_no, status, payment_method, product_type, product_id, remark, created_at) VALUES
(1, 100.00, 'PAY20260224001', 2, 'WECHAT', 'DONATION', '1', 'VR教室座位捐赠', '2026-02-24 09:00:00'),
(2, 200.00, 'PAY20260224002', 2, 'ALIPAY', 'DONATION', '2', 'VR教室座位捐赠', '2026-02-24 10:00:00'),
(3, 500.00, 'PAY20260224003', 0, 'WECHAT', 'DONATION', '3', 'VR教室座位捐赠', '2026-02-24 11:00:00');

-- 插入成功提示
SELECT '测试数据插入成功！' AS message;
SELECT CONCAT('插入学院数据: ', COUNT(*), ' 条') AS result FROM college;
SELECT CONCAT('插入分类数据: ', COUNT(*), ' 条') AS result FROM category;
SELECT CONCAT('插入校区数据: ', COUNT(*), ' 条') AS result FROM campus;
SELECT CONCAT('插入建筑数据: ', COUNT(*), ' 条') AS result FROM building;
SELECT CONCAT('插入教室数据: ', COUNT(*), ' 条') AS result FROM classroom;
SELECT CONCAT('插入座位数据: ', COUNT(*), ' 条') AS result FROM seat;
SELECT CONCAT('插入用户数据: ', COUNT(*), ' 条') AS result FROM user;
SELECT CONCAT('插入帖子数据: ', COUNT(*), ' 条') AS result FROM post;
SELECT CONCAT('插入评论数据: ', COUNT(*), ' 条') AS result FROM comment;
SELECT CONCAT('插入证书数据: ', COUNT(*), ' 条') AS result FROM certificate;
SELECT CONCAT('插入捐赠订单数据: ', COUNT(*), ' 条') AS result FROM donation_order;
SELECT CONCAT('插入支付订单数据: ', COUNT(*), ' 条') AS result FROM payment_order;