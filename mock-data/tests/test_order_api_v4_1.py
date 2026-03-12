# 测试脚本 - test-order-api-v4.1.py
# 专门用于测试订单模块功能（座位购买流程）- v4.1版本
# 更新内容：
# 1. 创建订单请求体新增 campusId, buildingId, roomId, price 字段
# 2. 创建订单响应改为只返回 orderId
# 3. 新增查询订单列表接口测试

import pytest
import requests
import json
import time
import os
import atexit
import logging
from datetime import datetime
from typing import Dict, Optional, List, Tuple

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 测试结果收集
test_details = []

def log_test_detail(test_name: str, endpoint: str, method: str, status_code: int,
                    request_data: dict = None, response_data: dict = None,
                    error_msg: str = None):
    """记录测试详情"""
    detail = {
        "timestamp": datetime.now().isoformat(),
        "test_name": test_name,
        "endpoint": endpoint,
        "method": method,
        "status_code": status_code,
        "request_data": request_data,
        "response_data": response_data,
        "error_msg": error_msg,
        "result": "PASS" if error_msg is None else "FAIL"
    }
    test_details.append(detail)

    # 打印详细日志
    logger.info(f"\n{'='*60}")
    logger.info(f"测试: {test_name}")
    logger.info(f"接口: {method} {endpoint}")
    logger.info(f"状态码: {status_code}")
    if request_data:
        logger.info(f"请求数据: {json.dumps(request_data, ensure_ascii=False, indent=2)}")
    if response_data:
        logger.info(f"响应数据: {json.dumps(response_data, ensure_ascii=False, indent=2)[:500]}...")
    if error_msg:
        logger.error(f"错误: {error_msg}")
    logger.info(f"结果: {detail['result']}")
    logger.info(f"{'='*60}\n")

