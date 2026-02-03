import requests
import json

# 测试登录API
url = "http://localhost:8080/api/users/login"
headers = {"Content-Type": "application/json"}
data = {"phone": "13800138001"}

response = requests.post(url, headers=headers, json=data)

print("Status Code:", response.status_code)
print("Content Type:", response.headers.get("Content-Type"))
print("Response Content:", response.text)

# 尝试使用utf-8解码
print("\nDecoded Content:")
try:
    decoded_text = response.content.decode("utf-8")
    print(decoded_text)
    # 解析JSON
    json_data = json.loads(decoded_text)
    print("\nParsed JSON:")
    print(json.dumps(json_data, ensure_ascii=False, indent=2))
except Exception as e:
    print(f"Error decoding: {e}")