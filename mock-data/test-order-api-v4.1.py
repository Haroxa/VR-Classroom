# 测试脚本 - test-order-api-v4.1.py
# 专门用于测试订单模块功能（座位购买流程）- v4.1版本
# 更新内容：
# 1. 创建订单请求体新增 campusId, buildingId, roomId, price 字段
# 2. 创建订单响应改为只返回 orderId
# 3. 新增查询订单列表接口测试

import requests
import json
import time

# 基础URL
base_url = "http://localhost:8080/api"
server_url = "http://10.86.136.242:8082/api"
is_server = False
url = server_url if is_server else base_url

# 测试结果存储
test_results = []

# 测试函数
def test_api(method, endpoint, body=None, require_auth=False, use_params=False, token=None):
    headers = {}
    if require_auth and token:
        headers["Authorization"] = f"Bearer {token}"
    
    try:
        if method == "GET":
            if body:
                params = body
                response = requests.get(f"{url}{endpoint}", params=params, headers=headers)
            else:
                response = requests.get(f"{url}{endpoint}", headers=headers)
        elif method == "POST":
            if use_params and body:
                print(f"发送 POST 请求到 {url}{endpoint}，参数: {body}")
                response = requests.post(f"{url}{endpoint}", params=body, headers=headers)
            else:
                print(f"发送 POST 请求到 {url}{endpoint}，请求体: {body}")
                response = requests.post(f"{url}{endpoint}", json=body, headers=headers)
        elif method == "PATCH":
            print(f"发送 PATCH 请求到 {url}{endpoint}，请求体: {body}")
            response = requests.patch(f"{url}{endpoint}", json=body, headers=headers)
        else:
            print(f"❌ 不支持的方法: {method}")
            return None
        
        # 打印响应信息
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        result = {
            "endpoint": endpoint,
            "method": method,
            "status_code": response.status_code,
            "response": response.text
        }
        
        if response.status_code < 400:
            result["status"] = "Success"
            try:
                result["response"] = response.json()
            except:
                pass
            test_results.append(result)
            print(f"✅ {method} {endpoint} - Success")
            return result
        else:
            result["status"] = "Failed"
            test_results.append(result)
            print(f"❌ {method} {endpoint} - Failed: {response.status_code}")
            return result
            
    except Exception as e:
        result = {
            "endpoint": endpoint,
            "method": method,
            "status": "Failed",
            "error": str(e)
        }
        test_results.append(result)
        print(f"❌ {method} {endpoint} - Failed: {str(e)}")
        return result

# 用户登录获取token
def login_user(phone):
    response = requests.post(f"{url}/users/login/phone", json={"phone": phone})
    if response.status_code == 200:
        data = response.json().get("data", {})
        token = data.get("token")
        user = data.get("user", {})
        print(f"✅ 登录成功: {user.get('name')}")
        return token, user
    else:
        print(f"❌ 登录失败: {response.status_code} - {response.text}")
        return None, None

# 测试获取教室座位图
def test_get_room_seats(room_id="1"):
    print(f"\n=== 测试获取教室座位图 (roomId={room_id}) ===")
    result = test_api("GET", f"/rooms/{room_id}/seats")
    if result and result.get("status") == "Success":
        data = result.get("response", {}).get("data", {})
        seats = data.get("seats", [])
        print(f"教室总行列: {data.get('totalRows')} x {data.get('totalCols')}")
        print(f"座位数量: {len(seats)}")
        
        # 找出可购买的座位 (status=1)
        available_seats = [s for s in seats if s.get("status") == 1]
        print(f"可购买座位数量: {len(available_seats)}")
        
        return available_seats
    return []

