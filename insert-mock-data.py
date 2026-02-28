#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
插入模拟数据到MySQL数据库
"""

import pymysql
from pymysql import Error

def create_connection():
    """创建数据库连接"""
    try:
        connection = pymysql.connect(
            host='localhost',
            port=3307,
            database='vr_classroom',
            user='root',
            password='password',
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        return connection
    except Error as e:
        print(f"连接数据库时出错: {e}")
        return None

def insert_mock_data():
    """插入模拟数据"""
    connection = create_connection()
    if not connection:
        return

    try:
        cursor = connection.cursor()

        # 禁用外键检查
        cursor.execute("SET FOREIGN_KEY_CHECKS = 0")

        # 清空现有数据
        tables = [
            'certificate', 'payment_order', 'donation_order', 'comment',
            'post_images', 'post', 'seat', 'classroom', 'building',
            'campus', 'user', 'category', 'college'
        ]
        for table in tables:
            cursor.execute(f"DELETE FROM {table}")
            print(f"已清空表: {table}")

        # 启用外键检查
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")

        # 插入学院数据
        colleges = [
            ('计算机科学与技术学院',),
            ('电子工程学院',),
            ('机械工程学院',),
            ('经济管理学院',),
            ('人文学院',),
            ('外国语学院',)
        ]
        cursor.executemany("INSERT INTO college (name) VALUES (%s)", colleges)
        print(f"插入学院数据: {cursor.rowcount} 条")

        # 插入分类数据
        categories = [
            ('技术分享',),
            ('学习交流',),
            ('校园生活',),
            ('活动通知',),
            ('求助问答',)
        ]
        cursor.executemany("INSERT INTO category (name) VALUES (%s)", categories)
        print(f"插入分类数据: {cursor.rowcount} 条")

        # 插入校区数据
        campuses = [
            ('主校区', '学校的主要校区，包含大部分学院和教学楼', 1, True),
            ('东校区', '主要为理工科实验室和教学楼', 2, True),
            ('西校区', '主要为文科和艺术学院', 3, True)
        ]
        cursor.executemany(
            "INSERT INTO campus (name, description, sort_order, active) VALUES (%s, %s, %s, %s)",
            campuses
        )
        print(f"插入校区数据: {cursor.rowcount} 条")

        # 插入建筑数据
        buildings = [
            (1, '第一教学楼', '主要用于本科生教学', 1, True),
            (1, '第二教学楼', '主要用于研究生教学', 2, True),
            (1, '图书馆', '学校的主要图书馆', 3, True),
            (2, '实验楼', '理工科实验室', 1, True),
            (3, '艺术楼', '艺术学院教学楼', 1, True)
        ]
        cursor.executemany(
            "INSERT INTO building (campus_id, name, description, sort_order, active) VALUES (%s, %s, %s, %s, %s)",
            buildings
        )
        print(f"插入建筑数据: {cursor.rowcount} 条")

        # 插入教室数据
        classrooms = [
            (1, '101教室', 1, 50, True),
            (1, '102教室', 1, 50, True),
            (1, '201教室', 2, 100, True),
            (1, '202教室', 2, 100, True),
            (2, '301教室', 3, 80, True),
            (2, '302教室', 3, 80, True),
            (3, '阅览室', 1, 200, True),
            (4, '物理实验室', 1, 30, True),
            (5, '美术教室', 1, 40, True)
        ]
        cursor.executemany(
            "INSERT INTO classroom (building_id, name, floor, seat_count, active) VALUES (%s, %s, %s, %s, %s)",
            classrooms
        )
        print(f"插入教室数据: {cursor.rowcount} 条")

        # 插入座位数据（为101教室创建座位）
        seats = []
        for row in range(1, 11):
            for col in range(1, 6):
                seats.append((1, str(row), str(col), 0))
        cursor.executemany(
            "INSERT INTO seat (classroom_id, seat_row, seat_column, status) VALUES (%s, %s, %s, %s)",
            seats
        )
        print(f"插入座位数据: {cursor.rowcount} 条")

        # 插入用户数据
        users = [
            ('13800138001', 'openid_001', '张三', 'https://example.com/avatar1.jpg', 1, 0),
            ('13800138002', 'openid_002', '李四', 'https://example.com/avatar2.jpg', 2, 1),
            ('13800138003', 'openid_003', '王五', 'https://example.com/avatar3.jpg', 3, 2)
        ]
        cursor.executemany(
            "INSERT INTO user (phone, open_id, name, avatar, college_id, verify_status) VALUES (%s, %s, %s, %s, %s, %s)",
            users
        )
        print(f"插入用户数据: {cursor.rowcount} 条")

        # 插入帖子数据
        posts = [
            ('2026-02-24 10:00:00', '欢迎使用VR教室系统', '这是一个全新的VR教室系统，为大家提供沉浸式的学习体验',
             '大家好，欢迎使用我们的VR教室系统！\n\n这个系统可以让你在虚拟环境中体验真实的教室场景，与其他同学互动交流。\n\n希望大家能够喜欢并积极参与！',
             1, 3, 1),
            ('2026-02-24 11:00:00', '如何使用VR设备', '详细介绍VR设备的使用方法和注意事项',
             '使用VR设备时需要注意以下几点：\n\n1. 确保设备电量充足\n2. 佩戴舒适，调整好头带\n3. 保持周围环境安全，有足够的活动空间\n4. 首次使用时建议有人陪同\n\n希望大家能够安全、愉快地使用VR设备！',
             2, 1, 1),
            ('2026-02-24 12:00:00', 'VR教室系统功能介绍', '介绍VR教室系统的主要功能和使用方法',
             '我们的VR教室系统具有以下功能：\n\n1. 虚拟教室场景\n2. 实时互动交流\n3. 3D教学内容展示\n4. 座位捐赠系统\n\n更多功能正在开发中，敬请期待！',
             3, 2, 1)
        ]
        cursor.executemany(
            "INSERT INTO post (date, title, summary, content, author_id, category_id, status) VALUES (%s, %s, %s, %s, %s, %s, %s)",
            posts
        )
        print(f"插入帖子数据: {cursor.rowcount} 条")

        # 插入帖子图片数据
        post_images = [
            (1, 'https://example.com/images/vr-classroom-1.jpg'),
            (1, 'https://example.com/images/vr-classroom-2.jpg'),
            (2, 'https://example.com/images/vr-device-1.jpg'),
            (3, 'https://example.com/images/vr-features-1.jpg'),
            (3, 'https://example.com/images/vr-features-2.jpg')
        ]
        cursor.executemany(
            "INSERT INTO post_images (post_id, images) VALUES (%s, %s)",
            post_images
        )
        print(f"插入帖子图片数据: {cursor.rowcount} 条")

        # 插入评论数据
        comments = [
            ('2026-02-24 10:30:00', '太好了，终于有这样的系统了！', 2, 1, 1),
            ('2026-02-24 10:45:00', '期待早日体验', 3, 1, 1),
            ('2026-02-24 11:30:00', '非常详细的介绍，谢谢分享', 1, 2, 1),
            ('2026-02-24 12:30:00', '功能很强大，希望能够顺利使用', 2, 3, 1)
        ]
        cursor.executemany(
            "INSERT INTO comment (date, content, commenter_id, post_id, status) VALUES (%s, %s, %s, %s, %s)",
            comments
        )
        print(f"插入评论数据: {cursor.rowcount} 条")

        # 提交事务
        connection.commit()
        print("\n✅ 所有模拟数据插入成功！")

    except Error as e:
        print(f"插入数据时出错: {e}")
        connection.rollback()
    finally:
        if connection:
            cursor.close()
            connection.close()
            print("数据库连接已关闭")

if __name__ == "__main__":
    insert_mock_data()
