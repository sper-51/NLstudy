from playwright.sync_api import sync_playwright
import sys

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()

    # 监听console错误
    errors = []
    page.on("console", lambda msg: errors.append(msg.text) if msg.type == "error" else None)

    try:
        # 测试1: 访问前端首页
        print("测试1: 访问前端首页...")
        page.goto('http://localhost:3000')
        page.wait_for_load_state('networkidle')
        page.screenshot(path='screenshots/01_home.png')
        print("  首页加载成功")

        # 测试2: 访问登录页
        print("测试2: 访问登录页...")
        page.goto('http://localhost:3000/login')
        page.wait_for_load_state('networkidle')
        page.screenshot(path='screenshots/02_login.png')
        print("  登录页加载成功")

        # 测试3: 测试后端API连通性
        print("测试3: 测试后端API...")
        response = page.request.get("http://localhost:8080/api/v1/student/courses")
        print(f"  API响应状态: {response.status_code}")
        if response.status_code == 200:
            print(f"  API返回数据: {response.text[:200]}")
        else:
            print(f"  API错误: {response.text[:200]}")

        # 测试4: 截图报告
        print("测试4: 生成测试报告...")
        page.goto('http://localhost:3000')
        page.wait_for_load_state('networkidle')
        page.screenshot(path='screenshots/04_final.png', full_page=True)

        # 报告错误
        if errors:
            print(f"\n发现 {len(errors)} 个console错误:")
            for err in errors[:5]:
                print(f"  - {err}")
        else:
            print("\n未发现console错误")

        print("\n=== 联调测试完成 ===")

    except Exception as e:
        print(f"测试失败: {e}")
        sys.exit(1)
    finally:
        browser.close()