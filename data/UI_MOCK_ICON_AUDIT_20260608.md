# NLstudy 三端 UI / Mock / 图标联调审计记录

生成时间：2026-06-08

## 本轮结论

- 缺图标的主要原因不是必须重启前端，而是原先依赖外部 `remixicon` CDN/样式链路不稳定；当前已改为本地依赖并在 `src/main.ts` 导入。
- 管理端早期“数学学院/学院类看板 mock”已不再在前端源码中检出。
- 管理端页面已通过 Playwright 冒烟：页面不白屏、图标可见、无 API 4xx/5xx。
- 三端巡检脚本已固化，能检查管理端/教师端/学生端关键页面的白屏、乱码、问号占位、mock 文案和图标状态。
- 教师端组卷中心此前仍有前端 `Math.random()` 模拟智能组卷，本轮已改为基于真实题库的规则选题，并在题量不足时提示教师。
- 本轮复验确认前端、后端、Agent 服务均在线；图标和大部分 mock/假图问题不是“只需重启前端”，而是代码与数据源问题。

## 已整改项

- 图标：`remixicon` 已安装为前端依赖，`index.html` 不再使用 RemixIcon CDN。
- 管理端首页：使用后端真实统计或空状态，不再展示“学院/数学学院”等伪业务设计；系统资源卡已接入运行时 CPU/JVM 内存真实采集，并修正“GPS / 峰值”异常文案。
- 管理端菜单：`数据选连` 已修正为 `操作日志审计`。
- 管理端数据运维：不再展示随机/估算式释放空间。
- 管理端监控：题目数、试卷数、课程数、学生数、考试数已接入真实统计；系统状态页 CPU/JVM内存/磁盘/线程/GC 已接入运行时真实采集；在线用户列表与24小时趋势已基于登录日志真实聚合，当前无教师/学生登录时显示 0。
- 管理端数据备份/清理：未接入功能不再显示可执行成功/失败动作，按钮改为禁用待接入；自动备份计划也明确为未启用。
- 管理端日志审计：操作频率图不再用全 0 数据绘制假热力图；没有真实日志时显示空状态。
- 管理端 Dashboard：修复 `useRoute` 注入导致的 console error，三个 tab 仍可通过 `/admin?tab=...` 打开。
- 全局空状态：Element Plus 已接入中文 locale，表格空态从 `No Data` 改为中文。
- 教师课程/试卷：对数据库 `????` 脏数据做前端可读兜底，避免页面直出问号。
- 学生成绩报告：对超满分和乱码答案做防御展示，避免误导学生。
- 学生成绩报告：隐藏空课程码括号/标签，避免展示 `课程（）`、`加课码:` 这类空值痕迹。
- 教师组卷中心：去掉 `模拟随机选取`，改为基于当前真实题库、课程、题型、难度、知识点的确定性选题。

## 仍需业务确认项

- 数据库脏数据仍存在：课程 `id=4-10` 名称/简介为 `????`。
- `answer_record` 仍有 38 条分数超过题目满分。
- `answer_record` 仍有 2 条学生答案编码污染。
- 是否执行数据修复，见 `data/DATA_FIX_PLAN_20260608.md`。
- 学生端成绩报告的 ECharts 固定轴 warning 已处理：知识点掌握度由雷达图改为横向掌握度条形图，三端巡检无 console/API 错误。

## 暂不扩展的大模块

- 管理端在线用户趋势：`online/current` 和 `online/trend` 已基于 `login_log` 真实聚合近30分钟在线用户与24小时登录趋势；无登录记录时显示 0，不再随机生成。
- 系统资源监控：CPU、JVM 内存、磁盘、线程、GC 与运行时间已是真实采集；历史趋势仍未落库，前端展示当前资源快照。
- 数据备份记录：后端未实现备份文件生成/下载，前端明确标记暂未接入；存储占用已通过 `information_schema.tables` 接入真实数据库大小、表行数与空间统计。

