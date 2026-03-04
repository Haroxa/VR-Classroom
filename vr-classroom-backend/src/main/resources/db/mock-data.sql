-- Active: 1771720784163@@127.0.0.1@3306@vr_classroom
-- Active: 1772420486203@@10.86.136.242@3307@vr_classroom
-- 测试数据初始化脚本
-- 用于开发和测试环境的数据插入
-- 请确保在执行此脚本前已创建所有表结构

-- 使用vr_classroom数据库
USE vr_classroom;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据（如果存在）
DELETE FROM certificate;
DELETE FROM payment_order;
DELETE FROM order_seat;
DELETE FROM `order`;
DELETE FROM donation_order;
DELETE FROM seat;
DELETE FROM classroom;
DELETE FROM building;
DELETE FROM campus;
DELETE FROM comment;
DELETE FROM post_images;
DELETE FROM post;
DELETE FROM user;
DELETE FROM category;
DELETE FROM college;

-- 重置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 学院表 (college)
-- ============================================
INSERT INTO college (name) VALUES
('计算机科学与技术学院'),
('电子工程学院'),
('机械工程学院'),
('经济管理学院'),
('人文学院'),
('外国语学院');

-- ============================================
-- 2. 分类表 (category)
-- ============================================
INSERT INTO category (name) VALUES
('技术分享'),
('学习交流'),
('校园生活'),
('活动通知'),
('求助问答');

-- ============================================
-- 3. 用户表 (user)
-- verify_status: 0-未认证, 1-认证中, 2-已认证
-- ============================================
INSERT INTO user (phone, open_id, name, avatar, college_id, verify_status) VALUES
('13800138001', 'openid_001', '张三', 'https://example.com/avatar1.jpg', '1', 2),
('13800138002', 'openid_002', '李四', 'https://example.com/avatar2.jpg', '2', 2),
('13800138003', 'openid_003', '王五', 'https://example.com/avatar3.jpg', '3', 2),
('13800138004', 'openid_004', '赵六', 'https://example.com/avatar4.jpg', '1', 0),
('13800138005', 'openid_005', '钱七', 'https://example.com/avatar5.jpg', '2', 1);

-- ============================================
-- 4. 帖子表 (post)
-- status: 0-待审核, 1-已发布, 2-已拒绝
-- ============================================
INSERT INTO post (created_at, updated_at, date, title, summary, content, author_id, category_id, like_count, share_count, comment_count, status) VALUES
('2026-02-24 10:00:00', '2026-02-24 10:00:00', '2026-02-24', '欢迎使用VR教室系统', '这是一个全新的VR教室系统，为大家提供沉浸式的学习体验', '大家好，欢迎使用我们的VR教室系统！\n\n这个系统可以让你在虚拟环境中体验真实的教室场景，与其他同学互动交流。\n\n希望大家能够喜欢并积极参与！', 1, 3, 15, 5, 3, 1),
('2026-02-24 11:00:00', '2026-02-24 11:00:00', '2026-02-24', '如何使用VR设备', '详细介绍VR设备的使用方法和注意事项', '使用VR设备时需要注意以下几点：\n\n1. 确保设备电量充足\n2. 佩戴舒适，调整好头带\n3. 保持周围环境安全，有足够的活动空间\n4. 首次使用时建议有人陪同\n\n希望大家能够安全、愉快地使用VR设备！', 2, 1, 28, 10, 2, 1),
('2026-02-24 12:00:00', '2026-02-24 12:00:00', '2026-02-24', 'VR教室系统功能介绍', '介绍VR教室系统的主要功能和使用方法', '我们的VR教室系统具有以下功能：\n\n1. 虚拟教室场景\n2. 实时互动交流\n3. 3D教学内容展示\n4. 座位捐赠系统\n\n更多功能正在开发中，敬请期待！', 3, 2, 42, 18, 4, 1),
('2026-02-25 09:00:00', '2026-02-25 09:00:00', '2026-02-25', 'VR教室捐赠活动开始啦', '参与座位捐赠，共建智慧校园', '亲爱的同学们，VR教室座位捐赠活动正式开始！\n\n通过捐赠座位，您可以：\n- 获得专属座位命名权\n- 获得电子捐赠证书\n- 支持学校智慧教育发展\n\n欢迎广大师生积极参与！', 1, 4, 56, 25, 6, 1),
('2026-02-25 14:00:00', '2026-02-25 14:00:00', '2026-02-25', 'VR设备使用常见问题解答', '汇总VR设备使用过程中的常见问题及解决方案', 'Q: VR设备如何充电？\nA: 使用配套的USB-C充电线，充电时间约2-3小时。\n\nQ: 佩戴VR设备时感到头晕怎么办？\nA: 建议初次使用时间不超过15分钟，逐渐适应后可延长时间。\n\nQ: 如何清洁VR设备？\nA: 使用专用清洁布轻轻擦拭镜片，避免使用化学清洁剂。', 2, 5, 33, 12, 3, 1);

