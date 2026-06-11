import requests

# 测试后端API
print("=== 测试后端API ===")

# 测试1: 学生课程列表
print("\n1. 测试学生课程列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/courses", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:300]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试2: 成绩列表
print("\n2. 测试学生成绩列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/grades", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:300]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试3: 错题列表
print("\n3. 测试学生错题列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/wrongQuestions", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:300]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试4: 考试列表
print("\n4. 测试学生考试列表 API")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/exams", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    print(f"   响应: {resp.text[:300]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试5: 前端页面
print("\n=== 测试前端页面 ===")
print("\n5. 测试前端页面")
try:
    resp = requests.get("http://localhost:3000")
    print(f"   状态码: {resp.status_code}")
    print(f"   页面标题: {resp.text[:500]}")
except Exception as e:
    print(f"   错误: {e}")

print("\n=== 测试完成 ===")
