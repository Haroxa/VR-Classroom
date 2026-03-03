-- ============================================
-- 数据库升级脚本 v4.1
-- 从 v3.0 升级到 v4.1
-- 执行前请备份数据！
-- ============================================

USE vr_classroom;

DELIMITER //

CREATE PROCEDURE update_schema_v4_1()
BEGIN
    -- ============================================
    -- 1. Order 表新增字段
    -- ============================================
    -- 备份原数据
    CREATE TABLE IF NOT EXISTS `order_backup` AS SELECT * FROM `order`;
    
    -- 检查并添加 campus_id 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND column_name = 'campus_id'
    ) THEN
        ALTER TABLE `order` ADD COLUMN `campus_id` INTEGER COMMENT '校区ID' AFTER `user_id`;
    END IF;
    
    -- 检查并添加 building_id 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND column_name = 'building_id'
    ) THEN
        ALTER TABLE `order` ADD COLUMN `building_id` INTEGER COMMENT '教学楼ID' AFTER `campus_id`;
    END IF;
    
    -- 检查并添加 room_id 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND column_name = 'room_id'
    ) THEN
        ALTER TABLE `order` ADD COLUMN `room_id` INTEGER COMMENT '教室ID' AFTER `building_id`;
    END IF;
    
    -- 添加索引
    -- 检查并添加 idx_campus_id 索引
    IF NOT EXISTS (
        SELECT * FROM information_schema.statistics 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND index_name = 'idx_campus_id'
    ) THEN
        ALTER TABLE `order` ADD INDEX idx_campus_id (campus_id);
    END IF;
    
    -- 检查并添加 idx_building_id 索引
    IF NOT EXISTS (
        SELECT * FROM information_schema.statistics 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND index_name = 'idx_building_id'
    ) THEN
        ALTER TABLE `order` ADD INDEX idx_building_id (building_id);
    END IF;
    
    -- 检查并添加 idx_room_id 索引
    IF NOT EXISTS (
        SELECT * FROM information_schema.statistics 
        WHERE table_schema = 'vr_classroom' AND table_name = 'order' AND index_name = 'idx_room_id'
    ) THEN
        ALTER TABLE `order` ADD INDEX idx_room_id (room_id);
    END IF;
    
    -- 数据迁移（从seat表反推room_id）
    -- 注意：这只是一个示例，实际数据迁移需要根据业务逻辑调整
    UPDATE `order` o 
    JOIN order_seat os ON o.id = os.order_id 
    JOIN seat s ON os.seat_id = s.id 
    SET o.room_id = s.room_id
    WHERE o.room_id IS NULL;
    
    -- ============================================
    -- 2. Seat 表新增 price 字段
    -- ============================================
    -- 备份原数据
    CREATE TABLE IF NOT EXISTS `seat_backup` AS SELECT * FROM `seat`;
    
    -- 检查并添加 price 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_schema = 'vr_classroom' AND table_name = 'seat' AND column_name = 'price'
    ) THEN
        ALTER TABLE `seat` ADD COLUMN `price` INTEGER NOT NULL DEFAULT 10000 COMMENT '座位价格（分）' AFTER `col`;
    END IF;
    
    -- 更新现有数据（根据排数设置不同价格）
    -- 前3排 100元
    UPDATE `seat` SET price = 10000 WHERE `row` <= 3 AND price = 10000;
    -- 4-6排 80元
    UPDATE `seat` SET price = 8000 WHERE `row` > 3 AND `row` <= 6 AND price = 10000;
    -- 7-10排 50元
    UPDATE `seat` SET price = 5000 WHERE `row` > 6 AND price = 10000;
    
    -- ============================================
    -- 3. User 表认证状态字段说明更新
    -- ============================================
    -- 更新字段注释
    ALTER TABLE `user` 
      MODIFY COLUMN verify_status TINYINT DEFAULT 0 COMMENT '认证状态：0-未认证，1-审核中，2-已认证';
    
    -- ============================================
    -- 4. 验证升级结果
    -- ============================================
    SELECT 'Order表结构' AS check_item;
    DESCRIBE `order`;
    
    SELECT 'Seat表结构' AS check_item;
    DESCRIBE `seat`;
    
    SELECT 'User表结构' AS check_item;
    DESCRIBE `user`;
    
    -- ============================================
    -- 5. 清理备份表（确认无误后执行）
    -- ============================================
    -- DROP TABLE IF EXISTS `order_backup`;
    -- DROP TABLE IF EXISTS `seat_backup`;
    
    SELECT '数据库升级完成！' AS message;
END //

DELIMITER ;

-- 执行存储过程
CALL update_schema_v4_1();

-- 删除存储过程
DROP PROCEDURE IF EXISTS update_schema_v4_1;