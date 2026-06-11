# NLstudy 智考平台项目说明

## 项目简介

NLstudy 是一个基于 Spring Boot 的智能在线考试平台，面向高校课程考试、题库建设、在线测评、成绩分析和错题复习等教学场景。系统采用前后端分离架构，包含管理端、教师端、学生端三类角色，并接入独立 Python AI Agent 服务，实现教师端 AI 出题草稿生成与学生端错题 AI 辅导。

项目当前已完成核心功能闭环、本地联调、云服务器部署和三端冒烟验收。公网临时访问入口为 `http://39.96.3.61`；域名 `sper51.icu` 已解析到服务器，但因 ICP 备案未完成，当前域名访问会被阿里云拦截。

## 技术栈

### 后端

- Java 17
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8
- JWT 登录认证
- Knife4j / OpenAPI 接口文档
- systemd 云端服务托管

### 前端

- Vue 3
- Vite 5
- TypeScript
- Element Plus
- Pinia
- Vue Router
- ECharts
- Tailwind CSS
- Remix Icon

### AI Agent

- Python 3
- Flask
- PyMySQL
- DashScope 兼容 OpenAI 风格接口
- 支持普通问答、流式问答、会话管理、教师 AI 出题

### 部署

- Ubuntu 24.04
- Nginx
- MySQL
- Spring Boot Jar
- Python venv
- systemd

## 项目结构

```text
NLstudy
├─ demo_frontend                 # Vue 前端项目
├─ nlstudy                       # Spring Boot 后端项目
├─ data                          # 项目资料、SQL、测试脚本、部署记录
│  ├─ exam_platform.sql          # 当前推荐导入的数据库脚本
│  ├─ test-scripts               # 联调与验收脚本
│  ├─ PROJECT_README.md          # 本项目说明文档
│  └─ cloud_deploy_result_2026-06-09.md
└─ docs / scripts / screenshots  # 其他过程资料
```

AI Agent 独立存放在：

```text
D:\123\learn_agent
```

云端部署路径：

```text
/opt/nlstudy/frontend
/opt/nlstudy/backend
/opt/nlstudy/learn_agent
/opt/nlstudy/sql
```

## 角色与默认账号

| 角色 | 账号 | 密码 | 默认入口 |
|---|---|---|---|
| 管理员 | `admin` | `123456` | `/admin` |
| 教师 | `wanglaoshi` | `123456` | `/teacher/dashboard` |
| 学生 | `stu_001` | `123456` | `/home` |

## 核心功能

### 管理端

- 全局控制台：展示平台考试、用户、在线会话和教学统计概览。
- 用户账号管理：支持教师账号、学生账号查询、创建、编辑、启禁用、密码重置。
- 班级管理中心：支持班级维护和学生分配。
- 学期与规则配置：支持学期信息管理。
- 日志审计监控：展示登录日志、操作日志和审计数据。
- 在线会话追踪：登录写入在线会话，接口访问刷新活跃时间，超过 5 分钟无请求视为离线。
- 数据备份运维：当前以待接入提示为主，未伪装为真实功能。

### 教师端

- 教师工作台：展示课程、题库、考试和待批改概览。
- 课程教学：管理教师名下课程，查看课程学生与考试情况。
- 资源题库：管理单选、多选、判断、填空、简答等题目。
- 试卷管理：支持试卷列表、组卷中心、题目加入试卷和试卷发布。
- AI 出题：教师输入课程、章节、题型、难度、数量和要求后，AI 生成题目草稿；教师确认后再保存题库或加入试卷。
- 考务中心：支持考试安排、考试状态查看和监控入口。
- 阅卷评分：支持待阅任务、成绩复核和批改历史。
- 数据分析：展示成绩总览、分布、排名、知识点掌握度等教学分析数据。

### 学生端

- 学习中心：展示课程、考试、错题、成绩和通知概览。
- 我的课程：支持查看已加入课程，并通过课程码加入课程。
- 在线考试：支持进入考试、答题、保存进度和提交试卷。
- 成绩报告：展示得分、均分、最高分、排名、正确率、逐题回顾和成绩复核入口。
- 错题本：支持错题筛选、收藏、重练和自建题库。
- AI 错题辅导：右侧 AI 面板显示题干、选项、学生答案、正确答案和解析；支持最多 3 道错题加入对话、快捷追问、Markdown/公式/表格展示和会话管理。

