import requests

# 测试后端API
print("=== 测试后端API (修复后) ===")

# 测试1: 学生待参加考试列表
print("\n1. 测试学生待参加考试列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/exams/pending", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:400]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试2: 成绩列表
print("\n2. 测试学生成绩列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/grades", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    if resp.status_code == 200:
        data = resp.json()
        if data.get("code") == 200:
            print(f"   响应: 成功, 数据条数: {len(data.get('data', []))}")
        else:
            print(f"   响应: 业务错误 - {data.get('message', '')[:200]}")
    else:
        print(f"   响应: {resp.text[:200]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试3: 已完成考试历史
print("\n3. 测试已完成考试历史 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/exams/history", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:300]}")
except Exception as e:
    print(f"   错误: {e}")

print("\n=== 测试完成 ===")
