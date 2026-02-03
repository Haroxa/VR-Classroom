# 测试脚本 - test-api.py

import requests
import json

# 基础URL
base_url = "http://localhost:8080/api"

# 测试token (实际测试时需要替换为真实token)
token = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzcwMDQ1NDM5LCJleHAiOjE3NzAxMzE4Mzl9.sz9uOmRPVr8qbcjBwIt-aAp1CoAnDo8_2GouzDM50xoiJsiRas4x0zWu_chuIbEb"

# 测试结果存储
test_results = []

# 测试函数
def test_api(method, endpoint, body=None, require_auth=False):
    headers = {}
    if require_auth:
        headers["Authorization"] = token
    
    try:
        if method == "GET":
            if body:
                params = body
                response = requests.get(f"{base_url}{endpoint}", params=params, headers=headers)
            else:
                response = requests.get(f"{base_url}{endpoint}", headers=headers)
        elif method == "POST":
            response = requests.post(f"{base_url}{endpoint}", json=body, headers=headers)
        elif method == "PUT":
            response = requests.put(f"{base_url}{endpoint}", json=body, headers=headers)
        elif method == "DELETE":
            response = requests.delete(f"{base_url}{endpoint}", headers=headers)
        else:
            print(f"❌ 不支持的方法: {method}")
            return
        
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
    except Exception as e:
        result = {
            "endpoint": endpoint,
            "method": method,
            "status": "Failed",
            "error": str(e)
        }
        
        test_results.append(result)
        print(f"❌ {method} {endpoint} - Failed: {str(e)}")

# 测试 GET /api/posts
print("\n=== 测试 GET /api/posts ===")
test_api("GET", "/posts", {"page": 0})

# 测试 POST /api/posts (需要认证)
print("\n=== 测试 POST /api/posts ===")
post_body = {
    "title": "测试帖子",
    "content": "测试内容",
    "images": ["images/test.png"],
    "likeCount": 0
}
test_api("POST", "/posts", post_body, require_auth=True)

# 测试 GET /api/posts/{id}
print("\n=== 测试 GET /api/posts/{id} ===")
test_api("GET", "/posts/1")

# 测试 GET /api/comments
print("\n=== 测试 GET /api/comments ===")
comment_body = {
    "postId": "1",
    "page": 0
}
test_api("GET", "/comments", comment_body)

# 测试 POST /api/comments (需要认证)
print("\n=== 测试 POST /api/comments ===")
new_comment_body = {
    "content": "测试评论",
    "postId": 1
}
test_api("POST", "/comments", new_comment_body, require_auth=True)

# 测试 GET /api/users/posts (需要认证)
print("\n=== 测试 GET /api/users/posts ===")
user_posts_body = {
    "page": 0
}
test_api("GET", "/users/posts", user_posts_body, require_auth=True)

# 测试 GET /api/users/comments (需要认证)
print("\n=== 测试 GET /api/users/comments ===")
user_comments_body = {
    "page": 0
}
test_api("GET", "/users/comments", user_comments_body, require_auth=True)

# 测试 GET /api/oss/sign
print("\n=== 测试 GET /api/oss/sign ===")
test_api("GET", "/oss/sign",require_auth=True)

# 输出测试结果
print("\n=== 测试结果汇总 ===")
for result in test_results:
    print(f"{result['method']} {result['endpoint']} - {result['status']}")

# 统计成功和失败的测试用例
success_count = len([r for r in test_results if r['status'] == "Success"])
failed_count = len([r for r in test_results if r['status'] == "Failed"])

print(f"\n测试完成: 成功 {success_count} 个，失败 {failed_count} 个")