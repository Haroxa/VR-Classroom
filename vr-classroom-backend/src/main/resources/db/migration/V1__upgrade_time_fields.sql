-- 数据库时间字段升级迁移脚本
-- 将VARCHAR类型的date字段改为DATETIME类型，并添加created_at和updated_at字段
USE vr_classroom;
-- 1. 备份现有数据（可选，建议在生产环境执行前备份）
-- CREATE TABLE post_backup AS SELECT * FROM post;
-- CREATE TABLE comment_backup AS SELECT * FROM comment;

-- 2. 为post表添加created_at和updated_at字段
ALTER TABLE post 
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 3. 为comment表添加created_at和updated_at字段
ALTER TABLE comment 
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 4. 迁移post表中的date数据到created_at字段
UPDATE post 
SET created_at = STR_TO_DATE(date, '%Y-%m-%d %H:%i:%s')
WHERE date IS NOT NULL AND date != '';

-- 5. 迁移comment表中的date数据到created_at字段
UPDATE comment 
SET created_at = STR_TO_DATE(date, '%Y-%m-%d %H:%i:%s')
WHERE date IS NOT NULL AND date != '';

-- 6. 设置created_at和updated_at为NOT NULL（确保数据迁移成功后再执行）
ALTER TABLE post 
MODIFY COLUMN created_at DATETIME NOT NULL,
MODIFY COLUMN updated_at DATETIME NOT NULL;

ALTER TABLE comment 
MODIFY COLUMN created_at DATETIME NOT NULL,
MODIFY COLUMN updated_at DATETIME NOT NULL;

-- 7. 删除旧的date字段（可选，建议在确认新字段工作正常后再执行）
-- ALTER TABLE post DROP COLUMN date;
-- ALTER TABLE comment DROP COLUMN date;

-- 索引部分暂时不考虑
-- -- 8. 添加索引以优化查询性能
-- CREATE INDEX idx_post_created_at ON post(created_at DESC);
-- CREATE INDEX idx_comment_created_at ON comment(created_at DESC);

-- -- 9. 添加索引以优化查询性能（按状态和创建时间查询）
-- CREATE INDEX idx_post_status_created_at ON post(status, created_at DESC);
-- CREATE INDEX idx_comment_status_created_at ON comment(status, created_at DESC);

-- -- 10. 添加索引以优化查询性能（按作者和创建时间查询）
-- CREATE INDEX idx_post_author_created_at ON post(authorId, created_at DESC);
-- CREATE INDEX idx_comment_commenter_created_at ON comment(commenterId, created_at DESC);

-- -- 11. 添加索引以优化查询性能（按分类和创建时间查询）
-- CREATE INDEX idx_post_category_created_at ON post(categoryId, created_at DESC);

-- -- 12. 添加索引以优化查询性能（按帖子和创建时间查询）
-- CREATE INDEX idx_comment_post_created_at ON comment(postId, created_at DESC);