-- ============================================
-- 5. 帖子图片表 (post_images)
-- ============================================
INSERT INTO post_images (post_id, images) VALUES
(1, 'https://example.com/images/vr-classroom-1.jpg'),
(1, 'https://example.com/images/vr-classroom-2.jpg'),
(2, 'https://example.com/images/vr-device-1.jpg'),
(2, 'https://example.com/images/vr-device-2.jpg'),
(3, 'https://example.com/images/vr-features-1.jpg'),
(3, 'https://example.com/images/vr-features-2.jpg'),
(3, 'https://example.com/images/vr-features-3.jpg'),
(4, 'https://example.com/images/donation-event-1.jpg'),
(4, 'https://example.com/images/donation-event-2.jpg'),
(5, 'https://example.com/images/faq-1.jpg');

-- ============================================
-- 6. 评论表 (comment)
-- status: 0-待审核, 1-已发布, 2-已拒绝
-- ============================================
INSERT INTO comment (created_at, updated_at, date, content, commenter_id, post_id, like_count, status) VALUES
('2026-02-24 10:30:00', '2026-02-24 10:30:00', '2026-02-24', '太好了，终于有这样的系统了！', 2, 1, 5, 1),
('2026-02-24 10:45:00', '2026-02-24 10:45:00', '2026-02-24', '期待早日体验', 3, 1, 3, 1),
('2026-02-24 11:30:00', '2026-02-24 11:30:00', '2026-02-24', '非常详细的介绍，谢谢分享', 1, 2, 8, 1),
('2026-02-24 12:30:00', '2026-02-24 12:30:00', '2026-02-24', '功能很强大，希望能够顺利使用', 2, 3, 6, 1),
('2026-02-25 10:00:00', '2026-02-25 10:00:00', '2026-02-25', '我已经报名参加了，期待拿到证书！', 3, 4, 12, 1),
('2026-02-25 15:00:00', '2026-02-25 15:00:00', '2026-02-25', '这些解答很有帮助，谢谢！', 1, 5, 4, 1);

-- ============================================
-- 7. 校区表 (campus)
-- ============================================
INSERT INTO campus (name, description, sort_order, active) VALUES
('主校区', '学校的主要校区，包含大部分学院和教学楼，是学校的行政中心', 1, TRUE),
('东校区', '主要为理工科实验室和教学楼，配备先进的实验设备', 2, TRUE),
('西校区', '主要为文科和艺术学院，环境优美，适合人文艺术学习', 3, TRUE);

