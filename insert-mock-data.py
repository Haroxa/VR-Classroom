#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
插入模拟数据到MySQL数据库
根据 schema.sql 中的所有表结构生成完整的测试数据
"""

import pymysql
from pymysql import Error

is_server = True
host = '10.86.136.242' if is_server else 'localhost'
port = 3307 if is_server else 3306
password = 'password' if is_server else '123456'

def create_connection():
    """创建数据库连接"""
    try:
        connection = pymysql.connect(
            host=host,
            port=port,
            database='vr_classroom',
            user='root',
            password=password,
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

        cursor.execute("SET FOREIGN_KEY_CHECKS = 0")

        tables = [
            'certificate', 'payment_order', 'order_seat', '`order`',
            'donation_order', 'seat', 'classroom', 'building',
            'campus', 'comment', 'post_images', 'post',
            'user', 'category', 'college'
        ]
        for table in tables:
            cursor.execute(f"DELETE FROM {table}")
            print(f"已清空表: {table}")

        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")

        print("\n--- 插入基础数据 ---")
        
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

        categories = [
            ('技术分享',),
            ('学习交流',),
            ('校园生活',),
            ('活动通知',),
            ('求助问答',)
        ]
        cursor.executemany("INSERT INTO category (name) VALUES (%s)", categories)
        print(f"插入分类数据: {cursor.rowcount} 条")

        users = [
            ('13800138001', 'openid_001', '张三', 'https://example.com/avatar1.jpg', '1', 2),
            ('13800138002', 'openid_002', '李四', 'https://example.com/avatar2.jpg', '2', 2),
            ('13800138003', 'openid_003', '王五', 'https://example.com/avatar3.jpg', '3', 2),
            ('13800138004', 'openid_004', '赵六', 'https://example.com/avatar4.jpg', '1', 0),
            ('13800138005', 'openid_005', '钱七', 'https://example.com/avatar5.jpg', '2', 1)
        ]
        cursor.executemany(
            "INSERT INTO user (phone, open_id, name, avatar, college_id, verify_status) VALUES (%s, %s, %s, %s, %s, %s)",
            users
        )
        print(f"插入用户数据: {cursor.rowcount} 条")

        print("\n--- 插入论坛数据 ---")
        
        posts = [
            ('2026-02-24 10:00:00', '2026-02-24 10:00:00', '2026-02-24', '欢迎使用VR教室系统', '这是一个全新的VR教室系统，为大家提供沉浸式的学习体验',
             '大家好，欢迎使用我们的VR教室系统！\n\n这个系统可以让你在虚拟环境中体验真实的教室场景，与其他同学互动交流。\n\n希望大家能够喜欢并积极参与！',
             1, 3, 15, 5, 3, 1),
            ('2026-02-24 11:00:00', '2026-02-24 11:00:00', '2026-02-24', '如何使用VR设备', '详细介绍VR设备的使用方法和注意事项',
             '使用VR设备时需要注意以下几点：\n\n1. 确保设备电量充足\n2. 佩戴舒适，调整好头带\n3. 保持周围环境安全，有足够的活动空间\n4. 首次使用时建议有人陪同\n\n希望大家能够安全、愉快地使用VR设备！',
             2, 1, 28, 10, 2, 1),
            ('2026-02-24 12:00:00', '2026-02-24 12:00:00', '2026-02-24', 'VR教室系统功能介绍', '介绍VR教室系统的主要功能和使用方法',
             '我们的VR教室系统具有以下功能：\n\n1. 虚拟教室场景\n2. 实时互动交流\n3. 3D教学内容展示\n4. 座位捐赠系统\n\n更多功能正在开发中，敬请期待！',
             3, 2, 42, 18, 4, 1),
            ('2026-02-25 09:00:00', '2026-02-25 09:00:00', '2026-02-25', 'VR教室捐赠活动开始啦', '参与座位捐赠，共建智慧校园',
             '亲爱的同学们，VR教室座位捐赠活动正式开始！\n\n通过捐赠座位，您可以：\n- 获得专属座位命名权\n- 获得电子捐赠证书\n- 支持学校智慧教育发展\n\n欢迎广大师生积极参与！',
             1, 4, 56, 25, 6, 1),
            ('2026-02-25 14:00:00', '2026-02-25 14:00:00', '2026-02-25', 'VR设备使用常见问题解答', '汇总VR设备使用过程中的常见问题及解决方案',
             'Q: VR设备如何充电？\nA: 使用配套的USB-C充电线，充电时间约2-3小时。\n\nQ: 佩戴VR设备时感到头晕怎么办？\nA: 建议初次使用时间不超过15分钟，逐渐适应后可延长时间。\n\nQ: 如何清洁VR设备？\nA: 使用专用清洁布轻轻擦拭镜片，避免使用化学清洁剂。',
             2, 5, 33, 12, 3, 1)
        ]
        cursor.executemany(
            "INSERT INTO post (created_at, updated_at, date, title, summary, content, author_id, category_id, like_count, share_count, comment_count, status) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            posts
        )
        print(f"插入帖子数据: {cursor.rowcount} 条")

        post_images = [
            (1, 'https://example.com/images/vr-classroom-1.jpg'),
            (1, 'https://example.com/images/vr-classroom-2.jpg'),
            (2, 'https://example.com/images/vr-device-1.jpg'),
            (2, 'https://example.com/images/vr-device-2.jpg'),
            (3, 'https://example.com/images/vr-features-1.jpg'),
            (3, 'https://example.com/images/vr-features-2.jpg'),
            (3, 'https://example.com/images/vr-features-3.jpg'),
            (4, 'https://example.com/images/donation-event-1.jpg'),
            (4, 'https://example.com/images/donation-event-2.jpg'),
            (5, 'https://example.com/images/faq-1.jpg')
        ]
        cursor.executemany(
            "INSERT INTO post_images (post_id, images) VALUES (%s, %s)",
            post_images
        )
        print(f"插入帖子图片数据: {cursor.rowcount} 条")

        comments = [
            ('2026-02-24 10:30:00', '2026-02-24 10:30:00', '2026-02-24', '太好了，终于有这样的系统了！', 2, 1, 5, 1),
            ('2026-02-24 10:45:00', '2026-02-24 10:45:00', '2026-02-24', '期待早日体验', 3, 1, 3, 1),
            ('2026-02-24 11:30:00', '2026-02-24 11:30:00', '2026-02-24', '非常详细的介绍，谢谢分享', 1, 2, 8, 1),
            ('2026-02-24 12:30:00', '2026-02-24 12:30:00', '2026-02-24', '功能很强大，希望能够顺利使用', 2, 3, 6, 1),
            ('2026-02-25 10:00:00', '2026-02-25 10:00:00', '2026-02-25', '我已经报名参加了，期待拿到证书！', 3, 4, 12, 1),
            ('2026-02-25 15:00:00', '2026-02-25 15:00:00', '2026-02-25', '这些解答很有帮助，谢谢！', 1, 5, 4, 1)
        ]
        cursor.executemany(
            "INSERT INTO comment (created_at, updated_at, date, content, commenter_id, post_id, like_count, status) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
            comments
        )
        print(f"插入评论数据: {cursor.rowcount} 条")

        print("\n--- 插入校区建筑数据 ---")
        
        campuses = [
            ('主校区', '学校的主要校区，包含大部分学院和教学楼，是学校的行政中心', 1, True),
            ('东校区', '主要为理工科实验室和教学楼，配备先进的实验设备', 2, True),
            ('西校区', '主要为文科和艺术学院，环境优美，适合人文艺术学习', 3, True)
        ]
        cursor.executemany(
            "INSERT INTO campus (name, description, sort_order, active) VALUES (%s, %s, %s, %s)",
            campuses
        )
        print(f"插入校区数据: {cursor.rowcount} 条")

        buildings = [
            (1, '第一教学楼', '主要用于本科生教学，配备多媒体教室和VR体验室', 'https://vr.example.com/models/building1.glb', 1, True),
            (1, '第二教学楼', '主要用于研究生教学，设有研讨室和实验室', 'https://vr.example.com/models/building2.glb', 2, True),
            (1, '图书馆', '学校的主要图书馆，藏书丰富，设有自习室和电子阅览室', 'https://vr.example.com/models/library.glb', 3, True),
            (2, '实验楼', '理工科实验室，配备先进的实验设备', 'https://vr.example.com/models/lab.glb', 1, True),
            (2, '工程中心', '工程实践基地，支持学生创新项目', 'https://vr.example.com/models/engineering.glb', 2, True),
            (3, '艺术楼', '艺术学院教学楼，设有画室、音乐室等', 'https://vr.example.com/models/art.glb', 1, True),
            (3, '文科楼', '人文社科类教学场所', 'https://vr.example.com/models/humanities.glb', 2, True)
        ]
        cursor.executemany(
            "INSERT INTO building (campus_id, name, description, vr_model_url, sort_order, active) VALUES (%s, %s, %s, %s, %s, %s)",
            buildings
        )
        print(f"插入建筑数据: {cursor.rowcount} 条")

        classrooms = [
            (1, '101', '101教室', 1, '标准多媒体教室，支持VR教学', 'https://vr.example.com/models/room101.glb', 10, 5, 50, 5, True),
            (1, '102', '102教室', 1, '标准多媒体教室', 'https://vr.example.com/models/room102.glb', 10, 5, 50, 3, True),
            (1, '201', '201教室', 2, '大型阶梯教室', 'https://vr.example.com/models/room201.glb', 10, 10, 100, 12, True),
            (1, '202', '202教室', 2, '大型阶梯教室', 'https://vr.example.com/models/room202.glb', 10, 10, 100, 8, True),
            (2, '301', '301研讨室', 3, '小型研讨室，适合小组讨论', 'https://vr.example.com/models/room301.glb', 8, 10, 80, 6, True),
            (2, '302', '302研讨室', 3, '小型研讨室', 'https://vr.example.com/models/room302.glb', 8, 10, 80, 4, True),
            (3, 'R001', '阅览室', 1, '图书馆主阅览室，环境安静', 'https://vr.example.com/models/reading1.glb', 20, 10, 200, 25, True),
            (3, 'R002', '电子阅览室', 1, '配备电脑的电子阅览室', 'https://vr.example.com/models/reading2.glb', 15, 8, 120, 15, True),
            (4, 'L001', '物理实验室', 1, '物理实验专用教室', 'https://vr.example.com/models/lab_physics.glb', 5, 6, 30, 2, True),
            (4, 'L002', '化学实验室', 1, '化学实验专用教室', 'https://vr.example.com/models/lab_chemistry.glb', 5, 6, 30, 1, True),
            (5, 'E001', '工程实训室', 1, '工程实践训练室', 'https://vr.example.com/models/engineering1.glb', 6, 8, 48, 4, True),
            (6, 'A001', '美术教室', 1, '美术专业教室', 'https://vr.example.com/models/art1.glb', 5, 8, 40, 3, True),
            (6, 'A002', '音乐教室', 1, '音乐专业教室', 'https://vr.example.com/models/music1.glb', 4, 6, 24, 2, True),
            (7, 'H001', '语言教室', 1, '外语学习专用教室', 'https://vr.example.com/models/language1.glb', 6, 8, 48, 5, True)
        ]
        cursor.executemany(
            "INSERT INTO classroom (building_id, room_number, name, floor, description, vr_model_url, total_rows, total_cols, seat_count, claimed_count, active) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            classrooms
        )
        print(f"插入教室数据: {cursor.rowcount} 条")

        print("\n--- 插入座位数据 ---")
        # status: 0-过道/不可用, 1-可选, 2-锁定, 3-已购买

        seats = []
        # 101教室第1排: 4个已购买(status=3), 1个过道(status=0)
        seat_data = [
            (1, 1, 1, 3, 1, 1, '2026-02-24 09:00:00'),  # 已购买
            (1, 1, 2, 3, 1, 2, '2026-02-24 10:00:00'),  # 已购买
            (1, 1, 3, 0, 0, None, None),                # 过道
            (1, 1, 4, 3, 1, 3, '2026-02-24 11:00:00'),  # 已购买
            (1, 1, 5, 3, 1, 1, '2026-02-24 12:00:00'),  # 已购买
        ]
        seats.extend(seat_data)

        # 101教室第2-10排: 全部可选(status=1)
        for row in range(2, 11):
            for col in range(1, 6):
                seats.append((1, row, col, 1, 0, None, None))

        cursor.executemany(
            "INSERT INTO seat (room_id, `row`, `col`, status, version, donor_id, claimed_at) VALUES (%s, %s, %s, %s, %s, %s, %s)",
            seats
        )
        print(f"插入101教室座位数据: {cursor.rowcount} 条")

        # 102教室: 3个已购买(status=3), 其余可选(status=1)
        seats_102 = [
            (2, 1, 1, 3, 1, 1, '2026-02-25 09:00:00'),  # 已购买
            (2, 1, 2, 3, 1, 2, '2026-02-25 10:00:00'),  # 已购买
            (2, 1, 3, 3, 1, 3, '2026-02-25 11:00:00'),  # 已购买
            (2, 2, 1, 1, 0, None, None),                # 可选
            (2, 2, 2, 1, 0, None, None),                # 可选
            (2, 2, 3, 1, 0, None, None),                # 可选
            (2, 3, 1, 1, 0, None, None),                # 可选
            (2, 3, 2, 1, 0, None, None),                # 可选
            (2, 3, 3, 1, 0, None, None),                # 可选
        ]
        cursor.executemany(
            "INSERT INTO seat (room_id, `row`, `col`, status, version, donor_id, claimed_at) VALUES (%s, %s, %s, %s, %s, %s, %s)",
            seats_102
        )
        print(f"插入102教室座位数据: {cursor.rowcount} 条")

        print("\n--- 插入订单数据 ---")
        
        orders = [
            (1, 10000, 'PAID', '2026-02-24 09:10:00', '2026-02-24 09:00:00', '2026-02-24 09:05:00'),
            (2, 10000, 'PAID', '2026-02-24 10:10:00', '2026-02-24 10:00:00', '2026-02-24 10:03:00'),
            (3, 10000, 'PAID', '2026-02-24 11:10:00', '2026-02-24 11:00:00', '2026-02-24 11:02:00'),
            (1, 10000, 'PAID', '2026-02-24 12:10:00', '2026-02-24 12:00:00', '2026-02-24 12:04:00'),
            (4, 20000, 'PENDING', '2026-02-25 10:10:00', '2026-02-25 10:00:00', '2026-02-25 10:00:00'),
            (5, 15000, 'CANCELLED', '2026-02-25 11:10:00', '2026-02-25 11:00:00', '2026-02-25 11:05:00'),
        ]
        cursor.executemany(
            "INSERT INTO `order` (user_id, amount, status, expires_at, created_at, updated_at) VALUES (%s, %s, %s, %s, %s, %s)",
            orders
        )
        print(f"插入订单数据: {cursor.rowcount} 条")

        order_seats = [
            (1, 1),
            (2, 2),
            (3, 4),
            (4, 5),
            (5, 6),
            (5, 7),
            (6, 8),
        ]
        cursor.executemany(
            "INSERT INTO order_seat (order_id, seat_id) VALUES (%s, %s)",
            order_seats
        )
        print(f"插入订单座位数据: {cursor.rowcount} 条")

        print("\n--- 插入捐赠支付数据 ---")
        
        donation_orders = [
            (1, 1, 1, 100.00, '支持VR教室建设', 2, 'DON20260224001', '2026-02-24 09:00:00', '2026-02-24 09:05:00', '2026-02-24 09:10:00'),
            (2, 2, 2, 200.00, '为教育事业贡献一份力量', 2, 'DON20260224002', '2026-02-24 10:00:00', '2026-02-24 10:03:00', '2026-02-24 10:08:00'),
            (3, 4, 3, 500.00, '希望VR教室能够帮助更多学生', 2, 'DON20260224003', '2026-02-24 11:00:00', '2026-02-24 11:02:00', '2026-02-24 11:07:00'),
            (1, 5, 1, 100.00, '第二次捐赠', 2, 'DON20260224004', '2026-02-24 12:00:00', '2026-02-24 12:04:00', '2026-02-24 12:09:00'),
            (4, 6, 2, 200.00, '期待VR体验', 0, 'DON20260225001', '2026-02-25 10:00:00', None, None),
            (5, 8, 1, 100.00, '支持教育', 3, 'DON20260225002', '2026-02-25 11:00:00', None, None),
        ]
        cursor.executemany(
            "INSERT INTO donation_order (donor_id, seat_id, tier_id, amount, message, status, order_no, created_at, paid_at, completed_at) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            donation_orders
        )
        print(f"插入捐赠订单数据: {cursor.rowcount} 条")

        payment_orders = [
            (1, 100.00, 'PAY20260224001', 3, 'WECHAT', 'WX202602240001', 'DONATION', '1', 'VR教室座位捐赠', '2026-02-24 09:00:00', '2026-02-24 09:05:00', '2026-02-24 09:10:00'),
            (2, 200.00, 'PAY20260224002', 3, 'ALIPAY', 'ALI202602240001', 'DONATION', '2', 'VR教室座位捐赠', '2026-02-24 10:00:00', '2026-02-24 10:03:00', '2026-02-24 10:08:00'),
            (3, 500.00, 'PAY20260224003', 3, 'WECHAT', 'WX202602240002', 'DONATION', '3', 'VR教室座位捐赠', '2026-02-24 11:00:00', '2026-02-24 11:02:00', '2026-02-24 11:07:00'),
            (1, 100.00, 'PAY20260224004', 3, 'ALIPAY', 'ALI202602240002', 'DONATION', '4', 'VR教室座位捐赠', '2026-02-24 12:00:00', '2026-02-24 12:04:00', '2026-02-24 12:09:00'),
            (4, 200.00, 'PAY20260225001', 0, 'WECHAT', None, 'DONATION', '5', 'VR教室座位捐赠', '2026-02-25 10:00:00', None, None),
            (5, 100.00, 'PAY20260225002', 4, 'ALIPAY', None, 'DONATION', '6', 'VR教室座位捐赠', '2026-02-25 11:00:00', None, None),
        ]
        cursor.executemany(
            "INSERT INTO payment_order (user_id, amount, order_no, status, payment_method, transaction_id, product_type, product_id, remark, created_at, paid_at, completed_at) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
            payment_orders
        )
        print(f"插入支付订单数据: {cursor.rowcount} 条")

        certificates = [
            (1, 1, 'CERT20260224001', 'https://example.com/certificates/CERT20260224001.pdf', '2026-02-24 09:10:00'),
            (2, 2, 'CERT20260224002', 'https://example.com/certificates/CERT20260224002.pdf', '2026-02-24 10:08:00'),
            (3, 3, 'CERT20260224003', 'https://example.com/certificates/CERT20260224003.pdf', '2026-02-24 11:07:00'),
            (4, 1, 'CERT20260224004', 'https://example.com/certificates/CERT20260224004.pdf', '2026-02-24 12:09:00'),
        ]
        cursor.executemany(
            "INSERT INTO certificate (claim_id, donor_id, certificate_no, certificate_url, created_at) VALUES (%s, %s, %s, %s, %s)",
            certificates
        )
        print(f"插入证书数据: {cursor.rowcount} 条")

        connection.commit()
        print("\n✅ 所有模拟数据插入成功！")

        print("\n--- 数据统计 ---")
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()
        print(f"数据库中的表 ({len(tables)} 个):")
        for table in tables:
            table_name = list(table.values())[0]
            cursor.execute(f"SELECT COUNT(*) as cnt FROM `{table_name}`")
            count = cursor.fetchone()['cnt']
            print(f"  - {table_name}: {count} 条记录")

    except Error as e:
        print(f"插入数据时出错: {e}")
        connection.rollback()
    finally:
        if connection:
            cursor.close()
            connection.close()
            print("\n数据库连接已关闭")

if __name__ == "__main__":
    insert_mock_data()
