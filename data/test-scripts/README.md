# 管理端 Playwright 联调脚本

这些脚本用于管理端一期联调复测，不依赖前端项目依赖树。

## 前置条件

- 后端运行在 `http://localhost:8080`
- 前端运行在 `http://localhost:3000`
- MySQL 运行在 `127.0.0.1:3307`
- 管理员账号可用：`admin / 123456`
- Playwright 安装在临时 runner，例如：`C:\Users\l'l'l\.codex\tmp\pw-runner`

## 运行方式

```powershell
node D:\123\Java_items\NLstudy\data\test-scripts\admin-smoke.js
node D:\123\Java_items\NLstudy\data\test-scripts\admin-crud-apiui.js
node D:\123\Java_items\NLstudy\data\test-scripts\data-quality-audit.js
node D:\123\Java_items\NLstudy\data\test-scripts\three-end-smoke-audit.js
node D:\123\Java_items\NLstudy\data\test-scripts\three-end-flow-audit.js
node D:\123\Java_items\NLstudy\data\test-scripts\acceptance-screenshots.js
node D:\123\Java_items\NLstudy\data\test-scripts\export-current-database.js
```

脚本会优先从 `NLSTUDY_PW_RUNNER` 指定目录加载 Playwright；未指定时默认使用 `C:\Users\l'l'l\.codex\tmp\pw-runner`。

## 环境变量

- `NLSTUDY_BASE_URL`：默认 `http://localhost:3000`
- `PLAYWRIGHT_CHROME_PATH`：默认 `C:/Program Files/Google/Chrome/Application/chrome.exe`
- `NLSTUDY_TEST_OUT`：测试报告和截图输出目录
- `NLSTUDY_PW_RUNNER`：Playwright runner 目录，默认 `C:\Users\l'l'l\.codex\tmp\pw-runner`
- `NLSTUDY_DB_HOST` / `NLSTUDY_DB_PORT` / `NLSTUDY_DB_USER` / `NLSTUDY_DB_PASSWORD` / `NLSTUDY_DB_NAME`：CRUD 脚本清理本轮临时数据使用，默认连接 `127.0.0.1:3307/exam_platform`
- `NLSTUDY_AUDIT_STRICT`：设为 `1` 时，数据体检发现高风险问题会以失败退出；默认只输出报告。
- `NLSTUDY_ADMIN_USER` / `NLSTUDY_TEACHER_USER` / `NLSTUDY_STUDENT_USER`：三端巡检账号，默认 `admin` / `wanglaoshi` / `stu_001`。
- `NLSTUDY_FLOW_OUT`：三端功能闭环展示脚本输出目录。
- `NLSTUDY_SQL_OUT`：数据库导出脚本输出文件，默认 `data/exam_platform_latest_20260608.sql`。

## 数据约定

- 临时用户名前缀：`codex_`
- 临时班级/学期名前缀：`Codex`
- CRUD 脚本结束后会通过接口删除，并按本轮唯一名称做数据库兜底清理。
- `data-quality-audit.js` 只读数据库，不会清理或修改业务数据。
- `three-end-smoke-audit.js` 只做页面访问、截图和文本/API 巡检，不会执行创建、删除、提交等业务动作。

## 巡检范围

- `data-quality-audit.js`：检查乱码课程、乱码答案、答题分数超过题目满分、联调临时数据残留。
- `three-end-smoke-audit.js`：检查管理端、教师端、学生端关键页面是否白屏、是否存在学院 mock/伪数据文案、是否直出乱码、图标是否加载、是否有 API 4xx/5xx。
- `three-end-flow-audit.js`：检查管理端、教师端、学生端主要功能闭环页面是否可打开，并生成可用于教程或验收的截图。
- `acceptance-screenshots.js`：生成管理端、教师端、学生端关键验收截图。
- `export-current-database.js`：导出当前 `exam_platform` 数据库结构与数据，生成最新版 SQL 脚本。

## 教程入口

- 登录页已增加“查看系统使用教程”按钮，跳转到 `http://localhost:3000/tutorial.html`。
- 教程截图资源位于 `demo_frontend/public/tutorial-assets`。