-- ============================================
-- 8. 建筑表 (building)
-- ============================================
INSERT INTO building (campus_id, name, description, vr_model_url, sort_order, active) VALUES
(1, '第一教学楼', '主要用于本科生教学，配备多媒体教室和VR体验室', 'https://vr.example.com/models/building1.glb', 1, TRUE),
(1, '第二教学楼', '主要用于研究生教学，设有研讨室和实验室', 'https://vr.example.com/models/building2.glb', 2, TRUE),
(1, '图书馆', '学校的主要图书馆，藏书丰富，设有自习室和电子阅览室', 'https://vr.example.com/models/library.glb', 3, TRUE),
(2, '实验楼', '理工科实验室，配备先进的实验设备', 'https://vr.example.com/models/lab.glb', 1, TRUE),
(2, '工程中心', '工程实践基地，支持学生创新项目', 'https://vr.example.com/models/engineering.glb', 2, TRUE),
(3, '艺术楼', '艺术学院教学楼，设有画室、音乐室等', 'https://vr.example.com/models/art.glb', 1, TRUE),
(3, '文科楼', '人文社科类教学场所', 'https://vr.example.com/models/humanities.glb', 2, TRUE);

-- ============================================
-- 9. 教室表 (classroom)
-- ============================================
INSERT INTO classroom (building_id, room_number, name, floor, description, vr_model_url, total_rows, total_cols, seat_count, claimed_count, active) VALUES
(1, '101', '101教室', 1, '标准多媒体教室，支持VR教学', 'https://vr.example.com/models/room101.glb', 10, 5, 50, 5, TRUE),
(1, '102', '102教室', 1, '标准多媒体教室', 'https://vr.example.com/models/room102.glb', 10, 5, 50, 3, TRUE),
(1, '201', '201教室', 2, '大型阶梯教室', 'https://vr.example.com/models/room201.glb', 10, 10, 100, 12, TRUE),
(1, '202', '202教室', 2, '大型阶梯教室', 'https://vr.example.com/models/room202.glb', 10, 10, 100, 8, TRUE),
(2, '301', '301研讨室', 3, '小型研讨室，适合小组讨论', 'https://vr.example.com/models/room301.glb', 8, 10, 80, 6, TRUE),
(2, '302', '302研讨室', 3, '小型研讨室', 'https://vr.example.com/models/room302.glb', 8, 10, 80, 4, TRUE),
(3, 'R001', '阅览室', 1, '图书馆主阅览室，环境安静', 'https://vr.example.com/models/reading1.glb', 20, 10, 200, 25, TRUE),
(3, 'R002', '电子阅览室', 1, '配备电脑的电子阅览室', 'https://vr.example.com/models/reading2.glb', 15, 8, 120, 15, TRUE),
(4, 'L001', '物理实验室', 1, '物理实验专用教室', 'https://vr.example.com/models/lab_physics.glb', 5, 6, 30, 2, TRUE),
(4, 'L002', '化学实验室', 1, '化学实验专用教室', 'https://vr.example.com/models/lab_chemistry.glb', 5, 6, 30, 1, TRUE),
(5, 'E001', '工程实训室', 1, '工程实践训练室', 'https://vr.example.com/models/engineering1.glb', 6, 8, 48, 4, TRUE),
(6, 'A001', '美术教室', 1, '美术专业教室', 'https://vr.example.com/models/art1.glb', 5, 8, 40, 3, TRUE),
(6, 'A002', '音乐教室', 1, '音乐专业教室', 'https://vr.example.com/models/music1.glb', 4, 6, 24, 2, TRUE),
(7, 'H001', '语言教室', 1, '外语学习专用教室', 'https://vr.example.com/models/language1.glb', 6, 8, 48, 5, TRUE);

