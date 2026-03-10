-- Active: 1771720784163@@127.0.0.1@3306@vr_classroom
-- Active: 1772420486203@@10.86.136.242@3307@vr_classroom
DROP DATABASE IF EXISTS vr_classroom;

CREATE DATABASE vr_classroom CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE vr_classroom;

-- 查看所有表，及其注释
SELECT 
    table_name AS '表名',
    table_comment AS '表注释'
FROM 
    information_schema.tables
WHERE 
    table_schema = 'vr_classroom'
    AND table_type = 'BASE TABLE';

building	楼栋信息表
campus	校区信息表
category	分类信息表
college	学院信息表
comment	评论表
comment_like	评论点赞表
order	订单记录表
order_seat	订单与座位的关联信息表
post	帖子表
post_like	帖子点赞表
room	教室信息表
seat	教室座位布局表
user	用户信息表

-- 生成真实合理的相关数据
-- 内置数据模块：用户信息表、分类信息表、校区信息表、建筑信息表、教室信息表、座位信息表
building	楼栋信息表
campus	校区信息表
category	分类信息表
college	学院信息表
room	教室信息表
seat	教室座位布局表
user	用户信息表
-- 模拟数据模块：订单信息表、订单座位关联表、评论表、点赞表、帖子表、帖子点赞表
comment	评论表
comment_like	评论点赞表
order	订单记录表
order_seat	订单与座位的关联信息表
post	帖子表
post_like	帖子点赞表

