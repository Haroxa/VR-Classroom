-- Active: 1771947726816@@192.168.247.150@3306@cj_vr

DROP DATABASE cj_vr;

CREATE DATABASE  cj_vr CHARACTER SET gbk COLLATE gbk_chinese_ci;

USE cj_vr;

ALTER TABLE `seat` ADD COLUMN `price` int NOT NULL DEFAULT 100000 COMMENT '价格，单位分';

ALTER TABLE `order` 
ADD COLUMN `campusId` int NOT NULL COMMENT '校区id',
ADD COLUMN `buildingId` int NOT NULL COMMENT '楼栋id',
ADD COLUMN `roomId` int NOT NULL COMMENT '教室id',
ADD KEY `fk_order_room` (`roomId`),
ADD CONSTRAINT `fk_order_room` FOREIGN KEY (`roomId`) REFERENCES `room` (`id`) ON DELETE CASCADE;