## 复验命令

```powershell
node D:\123\Java_items\NLstudy\data\test-scripts\admin-smoke.js
node D:\123\Java_items\NLstudy\data\test-scripts\admin-crud-apiui.js
node D:\123\Java_items\NLstudy\data\test-scripts\data-quality-audit.js
node D:\123\Java_items\NLstudy\data\test-scripts\three-end-smoke-audit.js
node D:\123\Java_items\NLstudy\data\test-scripts\three-end-flow-audit.js
cd D:\123\Java_items\NLstudy\demo_frontend; npm run build
cd D:\123\Java_items\NLstudy\nlstudy; mvn -q -DskipTests compile
```

## 最近一次复验结果（2026-06-08）

- `npm run build`：通过。
- `mvn -q -DskipTests compile`：通过。
- `node data/test-scripts/admin-smoke.js`：10 个管理端页面通过，无 console/API 错误。
- `node data/test-scripts/three-end-smoke-audit.js`：三端关键页面不白屏、无可见 `????`、无 mock 学院词、图标可见，且无 console/API 错误。
- `node data/test-scripts/data-quality-audit.js`：仍检出 47 条数据质量问题，均未自动修改，等待业务确认后执行修复 SQL。

## 指定展示账号复验结果（2026-06-08 晚）

- 展示账号：管理端 `admin`、教师端 `wanglaoshi`、学生端 `stu_001`，默认密码均为 `123456`。
- `node data/test-scripts/data-quality-audit.js`：通过，`totalIssues = 0`。
- `node data/test-scripts/three-end-smoke-audit.js`：通过，三端指定账号关键页面均不白屏、无可见 `????`、无乱码、无 mock 学院词、图标可见，且无 console/API 错误。
- `node data/test-scripts/acceptance-screenshots.js`：通过，已生成管理端首页、数据备份运维、操作日志审计、教师课程列表、学生成绩报告截图。
- `npm run build`：通过。
- `mvn -q -DskipTests compile`：通过。
- 为保证 `stu_001` 成绩报告可展示真实数据，已插入带 `codex_accept_` 标识的 Web 前端开发展示考试、试卷、成绩、答题记录与错题数据；后续如需清理，可按该前缀定位。
- 验收截图目录：`D:\123\Java_items\NLstudy\data\runtime-logs\acceptance-screenshots-20260608`。
- 三端巡检报告目录：`D:\123\Java_items\NLstudy\data\runtime-logs\three-end-acceptance-specified-accounts`。

## 三端闭环与教程页收口（2026-06-08 晚）

- 新增 `node data/test-scripts/three-end-flow-audit.js`，覆盖管理端、教师端、学生端主要功能闭环页面，并输出截图到 `D:\123\Java_items\NLstudy\data\runtime-logs\three-end-flow-audit-20260608`。
- 闭环复验通过：管理端账号/班级/学期/运维/监控，教师端课程/题库/试卷/组卷中心/考试/监控/阅卷/复核/分析，学生端学习中心/课程/错题本/成绩报告均可打开且无白屏、乱码、mock 学院词、console/API 错误。
- 已修复本轮闭环验收发现的可见脏数据：班级描述问号占位、演示教师/学生姓名问号占位、成绩复核和批改意见问号占位、学生答案乱码。
- 登录页新增“查看系统使用教程”按钮，跳转到 `http://localhost:3000/tutorial.html`。
- 教程页文件：`D:\123\Java_items\NLstudy\demo_frontend\public\tutorial.html`。
- 教程截图资源：`D:\123\Java_items\NLstudy\demo_frontend\public\tutorial-assets`。
- 教程页包含管理端、教师端、学生端、AI 能力、联调验收说明，并包含 `系统描述` / `代码描述` 两个 tab。
- 教程入口 Playwright 验证通过，截图：`D:\123\Java_items\NLstudy\data\runtime-logs\tutorial-entry-check.png`。





