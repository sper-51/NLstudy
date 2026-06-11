import requests

# 测试grade表结构
print("=== 检查数据库表结构 ===")

# 测试exam_record表的列
print("\n1. 测试exam_record表 (通过history查询)")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/exams/history", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    if resp.status_code == 200:
        data = resp.json()
        print(f"   响应: {data.get('message', '')[:300]}")
except Exception as e:
    print(f"   错误: {e}")

# 测试grade表
print("\n2. 测试grade表 (通过grades查询)")
try:
    resp = requests.get("http://localhost:8080/api/v1/student/grades", headers={"X-User-Id": "201"})
    print(f"   状态码: {resp.status_code}")
    if resp.status_code == 200:
        data = resp.json()
        print(f"   响应: {data.get('message', '')[:300]}")
except Exception as e:
    print(f"   错误: {e}")

print("\n=== 检查完成 ===")
