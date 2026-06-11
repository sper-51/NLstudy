import requests

print("=== 详细错误检查 ===")

# 测试成绩列表
print("\n1. 学生成绩列表 - 完整错误:")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/grades", headers={"X-User-Id": "201"}, timeout=10)
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text}")
except Exception as e:
    print(f"   错误: {e}")

# 测试历史
print("\n2. 已完成考试历史 - 完整错误:")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/exams/history", headers={"X-User-Id": "201"}, timeout=10)
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text}")
except Exception as e:
    print(f"   错误: {e}")

print("\n=== 完成 ===")