-- ============================================
-- 10. 座位表 (seat)
-- status: 0-过道/不可用, 1-可选, 2-锁定, 3-已购买
-- ============================================
-- 为101教室创建座位 (10行 x 5列)
-- price: 前3排100元, 4-6排80元, 7-10排50元
-- 布局说明: 第1排中间(1,3)设为过道, 部分座位已购买(status=3), 其余可选(status=1)
INSERT INTO seat (room_id, `row`, `col`, price, status, version, donor_id, claimed_at) VALUES
(1, 1, 1, 10000, 3, 1, 1, '2026-02-24 09:00:00'),
(1, 1, 2, 10000, 3, 1, 2, '2026-02-24 10:00:00'),
(1, 1, 3, 10000, 0, 0, NULL, NULL),
(1, 1, 4, 10000, 3, 1, 3, '2026-02-24 11:00:00'),
(1, 1, 5, 10000, 3, 1, 1, '2026-02-24 12:00:00'),
(1, 2, 1, 10000, 1, 0, NULL, NULL),
(1, 2, 2, 10000, 1, 0, NULL, NULL),
(1, 2, 3, 10000, 1, 0, NULL, NULL),
(1, 2, 4, 10000, 1, 0, NULL, NULL),
(1, 2, 5, 10000, 1, 0, NULL, NULL),
(1, 3, 1, 10000, 1, 0, NULL, NULL),
(1, 3, 2, 10000, 1, 0, NULL, NULL),
(1, 3, 3, 10000, 1, 0, NULL, NULL),
(1, 3, 4, 10000, 1, 0, NULL, NULL),
(1, 3, 5, 10000, 1, 0, NULL, NULL),
(1, 4, 1, 8000, 1, 0, NULL, NULL),
(1, 4, 2, 8000, 1, 0, NULL, NULL),
(1, 4, 3, 8000, 1, 0, NULL, NULL),
(1, 4, 4, 8000, 1, 0, NULL, NULL),
(1, 4, 5, 8000, 1, 0, NULL, NULL),
(1, 5, 1, 8000, 1, 0, NULL, NULL),
(1, 5, 2, 8000, 1, 0, NULL, NULL),
(1, 5, 3, 8000, 1, 0, NULL, NULL),
(1, 5, 4, 8000, 1, 0, NULL, NULL),
(1, 5, 5, 8000, 1, 0, NULL, NULL),
(1, 6, 1, 8000, 1, 0, NULL, NULL),
(1, 6, 2, 8000, 1, 0, NULL, NULL),
(1, 6, 3, 8000, 1, 0, NULL, NULL),
(1, 6, 4, 8000, 1, 0, NULL, NULL),
(1, 6, 5, 8000, 1, 0, NULL, NULL),
(1, 7, 1, 5000, 1, 0, NULL, NULL),
(1, 7, 2, 5000, 1, 0, NULL, NULL),
(1, 7, 3, 5000, 1, 0, NULL, NULL),
(1, 7, 4, 5000, 1, 0, NULL, NULL),
(1, 7, 5, 5000, 1, 0, NULL, NULL),
(1, 8, 1, 5000, 1, 0, NULL, NULL),
(1, 8, 2, 5000, 1, 0, NULL, NULL),
(1, 8, 3, 5000, 1, 0, NULL, NULL),
(1, 8, 4, 5000, 1, 0, NULL, NULL),
(1, 8, 5, 5000, 1, 0, NULL, NULL),
(1, 9, 1, 5000, 1, 0, NULL, NULL),
(1, 9, 2, 5000, 1, 0, NULL, NULL),
(1, 9, 3, 5000, 1, 0, NULL, NULL),
(1, 9, 4, 5000, 1, 0, NULL, NULL),
(1, 9, 5, 5000, 1, 0, NULL, NULL),
(1, 10, 1, 5000, 1, 0, NULL, NULL),
(1, 10, 2, 5000, 1, 0, NULL, NULL),
(1, 10, 3, 5000, 1, 0, NULL, NULL),
(1, 10, 4, 5000, 1, 0, NULL, NULL),
(1, 10, 5, 5000, 1, 0, NULL, NULL);

