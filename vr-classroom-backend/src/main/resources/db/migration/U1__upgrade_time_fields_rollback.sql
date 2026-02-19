-- 数据库时间字段升级回滚脚本
-- 将DATETIME类型的created_at和updated_at字段改回VARCHAR类型的date字段

-- 1. 添加回旧的date字段
ALTER TABLE post 
ADD COLUMN date VARCHAR(20);

ALTER TABLE comment 
ADD COLUMN date VARCHAR(20);

-- 2. 迁移created_at数据到date字段
UPDATE post 
SET date = DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s')
WHERE created_at IS NOT NULL;

UPDATE comment 
SET date = DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s')
WHERE created_at IS NOT NULL;

-- 3. 设置date为NOT NULL
ALTER TABLE post 
MODIFY COLUMN date VARCHAR(20) NOT NULL;

ALTER TABLE comment 
MODIFY COLUMN date VARCHAR(20) NOT NULL;

-- 4. 删除created_at和updated_at字段
ALTER TABLE post 
DROP COLUMN created_at,
DROP COLUMN updated_at;

ALTER TABLE comment 
DROP COLUMN created_at,
DROP COLUMN updated_at;

-- 5. 删除添加的索引
DROP INDEX idx_post_created_at ON post;
DROP INDEX idx_comment_created_at ON comment;
DROP INDEX idx_post_status_created_at ON post;
DROP INDEX idx_comment_status_created_at ON comment;
DROP INDEX idx_post_author_created_at ON post;
DROP INDEX idx_comment_commenter_created_at ON comment;
DROP INDEX idx_post_category_created_at ON post;
DROP INDEX idx_comment_post_created_at ON comment;
