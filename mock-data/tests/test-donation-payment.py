# 测试脚本 - test-donation-payment.py
# 专门用于测试捐赠与支付功能

import requests
import json
import uuid

# 基础URL
base_url = "http://localhost:8080/api"
server_url = "http://10.86.136.242:8082/api"
is_server = False
url = server_url if is_server else base_url

# 测试token (实际测试时需要替换为真实token)
token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzcyNTAyNzczLCJleHAiOjE3NzI1ODkxNzN9.6XyemPm957Zl4UVQq_Z4HgDA95fQQTrNUey3yn76bkc90r11nJDgO2f2KzetQ_ga"

# 测试结果存储
test_results = []

# 测试函数
def test_api(method, endpoint, body=None, require_auth=False, use_params=False):
    headers = {}
    if require_auth:
        headers["Authorization"] = token
    
    try:
        if method == "GET":
            if body:
                params = body
                response = requests.get(f"{url}{endpoint}", params=params, headers=headers)
            else:
                response = requests.get(f"{url}{endpoint}", headers=headers)
        elif method == "POST":
            if use_params and body:
                # 使用查询参数
                print(f"发送 POST 请求到 {url}{endpoint}，参数: {body}")
                response = requests.post(f"{url}{endpoint}", params=body, headers=headers)
            else:
                # 使用 JSON 请求体
                print(f"发送 POST 请求到 {url}{endpoint}，请求体: {body}")
                response = requests.post(f"{url}{endpoint}", json=body, headers=headers)
        elif method == "PUT":
            response = requests.put(f"{url}{endpoint}", json=body, headers=headers)
        elif method == "DELETE":
            response = requests.delete(f"{url}{endpoint}", headers=headers)
        else:
            print(f"❌ 不支持的方法: {method}")
            return
        
        # 打印响应信息
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        response.raise_for_status()
        
        result = {
            "endpoint": endpoint,
            "method": method,
            "status": "Success",
            "status_code": response.status_code,
            "response": response.json()
        }
        
        test_results.append(result)
        print(f"✅ {method} {endpoint} - Success")
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

# 生成唯一的订单号后缀
def generate_unique_suffix():
    return str(uuid.uuid4()).split('-')[0].upper()

# 测试捐赠与支付流程
def test_donation_payment_flow():
    print("\n=== 开始测试捐赠与支付流程 ===")
    
    # 1. 创建捐赠订单
    print("\n1. 测试创建捐赠订单")
    donation_body = {
        "seatId": 1,  # 假设座位ID为1
        "tierId": 1,  # 假设层级ID为1
        "message": "测试捐赠消息",
        "paymentMethod": "WECHAT"
    }
    donation_result = test_api("POST", "/donation/create", donation_body, require_auth=True)
    
    if donation_result.get("status") != "Success":
        print("❌ 捐赠流程测试失败：无法创建捐赠订单")
        return
    
    donation_id = donation_result.get("response", {}).get("data", {}).get("id")
    if not donation_id:
        print("❌ 捐赠流程测试失败：无法获取捐赠订单ID")
        return
    print(f"✅ 创建捐赠订单成功，ID: {donation_id}")
    
    # 2. 创建支付订单（此步骤已在创建捐赠订单时自动完成，这里可以跳过）
    print("\n2. 跳过创建支付订单步骤（已在捐赠流程中自动创建）")
    # 从捐赠结果中获取支付订单信息
    payment_order_no = donation_result.get("response", {}).get("data", {}).get("orderNo")
    if not payment_order_no:
        print("❌ 捐赠流程测试失败：无法获取支付订单号")
        return
    print(f"✅ 支付订单已创建，订单号: {payment_order_no}")
    
    # 3. 获取支付订单信息
    print("\n3. 测试获取支付订单信息")
    test_api("GET", f"/payment/orders/{payment_order_no}")
    
    # 4. 模拟支付回调
    print("\n4. 测试处理支付回调")
    # 构建查询参数
    callback_params = {
        "orderNo": payment_order_no,
        "transactionId": f"TXN{generate_unique_suffix()}",
        "status": 1,  # 1: 已支付
        "sign": "test_sign"
    }
    # 支付回调接口期望的是查询参数，不是JSON请求体
    test_api("POST", "/payment/callback", callback_params, use_params=True)
    
    # 5. 再次获取支付订单信息，验证状态是否更新
    print("\n5. 测试获取支付订单信息（验证状态更新）")
    test_api("GET", f"/payment/orders/{payment_order_no}")
    
    # 6. 测试获取用户的支付订单列表
    print("\n6. 测试获取用户的支付订单列表")
    test_api("GET", "/payment/orders", require_auth=True)
    
    print("\n=== 捐赠与支付流程测试完成 ===")

# 测试取消支付订单
def test_cancel_payment():
    print("\n=== 测试取消支付订单 ===")
    
    # 1. 创建一个新的支付订单
    print("\n1. 创建测试用支付订单")
    payment_body = {
        "amount": 50.0,
        "productType": "DONATION",
        "productId": "1",  # 假设目标ID为1
        "paymentMethod": "ALIPAY",
        "remark": f"测试取消支付 {generate_unique_suffix()}"
    }
    payment_result = test_api("POST", "/payment/create", payment_body, require_auth=True)
    
    if payment_result.get("status") != "Success":
        print("❌ 取消支付测试失败：无法创建支付订单")
        return
    
    payment_id = payment_result.get("response", {}).get("data", {}).get("id")
    if not payment_id:
        print("❌ 取消支付测试失败：无法获取支付订单ID")
        return
    print(f"✅ 创建支付订单成功，ID: {payment_id}")
    
    # 2. 取消支付订单
    print("\n2. 测试取消支付订单")
    test_api("POST", f"/payment/orders/{payment_id}/cancel", require_auth=True)
    
    # 3. 获取支付订单信息，验证状态是否已更新为取消
    payment_order_no = payment_result.get("response", {}).get("data", {}).get("orderNo")
    if payment_order_no:
        print("\n3. 测试获取支付订单信息（验证取消状态）")
        test_api("GET", f"/payment/orders/{payment_order_no}")

# 运行测试
if __name__ == "__main__":
    print("开始测试捐赠与支付功能")
    
    # 测试捐赠与支付完整流程
    test_donation_payment_flow()
    
    # 测试取消支付订单
    test_cancel_payment()
    
    # 输出测试结果
    print("\n=== 测试结果汇总 ===")
    for result in test_results:
        print(f"{result['method']} {result['endpoint']} - {result['status']}")
    
    # 统计成功和失败的测试用例
    success_count = len([r for r in test_results if r['status'] == "Success"])
    failed_count = len([r for r in test_results if r['status'] == "Failed"])
    
    print(f"\n测试完成: 成功 {success_count} 个，失败 {failed_count} 个")