## AI 接入说明

系统采用 Spring Boot 统一代理 Python Agent 的方式接入 AI 服务。

- 前端只请求 NLstudy 后端。
- 后端负责鉴权、权限校验、错题/课程/考试上下文组装和请求转发。
- Agent 负责 LLM 调用、结构化题目生成、学生问答和流式输出。

主要能力：

- 教师端：`POST /teacher/ai/questions/generate`
- 学生端：`POST /student/ai/wrongQuestions/{wrongQuestionId}/ask`
- 学生端流式问答：`POST /student/ai/wrongQuestions/{wrongQuestionId}/ask/stream`

## 本地启动

### 1. 数据库

确保 MySQL 运行在 `3307` 端口，并创建 `exam_platform` 数据库。

```powershell
mysql -h 127.0.0.1 -P 3307 -u root -p
```

```sql
DROP DATABASE IF EXISTS exam_platform;
CREATE DATABASE exam_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exam_platform;
SOURCE D:/123/Java_items/NLstudy/data/exam_platform.sql;
```

### 2. 后端

```powershell
cd D:\123\Java_items\NLstudy\nlstudy
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

### 3. AI Agent

```powershell
cd D:\123\learn_agent
python app.py
```

Agent 默认地址：

```text
http://localhost:5000
```

### 4. 前端

```powershell
cd D:\123\Java_items\NLstudy\demo_frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:3000
```

## 云端部署状态

当前云端已部署到阿里云 ECS：

```text
http://39.96.3.61
```

云端服务：

| 服务 | systemd 名称 | 监听地址 |
|---|---|---|
| Nginx | `nginx` | `0.0.0.0:80` |
| MySQL | `mysql` | `127.0.0.1:3306` |
| Spring Boot | `nlstudy-backend` | `127.0.0.1:8080` |
| Learn Agent | `nlstudy-agent` | `127.0.0.1:5000` |

常用运维命令：

```bash
systemctl status nlstudy-backend
systemctl status nlstudy-agent
systemctl status nginx
systemctl status mysql

journalctl -u nlstudy-backend -n 100 --no-pager
journalctl -u nlstudy-agent -n 100 --no-pager

systemctl restart nlstudy-backend
systemctl restart nlstudy-agent
systemctl reload nginx
```

## 构建与验收

### 后端编译

```powershell
cd D:\123\Java_items\NLstudy\nlstudy
mvn -q -DskipTests compile
```

### 后端打包

```powershell
cd D:\123\Java_items\NLstudy\nlstudy
mvn -q -DskipTests package
```

### 前端构建

```powershell
cd D:\123\Java_items\NLstudy\demo_frontend
npm run build
```

### 数据质量审计

```powershell
cd D:\123\Java_items\NLstudy
node data/test-scripts/data-quality-audit.js
```

预期结果：

```text
totalIssues = 0
```

### 三端冒烟验收

本地：

```powershell
cd D:\123\Java_items\NLstudy
node data/test-scripts/three-end-smoke-audit.js
```

公网 IP：

```powershell
cd D:\123\Java_items\NLstudy
$env:NLSTUDY_BASE_URL="http://39.96.3.61"
node data/test-scripts/three-end-smoke-audit.js
```

## 当前验收结论

- 本地前端构建通过。
- 本地后端编译和打包通过。
- 数据质量审计通过，乱码、超分、临时数据残留等检查结果为 0。
- 公网 IP 三端冒烟通过，无白屏、乱码、图标缺失、API/console 错误。
- 教师端 AI 出题接口已在公网 IP 下验证可用。
- 域名 `sper51.icu` 已解析到服务器，但受 ICP 备案限制，备案完成前建议使用公网 IP 访问。

## 仍待完善事项

以下功能不影响当前核心演示闭环，但属于后续可继续完善内容：

- 管理端批量导入用户。
- 管理端用户导出。
- 管理端数据备份与数据清理。
- 教师端题目批量导入。
- 成绩导出。
- 教师端补考快捷安排。
- Agent 生产环境可进一步从 Flask 开发服务器升级为 Gunicorn / uWSGI。
- 域名备案完成后配置 HTTPS 证书和 443 访问。

## 备案网站简介参考

```text
基于SpringBoot的智能在线考试平台，支持在线考试、题库管理、成绩分析与错题辅导。
```

