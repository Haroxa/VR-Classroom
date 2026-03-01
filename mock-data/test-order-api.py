# 测试脚本 - test-order-api.py
# 专门用于测试订单模块功能（座位购买流程）

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
    response = requests.post(f"{url}/users/login", json={"phone": phone})
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

# 测试创建订单
def test_create_order(token, seat_list):
    print(f"\n=== 测试创建订单 ===")
    
    if not seat_list:
        print("❌ 没有可购买的座位")
        return None
    
    # 选择前2个座位进行测试
    selected_seats = seat_list[:2] if len(seat_list) >= 2 else seat_list[:1]
    
    order_body = {
        "seatList": [
            {
                "id": str(seat.get("id")),
                "version": seat.get("version")
            } for seat in selected_seats
        ]
    }
    
    print(f"选择座位: {[s.get('id') for s in selected_seats]}")
    
    result = test_api("POST", "/orders", order_body, require_auth=True, token=token)
    
    if result and result.get("status") == "Success":
        data = result.get("response", {}).get("data", {})
        print(f"✅ 订单创建成功")
        print(f"   订单ID: {data.get('id')}")
        print(f"   金额: {data.get('amount')} 分")
        print(f"   状态: {data.get('status')}")
        print(f"   过期时间: {data.get('expiresAt')}")
        return data.get("id")
    return None

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
    print("开始测试完整订单流程")
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
    order_id = test_create_order(token, available_seats)
    if not order_id:
        print("❌ 订单创建失败，终止测试")
        return
    
    # 4. 再次获取座位图，验证座位状态已更新
    print("\n--- 步骤4: 验证座位状态更新 ---")
    time.sleep(1)
    test_get_room_seats("1")
    
    # 5. 模拟支付成功
    print("\n--- 步骤5: 模拟支付成功 ---")
    test_mock_pay_notify(order_id)
    
    # 6. 再次获取座位图，验证座位已售出
    print("\n--- 步骤6: 验证座位已售出 ---")
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
    order_id = test_create_order(token, available_seats)
    if not order_id:
        print("❌ 订单创建失败，终止测试")
        return
    
    # 4. 取消订单
    print("\n--- 步骤4: 取消订单 ---")
    test_cancel_order(token, order_id)
    
    # 5. 再次获取座位图，验证座位已释放
    print("\n--- 步骤5: 验证座位已释放 ---")
    time.sleep(1)
    test_get_room_seats("1")
    
    print("\n" + "="*60)
    print("取消订单流程测试完成")
    print("="*60)

# 测试并发场景（模拟多个用户同时选择同一个座位）
def test_concurrent_order():
    print("\n" + "="*60)
    print("开始测试并发场景")
    print("="*60)
    
    # 获取token
    token1, _ = login_user("13800138001")
    token2, _ = login_user("13800138002")
    
    if not token1 or not token2:
        print("❌ 登录失败，终止测试")
        return
    
    # 获取可用座位
    available_seats = test_get_room_seats("1")
    if not available_seats:
        print("❌ 没有可购买的座位，终止测试")
        return
    
    # 两个用户同时选择同一个座位
    same_seat = available_seats[0]
    print(f"\n两个用户同时选择座位: {same_seat.get('id')}")
    
    order_body = {
        "seatList": [
            {
                "id": str(same_seat.get("id")),
                "version": same_seat.get("version")
            }
        ]
    }
    
    # 用户1创建订单
    print("\n--- 用户1创建订单 ---")
    result1 = test_api("POST", "/orders", order_body, require_auth=True, token=token1)
    
    # 用户2尝试创建相同座位的订单（应该失败）
    print("\n--- 用户2尝试创建相同座位的订单 ---")
    time.sleep(0.5)
    result2 = test_api("POST", "/orders", order_body, require_auth=True, token=token2)
    
    print("\n" + "="*60)
    print("并发场景测试完成")
    print("="*60)

# 打印测试结果汇总
def print_test_summary():
    print("\n" + "="*60)
    print("测试结果汇总")
    print("="*60)
    
    success_count = sum(1 for r in test_results if r.get("status") == "Success")
    failed_count = len(test_results) - success_count
    
    print(f"总测试数: {len(test_results)}")
    print(f"成功: {success_count}")
    print(f"失败: {failed_count}")
    
    if failed_count > 0:
        print("\n失败的测试:")
        for result in test_results:
            if result.get("status") == "Failed":
                print(f"  - {result.get('method')} {result.get('endpoint')}: {result.get('status_code')}")

# 主函数
if __name__ == "__main__":
    print("="*60)
    print("订单模块API测试脚本")
    print("="*60)
    
    # 测试完整订单流程
    test_complete_order_flow()
    
    # 测试取消订单流程
    test_cancel_order_flow()
    
    # 测试并发场景
    test_concurrent_order()
    
    # 打印测试结果汇总
    print_test_summary()
    
    print("\n所有测试完成！")