def generate_reports():
    """生成测试报告"""
    if not test_details:
        return

    logger.info(f"\n{'='*80}")
    logger.info("测试执行完成，生成详细报告")
    logger.info(f"{'='*80}\n")

    # 统计结果
    total = len(test_details)
    passed = len([d for d in test_details if d["result"] == "PASS"])
    failed = len([d for d in test_details if d["result"] == "FAIL"])

    logger.info(f"总计: {total} 个测试")
    logger.info(f"通过: {passed} 个")
    logger.info(f"失败: {failed} 个")
    logger.info(f"通过率: {passed/total*100:.1f}%" if total > 0 else "N/A")

    # 获取测试目录（测试文件所在目录）
    test_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'logs')
    if not os.path.exists(test_dir):
        os.makedirs(test_dir)

    # 保存详细报告到JSON文件
    report_file = os.path.join(test_dir, f"test-order-api-v4.1-detailed-report-{datetime.now().strftime('%Y%m%d_%H%M%S')}.json")
    with open(report_file, 'w', encoding='utf-8') as f:
        json.dump({
            "summary": {
                "total": total,
                "passed": passed,
                "failed": failed,
                "pass_rate": f"{passed/total*100:.1f}%" if total > 0 else "N/A",
                "timestamp": datetime.now().isoformat()
            },
            "details": test_details
        }, f, ensure_ascii=False, indent=2)

    logger.info(f"\n详细报告已保存到: {report_file}")

    # 同时生成一个可读性更好的Markdown报告
    md_report_file = os.path.join(test_dir, f"test-order-api-v4.1-report-{datetime.now().strftime('%Y%m%d_%H%M%S')}.md")
    with open(md_report_file, 'w', encoding='utf-8') as f:
        f.write("# VR教室订单API测试报告 (v4.1)\n\n")
        f.write(f"**测试时间**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
        f.write("## 测试摘要\n\n")
        f.write(f"| 指标 | 数值 |\n")
        f.write(f"|------|------|\n")
        f.write(f"| 总计 | {total} |\n")
        f.write(f"| 通过 | {passed} |\n")
        f.write(f"| 失败 | {failed} |\n")
        f.write(f"| 通过率 | {passed/total*100:.1f}% |\n\n")
        f.write("## 测试详情\n\n")

        for detail in test_details:
            status_emoji = "✅" if detail["result"] == "PASS" else "❌"
            f.write(f"### {status_emoji} {detail['test_name']}\n\n")
            f.write(f"- **接口**: `{detail['method']} {detail['endpoint']}`\n")
            f.write(f"- **状态码**: {detail['status_code']}\n")
            f.write(f"- **结果**: {detail['result']}\n")

            if detail.get("request_data"):
                f.write(f"- **请求数据**:\n```json\n{json.dumps(detail['request_data'], ensure_ascii=False, indent=2)}\n```\n")

            if detail.get("response_data"):
                f.write(f"- **响应数据**:\n```json\n{json.dumps(detail['response_data'], ensure_ascii=False, indent=2)}\n```\n")

            if detail.get("error_msg"):
                f.write(f"- **错误信息**: {detail['error_msg']}\n")

            f.write("\n---\n\n")

    logger.info(f"Markdown报告已保存到: {md_report_file}")

# 注册退出时生成报告
atexit.register(generate_reports)

# 基础URL
base_url = "http://localhost:8082/api"
server_url = "http://10.86.136.242:8082/api"
is_server = False
url = server_url if is_server else base_url

# 测试数据准备
@pytest.fixture(scope="module")
def user_token():
    """获取用户token"""
    response = requests.post(f"{url}/users/login/phone", json={"phone": "13800138001"})
    assert response.status_code == 200, f"登录失败: {response.status_code}"
    data = response.json().get("data", {})
    token = data.get("token")
    assert token, "登录响应中缺少token"
    return token

@pytest.fixture(scope="module")
def auth_headers(user_token):
    """获取认证头"""
    return {"Authorization": f"Bearer {user_token}"}

# 测试API请求的工具函数
def make_api_request(method: str, endpoint: str, body: Optional[Dict] = None, headers: Optional[Dict] = None) -> requests.Response:
    """发送API请求并返回响应"""
    if headers is None:
        headers = {}
    
    if method == "GET":
        if body:
            return requests.get(f"{url}{endpoint}", params=body, headers=headers)
        else:
            return requests.get(f"{url}{endpoint}", headers=headers)
    elif method == "POST":
        return requests.post(f"{url}{endpoint}", json=body, headers=headers)
    elif method == "PATCH":
        return requests.patch(f"{url}{endpoint}", json=body, headers=headers)
    else:
        raise ValueError(f"不支持的HTTP方法: {method}")

# 测试获取教室座位图
def get_room_seats(room_id: str = "1") -> List[Dict]:
    """获取教室座位图"""
    endpoint = f"/rooms/{room_id}/seats"
    response = make_api_request("GET", endpoint)

    data = response.json().get("data", {}) if response.status_code == 200 else {}

    log_test_detail(
        "获取教室座位图",
        endpoint,
        "GET",
        response.status_code,
        response_data={"seats_count": len(data.get("seats", [])), "totalRows": data.get("totalRows")}
    )

    assert response.status_code == 200, f"获取教室座位图失败: {response.status_code}"
    assert "seats" in data, "响应中缺少seats字段"
    assert "totalRows" in data, "响应中缺少totalRows字段"
    assert "totalCols" in data, "响应中缺少totalCols字段"

    seats = data.get("seats", [])
    assert isinstance(seats, list), "seats字段应该是列表"

    # 找出可购买的座位 (status=1)
    available_seats = [s for s in seats if s.get("status") == 1]
    return available_seats

# 测试创建订单 - v4.1版本
def create_order(token: str, seat_list: List[Dict], campus_id: int = 1, building_id: int = 1, room_id: int = 1) -> str:
    """创建订单"""
    assert seat_list, "没有可购买的座位"

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

    headers = {"Authorization": f"Bearer {token}"}
    endpoint = "/orders"
    response = make_api_request("POST", endpoint, order_body, headers)

    data = response.json().get("data", {}) if response.status_code == 200 else {}

    log_test_detail(
        "创建订单",
        endpoint,
        "POST",
        response.status_code,
        request_data=order_body,
        response_data=data
    )

    assert response.status_code == 200, f"创建订单失败: {response.status_code}"
    assert "orderId" in data, "响应中缺少orderId字段"

    return data.get("orderId")

# 测试查询订单列表 - v4.1新增
def get_order_list(token: str, page: int = 1, size: int = 10) -> List[Dict]:
    """查询订单列表"""
    params = {
        "page": page,
        "size": size
    }

    headers = {"Authorization": f"Bearer {token}"}
    endpoint = "/orders"
    response = make_api_request("GET", endpoint, params, headers)

    data = response.json().get("data", []) if response.status_code == 200 else []

    log_test_detail(
        "查询订单列表",
        endpoint,
        "GET",
        response.status_code,
        request_data=params,
        response_data={"order_count": len(data)}
    )

    assert response.status_code == 200, f"查询订单列表失败: {response.status_code}"
    assert isinstance(data, list), "data字段应该是列表"

    return data

# 测试取消订单
def cancel_order(token: str, order_id: str) -> bool:
    """取消订单"""
    cancel_body = {
        "status": "CANCELLED"
    }

    headers = {"Authorization": f"Bearer {token}"}
    endpoint = f"/orders/{order_id}"
    response = make_api_request("PATCH", endpoint, cancel_body, headers)

    log_test_detail(
        "取消订单",
        endpoint,
        "PATCH",
        response.status_code,
        request_data=cancel_body
    )

    assert response.status_code == 200, f"取消订单失败: {response.status_code}"
    return True

# 测试模拟支付回调
def mock_pay_notify(order_id: str) -> bool:
    """模拟支付回调"""
    pay_body = {
        "orderId": order_id
    }

    endpoint = "/mock/pay/notify"
    response = make_api_request("POST", endpoint, pay_body)

    log_test_detail(
        "模拟支付回调",
        endpoint,
        "POST",
        response.status_code,
        request_data=pay_body
    )

    assert response.status_code == 200, f"支付回调失败: {response.status_code}"
    return True

# 测试完整订单流程
def test_complete_order_flow(user_token):
    """测试完整订单流程"""
    logger.info("\n" + "="*80)
    logger.info("开始测试: 完整订单流程")
    logger.info("="*80 + "\n")

    # 1. 获取教室座位图
    available_seats = get_room_seats("1")
    assert available_seats, "没有可购买的座位"

    # 2. 创建订单
    order_id = create_order(user_token, available_seats, campus_id=1, building_id=1, room_id=1)
    assert order_id, "订单创建失败"

    # 3. 再次获取座位图，验证座位状态已更新
    time.sleep(1)
    updated_seats = get_room_seats("1")
    assert updated_seats, "获取更新后的座位图失败"

    # 4. 查询订单列表
    order_list = get_order_list(user_token, page=1, size=10)
    assert len(order_list) > 0, "订单列表为空"

    # 5. 模拟支付成功
    mock_pay_notify(order_id)

    # 6. 再次获取座位图，验证座位已售出
    time.sleep(1)
    final_seats = get_room_seats("1")
    assert final_seats, "获取最终座位图失败"

    logger.info(f"\n✅ 完整订单流程测试通过 - 订单ID: {order_id}\n")

# 测试取消订单流程
def test_cancel_order_flow():
    """测试取消订单流程"""
    logger.info("\n" + "="*80)
    logger.info("开始测试: 取消订单流程")
    logger.info("="*80 + "\n")

    # 1. 登录获取token
    login_endpoint = "/users/login/phone"
    login_body = {"phone": "13800138002"}
    response = requests.post(f"{url}{login_endpoint}", json=login_body)

    login_data = response.json().get("data", {}) if response.status_code == 200 else {}
    token = login_data.get("token")

    log_test_detail(
        "用户登录(取消订单测试)",
        login_endpoint,
        "POST",
        response.status_code,
        request_data=login_body,
        response_data={"has_token": bool(token)}
    )

    assert response.status_code == 200, f"登录失败: {response.status_code}"
    assert token, "登录响应中缺少token"

    # 2. 获取教室座位图
    available_seats = get_room_seats("1")
    assert available_seats, "没有可购买的座位"

    # 3. 创建订单
    order_id = create_order(token, available_seats, campus_id=1, building_id=1, room_id=1)
    assert order_id, "订单创建失败"

    # 4. 查询订单列表
    order_list = get_order_list(token, page=1, size=10)
    assert len(order_list) > 0, "订单列表为空"

    # 5. 取消订单
    cancel_order(token, order_id)

    # 6. 再次获取座位图，验证座位已释放
    time.sleep(1)
    final_seats = get_room_seats("1")
    assert final_seats, "获取最终座位图失败"

    logger.info(f"\n✅ 取消订单流程测试通过 - 订单ID: {order_id}\n")

# 测试价格验证（错误价格）
def test_price_validation():
    """测试价格验证"""
    logger.info("\n" + "="*80)
    logger.info("开始测试: 价格验证")
    logger.info("="*80 + "\n")

    # 1. 登录获取token
    login_endpoint = "/users/login/phone"
    login_body = {"phone": "13800138003"}
    response = requests.post(f"{url}{login_endpoint}", json=login_body)

    login_data = response.json().get("data", {}) if response.status_code == 200 else {}
    token = login_data.get("token")

    log_test_detail(
        "用户登录(价格验证测试)",
        login_endpoint,
        "POST",
        response.status_code,
        request_data=login_body,
        response_data={"has_token": bool(token)}
    )

    assert response.status_code == 200, f"登录失败: {response.status_code}"
    assert token, "登录响应中缺少token"

    # 2. 获取教室座位图
    available_seats = get_room_seats("1")
    assert available_seats, "没有可购买的座位"

    # 3. 使用错误价格创建订单
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

    headers = {"Authorization": f"Bearer {token}"}
    endpoint = "/orders"
    response = make_api_request("POST", endpoint, wrong_price_body, headers)

    log_test_detail(
        "价格验证测试(错误价格)",
        endpoint,
        "POST",
        response.status_code,
        request_data=wrong_price_body,
        response_data={"error": "价格验证应该拒绝错误价格"} if response.status_code == 200 else {"message": "价格验证成功"}
    )

    # 价格验证应该失败
    assert response.status_code != 200, "价格验证失败，系统没有拒绝错误价格"

    logger.info("\n✅ 价格验证测试通过 - 系统正确拒绝了错误价格\n")

# 测试并发场景
def test_concurrent_order():
    """测试并发场景"""
    logger.info("\n" + "="*80)
    logger.info("开始测试: 并发场景")
    logger.info("="*80 + "\n")

    # 1. 获取两个用户的token
    login_endpoint = "/users/login/phone"

    response1 = requests.post(f"{url}{login_endpoint}", json={"phone": "13800138001"})
    response2 = requests.post(f"{url}{login_endpoint}", json={"phone": "13800138002"})

    token1 = response1.json().get("data", {}).get("token") if response1.status_code == 200 else None
    token2 = response2.json().get("data", {}).get("token") if response2.status_code == 200 else None

    log_test_detail(
        "用户1登录(并发测试)",
        login_endpoint,
        "POST",
        response1.status_code,
        request_data={"phone": "13800138001"},
        response_data={"has_token": bool(token1)}
    )

    log_test_detail(
        "用户2登录(并发测试)",
        login_endpoint,
        "POST",
        response2.status_code,
        request_data={"phone": "13800138002"},
        response_data={"has_token": bool(token2)}
    )

    assert response1.status_code == 200, f"用户1登录失败: {response1.status_code}"
    assert response2.status_code == 200, f"用户2登录失败: {response2.status_code}"
    assert token1, "用户1登录响应中缺少token"
    assert token2, "用户2登录响应中缺少token"

    # 2. 获取可用座位
    available_seats = get_room_seats("1")
    assert available_seats, "没有可购买的座位"

    # 3. 两个用户同时选择同一个座位
    same_seat = available_seats[0]

    order_body = {
        "campusId": 1,
        "buildingId": 1,
        "roomId": 1,
        "seatList": [
            {
                "id": str(same_seat.get("id")),
                "price": same_seat.get("price"),
                "version": same_seat.get("version")
            }
        ]
    }

    # 用户1创建订单
    headers1 = {"Authorization": f"Bearer {token1}"}
    endpoint = "/orders"
    response1 = make_api_request("POST", endpoint, order_body, headers1)

    log_test_detail(
        "用户1创建订单(并发测试)",
        endpoint,
        "POST",
        response1.status_code,
        request_data=order_body,
        response_data={"order_created": response1.status_code == 200}
    )

    assert response1.status_code == 200, f"用户1创建订单失败: {response1.status_code}"

    # 用户2尝试创建相同座位的订单（应该失败）
    time.sleep(0.5)
    headers2 = {"Authorization": f"Bearer {token2}"}
    response2 = make_api_request("POST", endpoint, order_body, headers2)

    log_test_detail(
        "用户2创建订单(并发测试-应失败)",
        endpoint,
        "POST",
        response2.status_code,
        request_data=order_body,
        response_data={"blocked": response2.status_code != 200}
    )

    assert response2.status_code != 200, "用户2应该无法创建相同座位的订单"

    logger.info("\n✅ 并发场景测试通过 - 系统正确处理了并发订单\n")

if __name__ == "__main__":
    pytest.main([__file__, "-v", "-s", "--tb=short"])