# 测试创建订单 - v4.1版本
def test_create_order(token, seat_list, campus_id=1, building_id=1, room_id=1):
    print(f"\n=== 测试创建订单 (v4.1) ===")
    
    if not seat_list:
        print("❌ 没有可购买的座位")
        return None
    
    # 选择前2个座位进行测试
    selected_seats = seat_list[:2] if len(seat_list) >= 2 else seat_list[:1]
    
    # v4.1 请求体格式
    order_body = {
        "campusId": campus_id,
        "buildingId": building_id,
        "roomId": room_id,
        "seatList": [
            {
                "id": str(seat.get("id")),
                "price": seat.get("price"),  # v4.1 新增 price 字段
                "version": seat.get("version")
            } for seat in selected_seats
        ]
    }
    
    print(f"选择座位: {[s.get('id') for s in selected_seats]}")
    print(f"座位价格: {[s.get('price') for s in selected_seats]}")
    
    result = test_api("POST", "/orders", order_body, require_auth=True, token=token)
    
    if result and result.get("status") == "Success":
        data = result.get("response", {}).get("data", {})
        print(f"✅ 订单创建成功")
        print(f"   订单ID: {data.get('orderId')}")  # v4.1 只返回 orderId
        return data.get("orderId")
    return None

# 测试查询订单列表 - v4.1新增
def test_get_order_list(token, page=1, size=10):
    print(f"\n=== 测试查询订单列表 (v4.1新增) ===")
    
    params = {
        "page": page,
        "size": size
    }
    
    result = test_api("GET", "/orders", params, require_auth=True, token=token)
    
    if result and result.get("status") == "Success":
        data = result.get("response", {}).get("data", [])
        print(f"✅ 查询订单列表成功")
        print(f"   订单数量: {len(data)}")
        for order in data[:3]:  # 只显示前3个订单
            print(f"   - 订单ID: {order.get('id')}, 金额: {order.get('amount')}分, 状态: {order.get('status')}")
            print(f"     校区: {order.get('campusId')}, 教学楼: {order.get('buildingId')}, 教室: {order.get('roomId')}")
            seats = order.get('seatList', [])
            if seats:
                print(f"     座位: {[(s.get('row'), s.get('col'), s.get('lookPrice')) for s in seats[:2]]}")
        return data
    return []

# 测试取消订单
def test_cancel_order(token, order_id):
    print(f"\n=== 测试取消订单 (orderId={order_id}) ===")
    
    cancel_body = {
        "status": "CANCELLED"
    }
    
    result = test_api("PATCH", f"/orders/{order_id}", cancel_body, require_auth=True, token=token)
    
    if result and result.get("status") == "Success":
        print(f"✅ 订单取消成功")
        return True
    return False

# 测试模拟支付回调
def test_mock_pay_notify(order_id):
    print(f"\n=== 测试模拟支付回调 (orderId={order_id}) ===")
    
    pay_body = {
        "orderId": order_id
    }
    
    result = test_api("POST", "/mock/pay/notify", pay_body, require_auth=False)
    
    if result and result.get("status") == "Success":
        print(f"✅ 支付回调成功")
        return True
    return False

# 测试完整订单流程
def test_complete_order_flow():
    print("\n" + "="*60)
    print("开始测试完整订单流程 (v4.1)")
    print("="*60)
    
    # 1. 登录获取token
    print("\n--- 步骤1: 用户登录 ---")
    token, user = login_user("13800138001")
    if not token:
        print("❌ 登录失败，终止测试")
        return
    
    # 2. 获取教室座位图
    print("\n--- 步骤2: 获取教室座位图 ---")
    available_seats = test_get_room_seats("1")
    if not available_seats:
        print("❌ 没有可购买的座位，终止测试")
        return
    
    # 3. 创建订单
    print("\n--- 步骤3: 创建订单 ---")
    order_id = test_create_order(token, available_seats, campus_id=1, building_id=1, room_id=1)
    if not order_id:
        print("❌ 订单创建失败，终止测试")
        return
    
    # 4. 再次获取座位图，验证座位状态已更新
    print("\n--- 步骤4: 验证座位状态更新 ---")
    time.sleep(1)
    test_get_room_seats("1")
    
    # 5. 查询订单列表 (v4.1新增)
    print("\n--- 步骤5: 查询订单列表 (v4.1新增) ---")
    test_get_order_list(token, page=1, size=10)
    
    # 6. 模拟支付成功
    print("\n--- 步骤6: 模拟支付成功 ---")
    test_mock_pay_notify(order_id)
    
    # 7. 再次获取座位图，验证座位已售出
    print("\n--- 步骤7: 验证座位已售出 ---")
    time.sleep(1)
    test_get_room_seats("1")
    
    print("\n" + "="*60)
    print("完整订单流程测试完成")
    print("="*60)

