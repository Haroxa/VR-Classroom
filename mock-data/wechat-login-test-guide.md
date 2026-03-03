# 微信登录测试指南

## 概述

微信登录需要真实的微信环境才能获取 `loginCode` 和 `phoneCode`，无法通过普通HTTP请求直接测试。

## 登录接口说明

### 1. 微信登录（正式环境）

**接口**: `POST /api/users/login`

**请求体**:
```json
{
  "loginCode": "微信登录凭证",
  "phoneCode": "微信手机号获取凭证",
  "nickName": "用户昵称（可选）",
  "avatarUrl": "用户头像URL（可选）"
}
```

**流程**:
1. 微信小程序调用 `wx.login()` 获取 `loginCode`
2. 用户点击授权手机号按钮，获取 `phoneCode`
3. 将两个code发送到后端
4. 后端调用微信API换取 `openId` 和手机号
5. 完成登录/注册

### 2. 手机号登录（测试环境）

**接口**: `POST /api/users/login/phone`

**请求体**:
```json
{
  "phone": "13800138001"
}
```

**说明**: 这是备用的测试接口，无需微信code，直接通过手机号登录。

## 微信登录测试方案

### 方案一：使用微信开发者工具（推荐）

1. **启动项目**
   ```bash
   cd vr-classroom-backend
   ./mvnw spring-boot:run
   ```

2. **配置微信小程序**
   - 打开微信开发者工具
   - 导入小程序项目
   - 修改 `app.js` 中的API地址：
     ```javascript
     const API_BASE_URL = 'http://localhost:8080/api';
     // 或服务器地址
     // const API_BASE_URL = 'http://10.86.136.242:8082/api';
     ```

3. **修改小程序登录代码**
   ```javascript
   // pages/login/login.js
   Page({
     data: {
       loading: false
     },
     
     // 微信登录
     handleWechatLogin() {
       wx.login({
         success: (res) => {
           const loginCode = res.code;
           console.log('loginCode:', loginCode);
           
           // 获取手机号
           this.setData({ loginCode });
         }
       });
     },
     
     // 获取手机号回调
     getPhoneNumber(e) {
       const { loginCode } = this.data;
       const phoneCode = e.detail.code;
       
       if (!phoneCode) {
         wx.showToast({ title: '请授权手机号', icon: 'none' });
         return;
       }
       
       // 调用后端登录接口
       wx.request({
         url: 'http://localhost:8080/api/users/login',
         method: 'POST',
         data: {
           loginCode: loginCode,
           phoneCode: phoneCode,
           nickName: '微信用户',
           avatarUrl: ''
         },
         success: (res) => {
           if (res.statusCode === 200) {
             const { token, user } = res.data.data;
             wx.setStorageSync('token', token);
             wx.setStorageSync('user', user);
             wx.showToast({ title: '登录成功' });
             wx.navigateBack();
           } else {
             wx.showToast({ title: '登录失败', icon: 'none' });
           }
         }
       });
     }
   });
   ```

4. **测试步骤**
   - 点击微信登录按钮
   - 授权获取手机号
   - 观察后端日志输出
   - 检查数据库用户表

### 方案二：使用模拟数据测试（后端开发）

如果只需要测试后端逻辑，可以使用测试脚本：

```python
# test-wechat-login-mock.py
import requests

url = "http://localhost:8080/api"

# 模拟微信登录（需要真实的code）
def test_wechat_login():
    """
    注意：这需要真实的微信code才能测试
    可以通过以下方式获取：
    1. 微信小程序开发者工具
    2. 抓包真实小程序请求
    """
    
    # 这里需要替换为真实的code
    login_code = "YOUR_LOGIN_CODE_HERE"  # 从wx.login()获取
    phone_code = "YOUR_PHONE_CODE_HERE"  # 从getPhoneNumber回调获取
    
    body = {
        "loginCode": login_code,
        "phoneCode": phone_code,
        "nickName": "测试用户",
        "avatarUrl": "https://example.com/avatar.png"
    }
    
    response = requests.post(f"{url}/users/login", json=body)
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")

# 使用手机号登录测试（无需微信code）
def test_phone_login():
    """使用备用手机号登录接口测试"""
    response = requests.post(f"{url}/users/login/phone", json={"phone": "13800138001"})
    print(f"状态码: {response.status_code}")
    print(f"响应: {response.text}")
    
    if response.status_code == 200:
        data = response.json().get("data", {})
        token = data.get("token")
        user = data.get("user", {})
        print(f"Token: {token}")
        print(f"用户信息: {user}")
        return token
    return None

if __name__ == "__main__":
    print("=== 测试手机号登录 ===")
    test_phone_login()
    
    print("\n=== 微信登录说明 ===")
    print("微信登录需要真实的微信code，请使用微信开发者工具测试")
```

### 方案三：使用Postman/Apifox测试

1. **手机号登录测试**
   - 方法: POST
   - URL: `http://localhost:8080/api/users/login/phone`
   - Body:
     ```json
     {
       "phone": "13800138001"
     }
     ```

2. **微信登录测试**
   - 需要先从小程序获取code
   - 方法: POST
   - URL: `http://localhost:8080/api/users/login`
   - Body:
     ```json
     {
       "loginCode": "从wx.login()获取的code",
       "phoneCode": "从getPhoneNumber获取的code",
       "nickName": "测试用户",
       "avatarUrl": "https://example.com/avatar.png"
     }
     ```

## 常见问题

### 1. "获取 openId 失败"

**原因**: 
- `loginCode` 已过期（有效期5分钟）
- 微信小程序AppID/Secret配置错误

**解决**:
- 检查 `application-*.yml` 中的微信配置
- 重新获取 `loginCode`

### 2. "获取手机号失败"

**原因**:
- `phoneCode` 已过期（有效期5分钟）
- 用户未授权手机号

**解决**:
- 重新调用 `getPhoneNumber` 获取新code
- 确保用户点击了授权按钮

### 3. 微信配置检查

```yaml
# application-dev.yml
wechat:
  miniapp:
    appId: "wx_your_app_id"
    appSecret: "your_app_secret"
    accessTokenUrl: "https://api.weixin.qq.com/cgi-bin/token"
    phoneNumberUrl: "https://api.weixin.qq.com/wxa/business/getuserphonenumber"
```

## 测试检查清单

- [ ] 微信小程序配置正确
- [ ] 后端微信配置正确
- [ ] 手机号登录接口可用
- [ ] 微信登录接口能正确换取openId
- [ ] 微信登录接口能正确换取手机号
- [ ] 新用户自动创建
- [ ] 老用户正常登录
- [ ] 用户信息（昵称、头像）正确保存
- [ ] Token正确生成

## 相关文件

- 登录接口: `UserController.java`
- 微信工具: `WechatUtil.java`
- 配置文件: `application-dev.yml`
- 测试脚本: `create-test-users.py`, `test-order-api.py`