-- 为102教室创建部分座位 (10行 x 5列，部分已售)
INSERT INTO seat (room_id, `row`, `col`, price, status, version, donor_id, claimed_at) VALUES
(2, 1, 1, 10000, 1, 0, 1, '2026-02-25 09:00:00'),
(2, 1, 2, 10000, 1, 0, 2, '2026-02-25 10:00:00'),
(2, 1, 3, 10000, 1, 0, 3, '2026-02-25 11:00:00'),
(2, 2, 1, 10000, 0, 0, NULL, NULL),
(2, 2, 2, 10000, 0, 0, NULL, NULL),
(2, 2, 3, 10000, 0, 0, NULL, NULL),
(2, 3, 1, 10000, 0, 0, NULL, NULL),
(2, 3, 2, 10000, 0, 0, NULL, NULL),
(2, 3, 3, 10000, 0, 0, NULL, NULL);

-- ============================================
-- 11. 订单表 (order)
-- status: PENDING-待支付, PAID-已支付, CANCELLED-已取消, REFUNDED-已退款
-- ============================================
INSERT INTO `order` (user_id, amount, status, expires_at, created_at, updated_at) VALUES
(1, 10000, 'PAID', '2026-02-24 09:10:00', '2026-02-24 09:00:00', '2026-02-24 09:05:00'),
(2, 10000, 'PAID', '2026-02-24 10:10:00', '2026-02-24 10:00:00', '2026-02-24 10:03:00'),
(3, 10000, 'PAID', '2026-02-24 11:10:00', '2026-02-24 11:00:00', '2026-02-24 11:02:00'),
(1, 10000, 'PAID', '2026-02-24 12:10:00', '2026-02-24 12:00:00', '2026-02-24 12:04:00'),
(4, 20000, 'PENDING', '2026-02-25 10:10:00', '2026-02-25 10:00:00', '2026-02-25 10:00:00'),
(5, 15000, 'CANCELLED', '2026-02-25 11:10:00', '2026-02-25 11:00:00', '2026-02-25 11:05:00');

-- ============================================
-- 12. 订单座位关联表 (order_seat)
-- ============================================
INSERT INTO order_seat (order_id, seat_id) VALUES
(1, 1),
(2, 2),
(3, 4),
(4, 5),
(5, 6),
(5, 7),
(6, 8);

-- ============================================
-- 13. 捐赠订单表 (donation_order)
-- status: 0-待支付, 1-已支付, 2-已完成, 3-已取消, 4-失败
-- ============================================
INSERT INTO donation_order (donor_id, seat_id, tier_id, amount, message, status, order_no, created_at, paid_at, completed_at) VALUES
(1, 1, 1, 100.00, '支持VR教室建设', 2, 'DON20260224001', '2026-02-24 09:00:00', '2026-02-24 09:05:00', '2026-02-24 09:10:00'),
(2, 2, 2, 200.00, '为教育事业贡献一份力量', 2, 'DON20260224002', '2026-02-24 10:00:00', '2026-02-24 10:03:00', '2026-02-24 10:08:00'),
(3, 4, 3, 500.00, '希望VR教室能够帮助更多学生', 2, 'DON20260224003', '2026-02-24 11:00:00', '2026-02-24 11:02:00', '2026-02-24 11:07:00'),
(1, 5, 1, 100.00, '第二次捐赠', 2, 'DON20260224004', '2026-02-24 12:00:00', '2026-02-24 12:04:00', '2026-02-24 12:09:00'),
(4, 6, 2, 200.00, '期待VR体验', 0, 'DON20260225001', '2026-02-25 10:00:00', NULL, NULL),
(5, 8, 1, 100.00, '支持教育', 3, 'DON20260225002', '2026-02-25 11:00:00', NULL, NULL);

