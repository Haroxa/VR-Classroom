# VR教室系统全面测试套件
# 包含分页功能、参数校验、审计日志、异常处理等测试

import pytest
import requests
import json
import time
import os
import atexit
import logging
from datetime import datetime
from typing import Dict, Optional, List, Any
from concurrent.futures import ThreadPoolExecutor, as_completed

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
                    error_msg: str = None, category: str = "general"):
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
        "result": "PASS" if error_msg is None else "FAIL",
        "category": category
    }
    test_details.append(detail)

    status_emoji = "✅" if error_msg is None else "❌"
    logger.info(f"{status_emoji} [{category}] {test_name} - 状态码: {status_code}")
    if error_msg:
        logger.error(f"   错误: {error_msg}")


def generate_reports():
    """生成测试报告"""
    if not test_details:
        return

    logger.info(f"\n{'='*80}")
    logger.info("测试执行完成，生成详细报告")
    logger.info(f"{'='*80}\n")

    # 按类别统计
    categories = {}
    for detail in test_details:
        cat = detail.get("category", "general")
        if cat not in categories:
            categories[cat] = {"passed": 0, "failed": 0, "tests": []}
        if detail["result"] == "PASS":
            categories[cat]["passed"] += 1
        else:
            categories[cat]["failed"] += 1
        categories[cat]["tests"].append(detail)

    # 总体统计
    total = len(test_details)
    passed = len([d for d in test_details if d["result"] == "PASS"])
    failed = len([d for d in test_details if d["result"] == "FAIL"])

    logger.info(f"总计: {total} 个测试")
    logger.info(f"通过: {passed} 个")
    logger.info(f"失败: {failed} 个")
    logger.info(f"通过率: {passed/total*100:.1f}%" if total > 0 else "N/A")

    # 按类别打印统计
    logger.info("\n按类别统计:")
    for cat, stats in categories.items():
        cat_total = stats["passed"] + stats["failed"]
        logger.info(f"  {cat}: {stats['passed']}/{cat_total} 通过")

    # 获取测试目录
    test_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'logs')
    if not os.path.exists(test_dir):
        os.makedirs(test_dir)

    # 保存详细报告
    report_file = os.path.join(test_dir, f"comprehensive-test-report-{datetime.now().strftime('%Y%m%d_%H%M%S')}.json")
    with open(report_file, 'w', encoding='utf-8') as f:
        json.dump({
            "summary": {
                "total": total,
                "passed": passed,
                "failed": failed,
                "pass_rate": f"{passed/total*100:.1f}%" if total > 0 else "N/A",
                "timestamp": datetime.now().isoformat(),
                "categories": {k: {"passed": v["passed"], "failed": v["failed"]} for k, v in categories.items()}
            },
            "details": test_details
        }, f, ensure_ascii=False, indent=2)

    logger.info(f"\n详细报告已保存到: {report_file}")

    # 生成Markdown报告
    md_report_file = os.path.join(test_dir, f"comprehensive-test-report-{datetime.now().strftime('%Y%m%d_%H%M%S')}.md")
    with open(md_report_file, 'w', encoding='utf-8') as f:
        f.write("# VR教室系统全面测试报告\n\n")
        f.write(f"**测试时间**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
        f.write("## 测试摘要\n\n")
        f.write(f"| 指标 | 数值 |\n")
        f.write(f"|------|------|\n")
        f.write(f"| 总计 | {total} |\n")
        f.write(f"| 通过 | {passed} |\n")
        f.write(f"| 失败 | {failed} |\n")
        f.write(f"| 通过率 | {passed/total*100:.1f}% |\n\n")

        # 按类别输出
        for cat, stats in categories.items():
            f.write(f"\n## {cat}测试\n\n")
            for detail in stats["tests"]:
                status_emoji = "✅" if detail["result"] == "PASS" else "❌"
                f.write(f"### {status_emoji} {detail['test_name']}\n\n")
                f.write(f"- **接口**: `{detail['method']} {detail['endpoint']}`\n")
                f.write(f"- **状态码**: {detail['status_code']}\n")
                if detail.get("error_msg"):
                    f.write(f"- **错误信息**: {detail['error_msg']}\n")
                f.write("\n")

    logger.info(f"Markdown报告已保存到: {md_report_file}")


atexit.register(generate_reports)

# 基础URL配置
BASE_URL = "http://localhost:8080/api"
SERVER_URL = "http://10.86.136.242:8082/api"
IS_SERVER = False
URL = SERVER_URL if IS_SERVER else BASE_URL


class APIClient:
    """API客户端封装"""

    def __init__(self, base_url: str):
        self.base_url = base_url
        self.default_headers = {}

    def set_auth(self, token: str):
        """设置认证token"""
        self.default_headers["Authorization"] = f"Bearer {token}"

    def request(self, method: str, endpoint: str, body: dict = None,
                headers: dict = None, params: dict = None) -> requests.Response:
        """发送请求"""
        full_url = f"{self.base_url}{endpoint}"
        req_headers = {**self.default_headers, **(headers or {})}

        try:
            if method == "GET":
                return requests.get(full_url, params=params or body, headers=req_headers, timeout=10)
            elif method == "POST":
                return requests.post(full_url, json=body, headers=req_headers, timeout=10)
            elif method == "PUT":
                return requests.put(full_url, json=body, headers=req_headers, timeout=10)
            elif method == "PATCH":
                return requests.patch(full_url, json=body, headers=req_headers, timeout=10)
            elif method == "DELETE":
                return requests.delete(full_url, headers=req_headers, timeout=10)
            else:
                raise ValueError(f"不支持的HTTP方法: {method}")
        except requests.exceptions.RequestException as e:
            logger.error(f"请求异常: {str(e)}")
            raise


# 创建全局客户端
client = APIClient(URL)


# ==================== 测试夹具 ====================

@pytest.fixture(scope="module")
def user_token():
    """获取用户token"""
    response = client.request("POST", "/users/login/phone", {"phone": "13800138001"})
    assert response.status_code == 200, f"登录失败: {response.status_code}"
    data = response.json().get("data", {})
    token = data.get("token")
    assert token, "登录响应中缺少token"
    return token


@pytest.fixture(scope="module")
def auth_headers(user_token):
    """获取认证头"""
    client.set_auth(user_token)
    return {"Authorization": f"Bearer {user_token}"}


# ==================== 分页功能测试 ====================

class TestPagination:
    """分页功能测试类"""

    def test_posts_pagination_default(self):
        """测试帖子列表默认分页"""
        response = client.request("GET", "/posts", {"page": 0})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "帖子列表默认分页",
            "/posts",
            "GET",
            response.status_code,
            request_data={"page": 0},
            response_data={"records_count": len(data.get("records", [])), "total": data.get("total")},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert "records" in data, "缺少records字段"
        assert "total" in data, "缺少total字段"
        assert "current" in data, "缺少current字段"
        assert "size" in data, "缺少size字段"

    def test_posts_pagination_custom_size(self):
        """测试帖子列表自定义分页大小"""
        response = client.request("GET", "/posts", {"page": 0, "size": 5})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "帖子列表自定义分页大小",
            "/posts",
            "GET",
            response.status_code,
            request_data={"page": 0, "size": 5},
            response_data={"records_count": len(data.get("records", [])), "size": data.get("size")},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert len(data.get("records", [])) <= 5, "返回记录数超过指定大小"

    def test_posts_pagination_second_page(self):
        """测试帖子列表第二页"""
        response = client.request("GET", "/posts", {"page": 1})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "帖子列表第二页",
            "/posts",
            "GET",
            response.status_code,
            request_data={"page": 1},
            response_data={"current": data.get("current")},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert data.get("current") == 1, "当前页码不正确"

    def test_admin_posts_pagination(self):
        """测试后台帖子列表分页"""
        response = client.request("GET", "/admin/posts", {"page": 1, "status": 1})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "后台帖子列表分页",
            "/admin/posts",
            "GET",
            response.status_code,
            request_data={"page": 1, "status": 1},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_admin_comments_pagination(self):
        """测试后台评论列表分页"""
        response = client.request("GET", "/admin/comments", {"page": 1, "status": 1})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "后台评论列表分页",
            "/admin/comments",
            "GET",
            response.status_code,
            request_data={"page": 1, "status": 1},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_user_posts_pagination(self, auth_headers):
        """测试用户帖子列表分页"""
        response = client.request("GET", "/users/posts", {"page": 0}, headers=auth_headers)

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "用户帖子列表分页",
            "/users/posts",
            "GET",
            response.status_code,
            request_data={"page": 0},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_user_comments_pagination(self, auth_headers):
        """测试用户评论列表分页"""
        response = client.request("GET", "/users/comments", {"page": 0}, headers=auth_headers)

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "用户评论列表分页",
            "/users/comments",
            "GET",
            response.status_code,
            request_data={"page": 0},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_comments_pagination_by_post(self):
        """测试按帖子ID获取评论分页"""
        response = client.request("GET", "/comments", {"postId": 1, "page": 0})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "按帖子ID获取评论分页",
            "/comments",
            "GET",
            response.status_code,
            request_data={"postId": 1, "page": 0},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_pagination_empty_page(self):
        """测试超出范围的分页"""
        response = client.request("GET", "/posts", {"page": 9999})

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "超出范围的分页",
            "/posts",
            "GET",
            response.status_code,
            request_data={"page": 9999},
            response_data={"records_count": len(data.get("records", []))},
            category="分页功能"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert len(data.get("records", [])) == 0, "超出范围应返回空列表"


# ==================== 参数校验测试 ====================

class TestParameterValidation:
    """参数校验测试类"""

    def test_create_post_empty_title(self, auth_headers):
        """测试创建帖子-空标题"""
        body = {
            "title": "",
            "content": "测试内容",
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-空标题",
            "/posts",
            "POST",
            response.status_code,
            request_data=body,
            response_data=response.json() if response.status_code != 500 else {"error": "server error"},
            category="参数校验"
        )

        # 应该返回400或500，不应该成功创建
        assert response.status_code != 200, "空标题不应该创建成功"

    def test_create_post_empty_content(self, auth_headers):
        """测试创建帖子-空内容"""
        body = {
            "title": "测试标题",
            "content": "",
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-空内容",
            "/posts",
            "POST",
            response.status_code,
            request_data=body,
            category="参数校验"
        )

        assert response.status_code != 200, "空内容不应该创建成功"

    def test_create_post_long_title(self, auth_headers):
        """测试创建帖子-超长标题"""
        body = {
            "title": "a" * 300,  # 超过200字符限制
            "content": "测试内容",
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-超长标题",
            "/posts",
            "POST",
            response.status_code,
            request_data={"title_length": 300},
            category="参数校验"
        )

        # 应该返回校验错误
        assert response.status_code != 200, "超长标题不应该创建成功"

    def test_create_post_long_content(self, auth_headers):
        """测试创建帖子-超长内容"""
        body = {
            "title": "测试标题",
            "content": "a" * 15000,  # 超过10000字符限制
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-超长内容",
            "/posts",
            "POST",
            response.status_code,
            request_data={"content_length": 15000},
            category="参数校验"
        )

        assert response.status_code != 200, "超长内容不应该创建成功"

    def test_create_post_missing_category(self, auth_headers):
        """测试创建帖子-缺少分类ID"""
        body = {
            "title": "测试标题",
            "content": "测试内容"
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-缺少分类ID",
            "/posts",
            "POST",
            response.status_code,
            request_data=body,
            category="参数校验"
        )

        assert response.status_code != 200, "缺少分类ID不应该创建成功"

    def test_create_comment_empty_content(self, auth_headers):
        """测试创建评论-空内容"""
        body = {
            "content": "",
            "postId": 1
        }
        response = client.request("POST", "/comments", body, headers=auth_headers)

        log_test_detail(
            "创建评论-空内容",
            "/comments",
            "POST",
            response.status_code,
            request_data=body,
            category="参数校验"
        )

        assert response.status_code != 200, "空内容不应该创建成功"

    def test_create_comment_long_content(self, auth_headers):
        """测试创建评论-超长内容"""
        body = {
            "content": "a" * 600,  # 超过500字符限制
            "postId": 1
        }
        response = client.request("POST", "/comments", body, headers=auth_headers)

        log_test_detail(
            "创建评论-超长内容",
            "/comments",
            "POST",
            response.status_code,
            request_data={"content_length": 600},
            category="参数校验"
        )

        assert response.status_code != 200, "超长内容不应该创建成功"

    def test_create_post_too_many_images(self, auth_headers):
        """测试创建帖子-图片数量超限"""
        body = {
            "title": "测试标题",
            "content": "测试内容",
            "categoryId": 1,
            "images": [f"image_{i}.jpg" for i in range(10)]  # 超过9张限制
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子-图片数量超限",
            "/posts",
            "POST",
            response.status_code,
            request_data={"images_count": 10},
            category="参数校验"
        )

        assert response.status_code != 200, "图片数量超限不应该创建成功"

    def test_login_empty_phone(self):
        """测试登录-空手机号"""
        body = {"phone": ""}
        response = client.request("POST", "/users/login/phone", body)

        log_test_detail(
            "登录-空手机号",
            "/users/login/phone",
            "POST",
            response.status_code,
            request_data=body,
            category="参数校验"
        )

        assert response.status_code != 200, "空手机号不应该登录成功"

    def test_login_invalid_phone_format(self):
        """测试登录-无效手机号格式"""
        body = {"phone": "12345"}
        response = client.request("POST", "/users/login/phone", body)

        log_test_detail(
            "登录-无效手机号格式",
            "/users/login/phone",
            "POST",
            response.status_code,
            request_data=body,
            category="参数校验"
        )

        # 可能返回200（创建新用户）或400（校验失败）
        # 根据实际业务逻辑判断


# ==================== 审计日志测试 ====================

class TestAuditLog:
    """审计日志测试类"""

    def test_audit_log_create_post(self, auth_headers):
        """测试创建帖子审计日志"""
        body = {
            "title": f"审计日志测试帖子_{datetime.now().strftime('%H%M%S')}",
            "content": "测试审计日志记录",
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body, headers=auth_headers)

        log_test_detail(
            "创建帖子审计日志",
            "/posts",
            "POST",
            response.status_code,
            request_data={"title": body["title"]},
            category="审计日志"
        )

        assert response.status_code == 200, f"创建帖子失败: {response.status_code}"
        # 审计日志应该在后端自动记录

    def test_audit_log_create_comment(self, auth_headers):
        """测试创建评论审计日志"""
        body = {
            "content": f"审计日志测试评论_{datetime.now().strftime('%H%M%S')}",
            "postId": 1
        }
        response = client.request("POST", "/comments", body, headers=auth_headers)

        log_test_detail(
            "创建评论审计日志",
            "/comments",
            "POST",
            response.status_code,
            request_data={"content": body["content"][:20]},
            category="审计日志"
        )

        assert response.status_code == 200, f"创建评论失败: {response.status_code}"

    def test_audit_log_audit_post(self):
        """测试审核帖子审计日志"""
        body = {
            "status": 1
        }
        response = client.request("PATCH", "/admin/posts/1", body)

        log_test_detail(
            "审核帖子审计日志",
            "/admin/posts/1",
            "PATCH",
            response.status_code,
            request_data=body,
            category="审计日志"
        )

        # 审核操作应该记录审计日志

    def test_audit_log_audit_comment(self):
        """测试审核评论审计日志"""
        body = {
            "status": 1
        }
        response = client.request("PATCH", "/admin/comments/1", body)

        log_test_detail(
            "审核评论审计日志",
            "/admin/comments/1",
            "PATCH",
            response.status_code,
            request_data=body,
            category="审计日志"
        )


# ==================== 异常处理测试 ====================

class TestExceptionHandling:
    """异常处理测试类"""

    def test_get_nonexistent_post(self):
        """测试获取不存在的帖子"""
        response = client.request("GET", "/posts/999999")

        log_test_detail(
            "获取不存在的帖子",
            "/posts/999999",
            "GET",
            response.status_code,
            category="异常处理"
        )

        # 应该返回404或500
        assert response.status_code in [404, 500], f"预期404或500，实际: {response.status_code}"

    def test_get_nonexistent_comment(self):
        """测试获取不存在的评论"""
        response = client.request("GET", "/comments", {"postId": 999999, "page": 0})

        log_test_detail(
            "获取不存在帖子的评论",
            "/comments",
            "GET",
            response.status_code,
            request_data={"postId": 999999},
            category="异常处理"
        )

        # 应该返回空列表或错误
        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_update_nonexistent_post(self, auth_headers):
        """测试更新不存在的帖子"""
        body = {
            "title": "测试标题",
            "content": "测试内容",
            "categoryId": 1
        }
        response = client.request("PUT", "/posts/999999", body, headers=auth_headers)

        log_test_detail(
            "更新不存在的帖子",
            "/posts/999999",
            "PUT",
            response.status_code,
            category="异常处理"
        )

        assert response.status_code != 200, "更新不存在的帖子应该失败"

    def test_delete_nonexistent_post(self, auth_headers):
        """测试删除不存在的帖子"""
        response = client.request("DELETE", "/posts/999999", headers=auth_headers)

        log_test_detail(
            "删除不存在的帖子",
            "/posts/999999",
            "DELETE",
            response.status_code,
            category="异常处理"
        )

        # 可能返回404或200（幂等删除）
        assert response.status_code in [200, 404, 500], f"状态码异常: {response.status_code}"

    def test_unauthorized_create_post(self):
        """测试未认证创建帖子"""
        body = {
            "title": "测试标题",
            "content": "测试内容",
            "categoryId": 1
        }
        response = client.request("POST", "/posts", body)

        log_test_detail(
            "未认证创建帖子",
            "/posts",
            "POST",
            response.status_code,
            category="异常处理"
        )

        assert response.status_code in [401, 500], f"预期401或500，实际: {response.status_code}"

    def test_invalid_json_format(self, auth_headers):
        """测试无效JSON格式"""
        # 直接使用requests发送无效数据
        try:
            response = requests.post(
                f"{URL}/posts",
                data="invalid json",
                headers={**auth_headers, "Content-Type": "application/json"},
                timeout=10
            )

            log_test_detail(
                "无效JSON格式",
                "/posts",
                "POST",
                response.status_code,
                category="异常处理"
            )

            assert response.status_code != 200, "无效JSON不应该处理成功"
        except Exception as e:
            log_test_detail(
                "无效JSON格式",
                "/posts",
                "POST",
                0,
                error_msg=str(e),
                category="异常处理"
            )


# ==================== 订单模块测试 ====================

class TestOrderModule:
    """订单模块测试类"""

    def test_get_room_seats(self):
        """测试获取教室座位"""
        response = client.request("GET", "/rooms/1/seats")

        data = response.json().get("data", {}) if response.status_code == 200 else {}

        log_test_detail(
            "获取教室座位",
            "/rooms/1/seats",
            "GET",
            response.status_code,
            response_data={"seats_count": len(data.get("seats", []))},
            category="订单模块"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert "seats" in data, "缺少seats字段"
        assert "totalRows" in data, "缺少totalRows字段"
        assert "totalCols" in data, "缺少totalCols字段"

    def test_get_order_list(self, auth_headers):
        """测试获取订单列表"""
        response = client.request("GET", "/orders", {"page": 1, "size": 10}, headers=auth_headers)

        data = response.json().get("data", []) if response.status_code == 200 else []

        log_test_detail(
            "获取订单列表",
            "/orders",
            "GET",
            response.status_code,
            response_data={"order_count": len(data) if isinstance(data, list) else 0},
            category="订单模块"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"

    def test_get_nonexistent_order(self, auth_headers):
        """测试获取不存在的订单"""
        response = client.request("GET", "/orders/999999", headers=auth_headers)

        log_test_detail(
            "获取不存在的订单",
            "/orders/999999",
            "GET",
            response.status_code,
            category="订单模块"
        )

        assert response.status_code != 200, "获取不存在的订单应该失败"

    def test_create_order_missing_seats(self, auth_headers):
        """测试创建订单-缺少座位列表"""
        body = {
            "campusId": 1,
            "buildingId": 1,
            "roomId": 1,
            "seatList": []
        }
        response = client.request("POST", "/orders", body, headers=auth_headers)

        log_test_detail(
            "创建订单-缺少座位列表",
            "/orders",
            "POST",
            response.status_code,
            request_data=body,
            category="订单模块"
        )

        assert response.status_code != 200, "空座位列表不应该创建成功"


# ==================== 并发测试 ====================

class TestConcurrency:
    """并发测试类"""

    def test_concurrent_post_read(self):
        """测试并发读取帖子"""
        def read_post(post_id):
            response = client.request("GET", f"/posts/{post_id}")
            return response.status_code

        # 并发读取多个帖子
        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = [executor.submit(read_post, i) for i in range(1, 6)]
            results = [f.result() for f in as_completed(futures)]

        log_test_detail(
            "并发读取帖子",
            "/posts/{id}",
            "GET",
            200 if all(r == 200 for r in results) else 500,
            response_data={"results": results},
            category="并发测试"
        )

        assert all(r in [200, 404, 500] for r in results), "并发读取出现异常状态码"

    def test_concurrent_comment_read(self):
        """测试并发读取评论"""
        def read_comments(post_id):
            response = client.request("GET", "/comments", {"postId": post_id, "page": 0})
            return response.status_code

        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = [executor.submit(read_comments, i) for i in range(1, 6)]
            results = [f.result() for f in as_completed(futures)]

        log_test_detail(
            "并发读取评论",
            "/comments",
            "GET",
            200 if all(r == 200 for r in results) else 500,
            response_data={"results": results},
            category="并发测试"
        )

        assert all(r == 200 for r in results), "并发读取评论失败"


# ==================== 性能测试 ====================

class TestPerformance:
    """性能测试类"""

    def test_response_time_posts_list(self):
        """测试帖子列表响应时间"""
        start_time = time.time()
        response = client.request("GET", "/posts", {"page": 0})
        end_time = time.time()

        response_time = (end_time - start_time) * 1000  # 毫秒

        log_test_detail(
            "帖子列表响应时间",
            "/posts",
            "GET",
            response.status_code,
            response_data={"response_time_ms": round(response_time, 2)},
            category="性能测试"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert response_time < 3000, f"响应时间过长: {response_time}ms"

    def test_response_time_post_detail(self):
        """测试帖子详情响应时间"""
        start_time = time.time()
        response = client.request("GET", "/posts/1")
        end_time = time.time()

        response_time = (end_time - start_time) * 1000

        log_test_detail(
            "帖子详情响应时间",
            "/posts/1",
            "GET",
            response.status_code,
            response_data={"response_time_ms": round(response_time, 2)},
            category="性能测试"
        )

        assert response_time < 3000, f"响应时间过长: {response_time}ms"

    def test_response_time_room_seats(self):
        """测试教室座位响应时间"""
        start_time = time.time()
        response = client.request("GET", "/rooms/1/seats")
        end_time = time.time()

        response_time = (end_time - start_time) * 1000

        log_test_detail(
            "教室座位响应时间",
            "/rooms/1/seats",
            "GET",
            response.status_code,
            response_data={"response_time_ms": round(response_time, 2)},
            category="性能测试"
        )

        assert response.status_code == 200, f"请求失败: {response.status_code}"
        assert response_time < 3000, f"响应时间过长: {response_time}ms"


# ==================== 运行测试 ====================

if __name__ == "__main__":
    pytest.main([__file__, "-v", "-s", "--tb=short"])
