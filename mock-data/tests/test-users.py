# 创建测试用户脚本 - create-test-users.py

import requests
import json

# 基础URL
base_url = "http://localhost:8082/api"
server_url = "http://10.86.136.242:8082/api"
is_server = False
url = server_url if is_server else base_url

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

# 用户登录 - 使用手机号登录接口（备用测试接口）
def login_user(phone):
    """使用手机号登录接口（备用测试接口，无需微信code）"""
    response = requests.post(f"{url}/users/login/phone", json={"phone": phone})
    if response.status_code == 200:
        data = response.json().get("data", {})
        token = data.get("token")
        user = data.get("user", {})
        print(f"登录成功: {user.get('name')} (手机号: {phone})")
        return token
    else:
        print(f"登录失败: {response.status_code} - {response.text}")
        return None

# 微信登录 - 需要真实的微信code
def login_wechat(login_code, phone_code, nick_name=None, avatar_url=None):
    """
    微信登录接口（需要真实的微信code）
    
    Args:
        login_code: 微信登录凭证，用于换取openId
        phone_code: 微信手机号获取凭证，用于换取手机号
        nick_name: 用户昵称（可选）
        avatar_url: 用户头像URL（可选）
    """
    body = {
        "loginCode": login_code,
        "phoneCode": phone_code
    }
    if nick_name:
        body["nickName"] = nick_name
    if avatar_url:
        body["avatarUrl"] = avatar_url
    
    response = requests.post(f"{url}/users/login", json=body)
    if response.status_code == 200:
        data = response.json().get("data", {})
        token = data.get("token")
        user = data.get("user", {})
        print(f"微信登录成功: {user.get('name')} (手机号: {user.get('phone')})")
        return token, user
    else:
        print(f"微信登录失败: {response.status_code} - {response.text}")
        return None, None

# 用户创建
def create_user(user_data):
    response = requests.post(f"{url}/users", json=user_data)
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