-- ============================================
-- 14. 支付订单表 (payment_order)
-- status: 0-待支付, 1-支付中, 2-已支付, 3-已完成, 4-已取消, 5-失败
-- ============================================
INSERT INTO payment_order (user_id, amount, order_no, status, payment_method, transaction_id, product_type, product_id, remark, created_at, paid_at, completed_at) VALUES
(1, 100.00, 'PAY20260224001', 3, 'WECHAT', 'WX202602240001', 'DONATION', '1', 'VR教室座位捐赠', '2026-02-24 09:00:00', '2026-02-24 09:05:00', '2026-02-24 09:10:00'),
(2, 200.00, 'PAY20260224002', 3, 'ALIPAY', 'ALI202602240001', 'DONATION', '2', 'VR教室座位捐赠', '2026-02-24 10:00:00', '2026-02-24 10:03:00', '2026-02-24 10:08:00'),
(3, 500.00, 'PAY20260224003', 3, 'WECHAT', 'WX202602240002', 'DONATION', '3', 'VR教室座位捐赠', '2026-02-24 11:00:00', '2026-02-24 11:02:00', '2026-02-24 11:07:00'),
(1, 100.00, 'PAY20260224004', 3, 'ALIPAY', 'ALI202602240002', 'DONATION', '4', 'VR教室座位捐赠', '2026-02-24 12:00:00', '2026-02-24 12:04:00', '2026-02-24 12:09:00'),
(4, 200.00, 'PAY20260225001', 0, 'WECHAT', NULL, 'DONATION', '5', 'VR教室座位捐赠', '2026-02-25 10:00:00', NULL, NULL),
(5, 100.00, 'PAY20260225002', 4, 'ALIPAY', NULL, 'DONATION', '6', 'VR教室座位捐赠', '2026-02-25 11:00:00', NULL, NULL);

-- ============================================
-- 15. 证书表 (certificate)
-- ============================================
INSERT INTO certificate (claim_id, donor_id, certificate_no, certificate_url, created_at) VALUES
(1, 1, 'CERT20260224001', 'https://example.com/certificates/CERT20260224001.pdf', '2026-02-24 09:10:00'),
(2, 2, 'CERT20260224002', 'https://example.com/certificates/CERT20260224002.pdf', '2026-02-24 10:08:00'),
(3, 3, 'CERT20260224003', 'https://example.com/certificates/CERT20260224003.pdf', '2026-02-24 11:07:00'),
(4, 1, 'CERT20260224004', 'https://example.com/certificates/CERT20260224004.pdf', '2026-02-24 12:09:00');

-- ============================================
-- 验证数据插入结果
-- ============================================
SELECT '测试数据插入成功！' AS message;
SELECT CONCAT('学院数据: ', COUNT(*), ' 条') AS result FROM college;
SELECT CONCAT('分类数据: ', COUNT(*), ' 条') AS result FROM category;
SELECT CONCAT('用户数据: ', COUNT(*), ' 条') AS result FROM user;
SELECT CONCAT('帖子数据: ', COUNT(*), ' 条') AS result FROM post;
SELECT CONCAT('帖子图片数据: ', COUNT(*), ' 条') AS result FROM post_images;
SELECT CONCAT('评论数据: ', COUNT(*), ' 条') AS result FROM comment;
SELECT CONCAT('校区数据: ', COUNT(*), ' 条') AS result FROM campus;
SELECT CONCAT('建筑数据: ', COUNT(*), ' 条') AS result FROM building;
SELECT CONCAT('教室数据: ', COUNT(*), ' 条') AS result FROM classroom;
SELECT CONCAT('座位数据: ', COUNT(*), ' 条') AS result FROM seat;
SELECT CONCAT('订单数据: ', COUNT(*), ' 条') AS result FROM `order`;
SELECT CONCAT('订单座位数据: ', COUNT(*), ' 条') AS result FROM order_seat;
SELECT CONCAT('捐赠订单数据: ', COUNT(*), ' 条') AS result FROM donation_order;
SELECT CONCAT('支付订单数据: ', COUNT(*), ' 条') AS result FROM payment_order;
SELECT CONCAT('证书数据: ', COUNT(*), ' 条') AS result FROM certificate;
