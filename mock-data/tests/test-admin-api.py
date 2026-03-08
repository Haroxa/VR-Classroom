#!/usr/bin/env python3
"""
测试后台审核接口的脚本
"""

import requests
import json

# API基础URL
BASE_URL = "http://localhost:8081/api"
SERVER_URL = "http://10.86.136.242:8082/api"
IS_SERVER = False
URL = SERVER_URL if IS_SERVER else BASE_URL

# 测试获取帖子列表
print("=== 测试获取帖子列表 ===")
def test_get_posts():
    url = f"{URL}/admin/posts"      
    params = {
        "page": 0,
        "status": 1,  # 已发布
        # "categoryId": 1,
        # "keyword": "测试"
    }
    
    try:
        response = requests.get(url, params=params)
        response.raise_for_status()
        data = response.json()
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
        return data
    except requests.exceptions.RequestException as e:
        print(f"请求失败: {e}")
        return None

# 测试获取评论列表
print("\n=== 测试获取评论列表 ===")
def test_get_comments():
    url = f"{URL}/admin/comments"      
    params = {
        "page": 0,
        "status": 1  # 已发布
    }
    
    try:
        response = requests.get(url, params=params)
        response.raise_for_status()
        data = response.json()
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
        return data
    except requests.exceptions.RequestException as e:
        print(f"请求失败: {e}")
        return None

# 测试审核帖子
print("\n=== 测试审核帖子 ===")
def test_audit_post(post_id, status, reject_reason=None):
    url = f"{URL}/admin/posts/{post_id}"      
    data = {
        "status": status,
        "rejectReason": reject_reason
    }
    
    try:
        response = requests.patch(url, json=data)
        response.raise_for_status()
        result = response.json()
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {json.dumps(result, ensure_ascii=False, indent=2)}")
        return result
    except requests.exceptions.RequestException as e:
        print(f"请求失败: {e}")
        return None

# 测试审核评论
print("\n=== 测试审核评论 ===")
def test_audit_comment(comment_id, status, reject_reason=None):
    url = f"{URL}/admin/comments/{comment_id}"      
    data = {
        "status": status,
        "rejectReason": reject_reason
    }
    
    try:
        response = requests.patch(url, json=data)
        response.raise_for_status()
        result = response.json()
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {json.dumps(result, ensure_ascii=False, indent=2)}")
        return result
    except requests.exceptions.RequestException as e:
        print(f"请求失败: {e}")
        return None

if __name__ == "__main__":
    # 测试获取帖子列表
    posts_data = test_get_posts()
    
    # 测试获取评论列表
    comments_data = test_get_comments()
    
    # 如果有帖子数据，测试审核帖子
    if posts_data and "data" in posts_data and "records" in posts_data["data"] and len(posts_data["data"]["records"]) > 0:
        post_id = posts_data["data"]["records"][0]["id"]
        print(f"\n=== 测试审核第一个帖子 (ID: {post_id}) ===")
        test_audit_post(post_id, 1)  # 审核通过
    
    # 如果有评论数据，测试审核评论
    if comments_data and "data" in comments_data and "records" in comments_data["data"] and len(comments_data["data"]["records"]) > 0:
        comment_id = comments_data["data"]["records"][0]["id"]
        print(f"\n=== 测试审核第一个评论 (ID: {comment_id}) ===")
        test_audit_comment(comment_id, 1)  # 审核通过
