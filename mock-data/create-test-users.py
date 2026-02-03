# 创建测试用户脚本 - create-test-users.py

import requests
import json

# 基础URL
base_url = "http://localhost:8080/api"

# 测试用户数据
test_users = [
    {
        "phone": "13800138001",
        "openId": "wx_openid_test_001",
        "name": "测试用户1",
        "avatar": "assets/default_avatar.png",
        "collegeId": "1",
        "verifyStatus": 2
    },
    {
        "phone": "13800138002",
        "openId": "wx_openid_test_002",
        "name": "测试用户2",
        "avatar": "assets/default_avatar.png",
        "collegeId": "2",
        "verifyStatus": 0
    },
    {
        "phone": "13800138003",
        "openId": "wx_openid_test_003",
        "name": "测试用户3",
        "avatar": "assets/default_avatar.png",
        "collegeId": "3",
        "verifyStatus": 1
    },
]

# 用户登录
def login_user(phone):
    response = requests.post(f"{base_url}/users/login", json={"phone": phone})
    if response.status_code == 200:
        data = response.json().get("data")
        return data
    else:
        print(f"登录失败: {response.status_code} - {response.text}")
        return None

# 用户创建
def create_user(user_data):
    response = requests.post(f"{base_url}/users", json=user_data)
    if response.status_code == 200:
        print(f"用户 {user_data['name']} 创建成功")
    else:
        print(f"创建用户失败: {response.status_code} - {response.text}")

# 执行测试
if __name__ == "__main__":
    # print("=== 创建测试用户 ===")
    
    # for user in test_users:
    #     create_user(user)
    
    print("\n=== 测试用户登录 ===")
    
    for user in test_users:
        token = login_user(user['phone'])
        print(f"用户: {user['name']}")
        print(f"Token: {token}")
        print("-" * 50)
    
    print("\n用户创建和登录完成！")