# 测试取消订单流程
def test_cancel_order_flow():
    print("\n" + "="*60)
    print("开始测试取消订单流程")
    print("="*60)
    
    # 1. 登录获取token
    print("\n--- 步骤1: 用户登录 ---")
    token, user = login_user("13800138002")
    if not token:
        print("❌ 登录失败，终止测试")
        return
    
    # 2. 获取教室座位图
    print("\n--- 步骤2: 获取教室座位图 ---")
    available_seats = test_get_room_seats("1")
    if not available_seats:
        print("❌ 没有可购买的座位，终止测试")
        return
    
    # 3. 创建订单
    print("\n--- 步骤3: 创建订单 ---")
    order_id = test_create_order(token, available_seats, campus_id=1, building_id=1, room_id=1)
    if not order_id:
        print("❌ 订单创建失败，终止测试")
        return
    
    # 4. 查询订单列表
    print("\n--- 步骤4: 查询订单列表 ---")
    test_get_order_list(token, page=1, size=10)
    
    # 5. 取消订单
    print("\n--- 步骤5: 取消订单 ---")
    test_cancel_order(token, order_id)
    
    # 6. 再次获取座位图，验证座位已释放
    print("\n--- 步骤6: 验证座位已释放 ---")
    time.sleep(1)
    test_get_room_seats("1")
    
    print("\n" + "="*60)
    print("取消订单流程测试完成")
    print("="*60)

# 测试价格验证（错误价格）
def test_price_validation():
    print("\n" + "="*60)
    print("开始测试价格验证")
    print("="*60)
    
    # 1. 登录获取token
    print("\n--- 步骤1: 用户登录 ---")
    token, user = login_user("13800138003")
    if not token:
        print("❌ 登录失败，终止测试")
        return
    
    # 2. 获取教室座位图
    print("\n--- 步骤2: 获取教室座位图 ---")
    available_seats = test_get_room_seats("1")
    if not available_seats:
        print("❌ 没有可购买的座位，终止测试")
        return
    
    # 3. 使用错误价格创建订单
    print("\n--- 步骤3: 使用错误价格创建订单 ---")
    selected_seats = available_seats[:1]
    
    # 构造错误价格的请求体
    wrong_price_body = {
        "campusId": 1,
        "buildingId": 1,
        "roomId": 1,
        "seatList": [
            {
                "id": str(selected_seats[0].get("id")),
                "price": 99999,  # 错误价格
                "version": selected_seats[0].get("version")
            }
        ]
    }
    
    print(f"选择座位: {selected_seats[0].get('id')}")
    print(f"实际价格: {selected_seats[0].get('price')}")
    print(f"提交价格: 99999 (错误价格)")
    
    result = test_api("POST", "/orders", wrong_price_body, require_auth=True, token=token)
    
    if result and result.get("status") == "Failed":
        print("✅ 价格验证成功，系统正确拒绝了错误价格")
    else:
        print("❌ 价格验证失败，系统没有拒绝错误价格")
    
    print("\n" + "="*60)
    print("价格验证测试完成")
    print("="*60)

# 主函数
if __name__ == "__main__":
    print("VR教室订单模块测试脚本 v4.1")
    print("="*60)
    print("更新内容:")
    print("1. 创建订单请求体新增 campusId, buildingId, roomId, price 字段")
    print("2. 创建订单响应改为只返回 orderId")
    print("3. 新增查询订单列表接口")
    print("="*60)
    
    # 运行测试
    test_complete_order_flow()
    test_cancel_order_flow()
    test_price_validation()
    
    # 打印测试汇总
    print("\n" + "="*60)
    print("测试汇总")
    print("="*60)
    success_count = len([r for r in test_results if r.get("status") == "Success"])
    failed_count = len([r for r in test_results if r.get("status") == "Failed"])
    print(f"总测试数: {len(test_results)}")
    print(f"成功: {success_count}")
    print(f"失败: {failed_count}")
