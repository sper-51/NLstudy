import requests

# 最终测试
print("=== 最终联调测试 ===")

tests = [
    ("学生课程列表", "http://localhost:8080/api/v1/student/courses", "/student/courses"),
    ("学生待参加考试", "http://localhost:8080/api/v1/student/exams/pending", "/student/exams/pending"),
    ("已完成考试历史", "http://localhost:8080/api/v1/student/exams/history", "/student/exams/history"),
    ("学生错题列表", "http://localhost:8080/api/v1/student/wrongQuestions", "/student/wrongQuestions"),
    ("学生成绩列表", "http://localhost:8080/api/v1/student/grades", "/student/grades"),
]

all_pass = True
for name, url, path in tests:
    try:
        resp = requests.get(url, headers={"X-User-Id": "201"}, timeout=5)
        if resp.status_code == 200:
            data = resp.json()
            if data.get("code") == 200:
                print(f"✓ {name}: 正常")
            else:
                print(f"✗ {name}: 业务错误 - {data.get('message', '')[:80]}")
                all_pass = False
        else:
            print(f"✗ {name}: HTTP {resp.status_code}")
            all_pass = False
    except Exception as e:
        print(f"✗ {name}: {str(e)[:50]}")
        all_pass = False

# 前端测试
print("\n--- 前端页面测试 ---")
try:
    resp = requests.get("http://localhost:3000", timeout=5)
    if resp.status_code == 200:
        print("✓ 前端页面: 可访问")
    else:
        print(f"✗ 前端页面: HTTP {resp.status_code}")
except Exception as e:
    print(f"✗ 前端页面: {str(e)[:50]}")

print("\n=== 测试完成 ===")
if all_pass:
    print("所有测试通过！")
else:
    print("部分测试失败，需要修复。")
