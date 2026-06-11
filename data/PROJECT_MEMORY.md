# NLstudy 项目记忆（Codex 接手用）

> 生成时间：2026-06-06
> 作用：作为后续联调、修复、开发时的统一项目记忆，减少重复理解成本。

## 1. 项目定位

这是一个**前后端分离的智能在线考试平台**，面向教育场景，包含三类角色：
- 管理员
- 教师
- 学生

核心业务围绕：
- 课程管理
- 在线考试
- 题库/试卷管理
- 成绩与分析
- 错题复习
- 成绩复核

当前阶段重点是：**前后端联调与缺口补齐**，不是从零搭建。

---

## 2. 仓库结构记忆

项目根目录：`D:\123\Java_items\NLstudy`

主要目录：
- `D:\123\Java_items\NLstudy\nlstudy`：Spring Boot 后端
- `D:\123\Java_items\NLstudy\demo_frontend`：Vue 3 前端
- `D:\123\Java_items\NLstudy\data`：项目资料、SQL、联调文档、进度报告
- `D:\123\Java_items\NLstudy\scripts`：辅助脚本
- `D:\123\Java_items\NLstudy\screenshots`：截图类资料

注意：当前没有发现 `AGENTS.md`，所以以用户要求和 `data` 文档为主。

---

## 3. 后端架构理解

后端目录：`D:\123\Java_items\NLstudy\nlstudy`

技术栈：
- Spring Boot 3.2.0
- Spring MVC
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JDK 17+
- Maven

典型分层：
- `controller`：接口入口
- `service` / `service.impl`：业务逻辑
- `mapper`：数据库访问
- `entity`：实体模型
- `common`：统一返回等公共类
- `config` / `filter` / `interceptor` / `util`：基础设施

已确认的关键实现：
- 统一响应类：`D:\123\Java_items\NLstudy\nlstudy\src\main\java\com\nl\nlstudy\common\Result.java`
- 登录拦截器：`D:\123\Java_items\NLstudy\nlstudy\src\main\java\com\nl\nlstudy\interceptor\JwtInterceptor.java`
- 配置文件：`D:\123\Java_items\NLstudy\nlstudy\src\main\resources\application.yml`

### 后端的重要行为记忆

1. **接口返回统一使用 `Result<T>`**
   - 成功：`code = 200`
   - 失败：使用 `Result.error(...)`

2. **认证机制处于“JWT + 临时头传用户ID”并存阶段**
   - 正式路径支持 `Authorization: Bearer <token>`
   - 联调阶段允许使用 `X-User-Id` 透传用户ID
   - 没有 token 时，只要 `X-User-Id` 存在，请求仍可能放行

3. **逻辑删除已启用**
   - MyBatis-Plus 全局配置中设置了 `deleted` 字段
   - 表设计和实体字段要注意是否包含 `deleted`

4. **后端目前并非全部真实实现完成**
   - 一部分接口是真实可用
   - 一部分接口是 stub、硬编码或空结果
   - 联调时必须区分“前端问题”还是“后端尚未实现”

---

## 4. 前端架构理解

前端目录：`D:\123\Java_items\NLstudy\demo_frontend`

技术栈：
- Vue 3
- Vue Router 4
- Pinia
- Axios
- Element Plus
- ECharts
- Vite 5
- TypeScript
- Tailwind CSS（已装配）

关键文件：
- 路由：`D:\123\Java_items\NLstudy\demo_frontend\src\router\index.ts`
- 当前打开布局：`D:\123\Java_items\NLstudy\demo_frontend\src\layouts\ExamLayout.vue`
- API 目录：`D:\123\Java_items\NLstudy\demo_frontend\src\api`

### 前端路由/布局记忆

系统按角色拆分布局：
- `StudentLayout.vue`
- `TeacherLayout.vue`
- `AdminLayout.vue`
- `ExamLayout.vue`

角色页面总体分布：
- 学生端：课程、考试、报告、错题本
- 教师端：工作台、课程详情、题库、试卷、排考、监考、批改、分析、复核
- 管理端：学期、用户、班级、日志、监控、数据管理

### 前端工作方式记忆

1. 前端不是纯静态 demo，已经接入较多真实 API。
2. 当前联调重点主要在教师端和管理端部分页面。
3. 前端有些页面之前用过静态数据，现阶段正在逐步替换为真实接口。
4. 修复时要优先保证：
   - API 路径一致
   - 参数字段一致
   - 返回结构一致
   - 空数据场景可正常渲染

---

## 5. 联调现状记忆

依据 `data/联调方案.md`、`data/前后端联调深度调研报告.md`、`data/项目开发进度报告.md`：

### 已基本打通的模块

学生端多数核心查询接口已可用：
- 课程
- 待参加考试/历史考试
- 错题列表
- 成绩列表

教师端多数基础管理接口已可用：
- 课程管理
- 题库管理
- 试卷管理
- 成绩管理
- 待批改 / 批改历史 / 复核列表（接口可通，但可能空数据）

管理员端部分接口已可用：
- 用户管理
- 班级管理

### 明确未完成/半完成项

重点缺口包括：
- 管理员系统配置：`/api/v1/admin/settings` 未实现
- 教师考场监控：后端为 stub，返回空数组
- 教师知识点分析：后端硬编码假数据
- 教师成绩统计/排名：部分接口仍需真实统计逻辑

### 联调判断原则

如果页面异常，优先按以下顺序排查：
1. 前端是否调错接口路径
2. 前端字段映射是否与后端返回不一致
3. 请求头是否带了 `Authorization` 或 `X-User-Id`
4. 数据库是否有对应测试数据
5. 后端是否只是 stub / 假数据 / 未实现

---

## 6. 数据与环境记忆

### 启动端口
- 后端：`8080`
- 前端：`3000`
- MySQL：文档中当前联调环境强调使用 `3307`（Docker 映射）

### 数据库相关文件
- 初始化脚本：`D:\123\Java_items\NLstudy\data\exam_platform.sql`
- 当前备份：`D:\123\Java_items\NLstudy\data\current_database_backup.sql`
- 其他数据：`D:\123\Java_items\NLstudy\data\wanglaoshi_data.sql`

### 重要环境注意点

1. `application.yml` 的数据库端口曾修正为 `3307`，联调时不要回退成 `3306`。
2. 许多接口是否“正常”，高度依赖测试数据是否已导入。
3. 数据库结构近期有过补字段操作，尤其是：
   - `exam_record`
   - `grade`
4. 处理数据库相关 bug 时，先核对：
   - 表结构
   - 实体字段
   - MyBatis-Plus 注解映射
   - SQL 脚本和当前库是否一致

---

## 7. 已知特殊要求

这是接手后必须长期遵守的项目约束：

1. **先看 `data/` 文档再动代码**
   - 该目录是项目事实来源之一
   - 特别是联调方案、进度报告、需求文档、API文档

2. **当前工作重点是联调，不是大重构**
   - 以最小改动修复问题
   - 优先打通链路和保证页面可用
   - 避免无关重构

3. **修 bug 时先判断责任边界**
   - 前端问题 / 后端问题 / 数据问题 / 文档问题
   - 不要误把 stub 当成前端 bug

4. **接口兼容性优先**
   - 尽量以现有接口约定为准
   - 若要改接口，必须同步评估前端调用处

5. **空数据必须友好处理**
   - 联调阶段数据库并不总是完备
   - 页面不能因为空数组/空对象直接报错

6. **保留临时联调机制意识**
   - 当前 `X-User-Id` 仍在使用
   - 修改鉴权逻辑时要避免把联调链路直接切断

---

## 8. 当前可优先关注的风险点

后续工作中，优先关注这些高频问题：

### 后端侧
- 实体字段与数据库列不匹配
- MySQL 保留字字段映射问题（例如 `rank`）
- 统计类接口返回硬编码数据
- 监控/复核/分析接口只有壳子没有真实逻辑

### 前端侧
- API 返回字段名和页面展示字段不一致
- 某些页面历史上存在重复函数、静态数据残留
- 角色布局和路由跳转的鉴权/入口逻辑问题
- 图表页对空数据、假数据、异常数据的兼容性

### 联调侧
- 请求头缺失导致 401
- 前端代理或 baseURL 配置不匹配
- 本地数据库内容与文档示例不一致

---

## 9. 接手后的默认工作方式

后续我在这个项目中会默认按下面方式执行：

1. 先看 `data` 文档和相关代码，再判断问题归属。
2. 改动尽量小，优先修复联调阻塞点。
3. 如涉及接口，尽量同时核对：
   - 前端 API 封装
   - 路由页面使用点
   - 后端 Controller / Service / Mapper
   - 数据库结构与样例数据
4. 每次修复后，尽量给出：
   - 改了什么
   - 为什么改
   - 是否属于真实实现 / 临时兼容 / 待后续完善

---

## 10. 后续建议优先阅读顺序

每次继续工作时，优先参考：
1. `D:\123\Java_items\NLstudy\data\README.md`
2. `D:\123\Java_items\NLstudy\data\联调方案.md`
3. `D:\123\Java_items\NLstudy\data\项目开发进度报告.md`
4. `D:\123\Java_items\NLstudy\data\api开发文档.md`
5. 具体业务需求再看：`D:\123\Java_items\NLstudy\data\基于SpringBoot的智能在线考试平台.md`

---

## 11. 一句话记忆

这是一个**正在联调阶段的前后端分离在线考试平台**；当前最重要的是：**基于 `data/` 文档、以最小改动快速定位并打通前后端链路，同时清楚区分真实 bug 与后端未实现项。**

## 12. 2026-06-06 补充记忆

### 本次运行态确认
- 前端可正常启动在 `3000`
- 后端可正常启动在 `8080`
- 数据库使用 `3307`
- `mvnw.cmd` 当前不稳定，必要时需绕过 wrapper 启动

### 新确认的问题
- 数据库存在非 `utf8mb4` 历史数据，部分中文已变成 `????`
- `course_selection`、`wrong_question` 等表结构与部分脚本/逻辑删除假设不一致
- `admin/settings` 确认未实现，不是单纯前端 bug
- 登录问题不只是“401 失败”，还可能出现登录成功但用户信息异常的情况
- 功能完成度不能只看接口是否存在，必须按真实业务闭环评估

### 后续默认策略
- 联调前先做账号、数据、表结构基线巡检
- 将问题明确区分为：数据问题 / 前端问题 / 后端问题 / 未实现项
- 优先解决会影响大面积联调判断的基础问题：账号、编码、缺接口、关键闭环缺失

## 13. 后端启动问题经验（2026-06-06）

### 一、这次遇到过的真实启动问题

#### 1. `mvnw.cmd` 不可靠
现象：
- 直接执行 `./mvnw.cmd spring-boot:run` 失败
- 报错表现不是业务代码异常，而是 wrapper 启动链异常，例如：
  - `Cannot start maven from wrapper`
  - PowerShell 内部脚本解析失败

结论：
- 本项目当前环境下，**不要默认信任 Maven Wrapper**
- 联调时优先使用本机 Maven + 自定义 settings

#### 2. Maven 全局仓库路径权限/配置污染
现象：
- 直接 `mvn spring-boot:run` 时，Maven 尝试写入全局仓库：
  - `D:\maven\apache-maven-3.9.11\mvn_repo`
- 出现拒绝访问、tracking file 写入失败等问题

结论：
- 当前机器上的 Maven 安装/仓库存在环境污染
- 不适合直接依赖默认全局仓库

#### 3. `8080` 端口可能被其他进程占用，且容易误判为后端已启动
现象：
- 有一度 `8080` 被 `powershell.exe` 占用
- 表面看端口已监听，但实际上不是当前 Spring Boot 服务
- 这会导致：
  - 访问接口得到“静态资源不存在”类错误
  - 误以为是后端 Controller 路由问题

结论：
- 判断后端是否真的起来，**不能只看 8080 被监听**
- 还要看：
  1. 监听进程是不是 `java`
  2. `/v3/api-docs` 是否返回真正的 OpenAPI JSON
  3. 目标接口是否命中当前代码版本

#### 4. BOM / 源码编码会直接导致后端编译失败
现象：
- 某些 Java 文件因 UTF-8 BOM 产生：
  - `非法字符: '\ufeff'`
- 编译直接失败

结论：
- Windows 下改写 Java 文件时，必须注意：
  - 使用 **UTF-8 无 BOM**
- 否则很容易出现“代码逻辑没错但项目根本起不来”的情况

#### 5. 运行态与代码版本可能不一致
现象：
- 某些时候代码已经改了，但 8080 上跑的并不是最新编译后的服务
- 原因可能包括：
  - 旧 Java 进程未停干净
  - 启动了错误进程
  - 端口被别的进程占用

结论：
- 每次关键修复后，要做“干净重启 + 关键接口回归”
- 不要只依据“看起来启动了”做判断

---

### 二、本项目后端推荐启动方式

当前项目最稳妥的启动方式：

1. 在目录：
   - `D:\123\Java_items\NLstudy\nlstudy`
2. 使用自定义 Maven settings：
   - `.codex-maven-settings.xml`
3. 启动命令：
   - `mvn -s .codex-maven-settings.xml spring-boot:run`

说明：
- 这个 settings 会把本地仓库切到工作区，绕过全局 Maven 仓库污染
- 当前比 `mvnw.cmd` 更稳定

---

### 三、后端启动后的标准验证动作

每次后端启动后，建议固定做以下 4 步验证：

#### 验证 1：确认 8080 监听者是 Java
- 查看端口监听进程
- 确认不是 `powershell.exe` 等其他进程

#### 验证 2：访问 OpenAPI 文档
- `http://127.0.0.1:8080/v3/api-docs`
- 如果能返回 OpenAPI JSON，说明 Spring Web 路由已正常注册

#### 验证 3：验证认证接口
- `POST /api/v1/auth/login`
- 这是最基础的业务入口

#### 验证 4：验证一个管理端或学生端核心接口
例如：
- `/api/v1/admin/users`
- `/api/v1/student/courses`

只有 1~4 都通过，才能认为“后端真的处于可联调状态”。

---

### 四、后端启动问题的经验结论

一句话总结：

**本项目后端的启动问题，不只是“代码能不能编译”，更是“本机 Maven 环境、端口占用、文件编码、运行中进程一致性”四类问题叠加的结果。**

因此后续默认策略应当是：
1. 优先使用自定义 Maven settings 启动
2. 修改 Java 文件时强制使用 UTF-8 无 BOM
3. 每次重启前尽量清理旧 Java 进程
4. 启动后用 OpenAPI + 登录接口做双重验活
5. 遇到接口异常，先排除“跑错进程/旧进程占端口”，再怀疑业务代码

## 14. 页面级联调收尾记忆（2026-06-06）
- 错题页已对齐关键字段：`wrongQuestionId`、`wrongCount`、`examName`、`courseName`
- 成绩详情已从假数据改为真实数据，图表依赖字段 `knowledgePoints` / `historyTrend` 已补齐
- 新样本建议优先使用：
  - 题目 `51`
  - 试卷 `18`
  - 考试 `8`
- 旧样本（如考试 `7`）仍属于脏数据参考，不建议继续作为主联调基线

## 2026-06-06 补充记忆：教师侧边栏与数据分析
- 教师侧边栏曾存在路由语义冲突：/teacher 被同时用作“工作台”和“课程列表”，导致左侧高亮状态不稳定、看起来像多处选中。当前修复策略：
  - “工作台”固定使用 /teacher/dashboard
  - “课程列表”使用 /teacher/courses，并兼容匹配 /teacher、/teacher/course/*
- 教师数据分析页存在前后端分桶映射不一致：
  - 后端成绩分布桶：-59、60-69、70-79、80-89、90-100
  - 前端曾错误按 -60、60-70、70-80、80-90 取值，导致图表读不到真实数据
- wanglaoshi 展示基线已补充：
  - 课程 15
  - 联调试卷 18
  - 联调考试 8
  - 已为学生 400~406 补齐选课、exam_student、exam_record、answer_record、grade
  - 考试 8 当前适合验证：排名、分布、知识点正确率、提交率
- 当前分析页数据质量说明：
  - AnalyticsOverview.vue 已能吃到考试 8 的真实统计/排名/知识点数据
  - AnalyticsDashboard.vue 仍有部分图表使用兜底假数据（尤其趋势/难度），后续应继续接真实接口
- 联调经验：当浏览器报页面空白但接口 seemingly 正常时，优先检查：
  1. 路由激活逻辑是否存在复用路径
  2. 前端是否按真实后端 key 读取统计分桶
  3. 最近一场考试是否实际上没有足够展示数据

## 2026-06-06 补充记忆：wanglaoshi 数据分析展示基线（正式样本）
- 为了让教师端数据分析页面更适合展示，新增了 wanglaoshi 的“正式展示样本考试”基线：
  - 考试：examId=5 Java程序设计-期中考试
  - 课程：courseId=12
  - 试卷：examPaperId=13
- 当前考试 5 已被重建为干净样本：
  - 样本学生：306,307,400,401,402,403,404,405,406,407,408,409
  - 总分：100
  - 通过线：60
  - 分布：90-100=2, 80-89=2, 70-79=3, 60-69=3, -59=2
  - 统计：平均分 74.0，及格率 83.3%
- 当前建议的分析展示优先顺序：
  1. examId=5：更适合成绩分析/质量看板展示
  2. examId=8：更适合联调闭环验证（少量、干净、可控）
- 前端分析页已做默认选择优化：优先选“有提交记录且总分 >= 60”的考试，避免默认落到 5 分联调样本。

## 2026-06-06 补充记忆：分析展示班级样本
- 为了后续把教师端“班级对比”做成真实图表，已为课程 12 的展示样本学生补充 student_class 映射。
- 新增展示班级：
  - 901 计科2401班
  - 902 计科2402班
  - 903 软工2401班
  - 904 软工2402班
- 当前课程 12 / 考试 5 的 12 名样本学生已分配到上述四个班级，后续可直接用于：
  - 班级平均分对比
  - 班级最高分对比
  - 多场考试趋势按班级展示

## 2026-06-06 补充记忆：质量看板班级对比已真实化
- 后端 GradeController.classComparison 已从“按考试ID假分组”修正为“按 student_class + sys_class 真实班级分组”。
- 当前 examId=5 的班级对比已可直接用于教师端质量看板：
  - 计科2401班：平均分 83.33，最高分 95
  - 计科2402班：平均分 64.67，最高分 76
  - 软工2401班：平均分 76.33，最高分 92
  - 软工2402班：平均分 71.67，最高分 83
- 当前教师端数据分析模块真实化进度：
  - 成绩分布：真实
  - 排名：真实
  - 知识点掌握：真实
  - 班级对比：真实
  - 趋势：仍主要为前端兜底数据
  - 难度分析：仍主要为前端兜底数据

## 2026-06-06 补充记忆：质量看板趋势图已真实化
- 已为课程 12 补充两场历史展示考试：
  - examId=14 Java程序设计-第一次单元测验
  - examId=15 Java程序设计-第二次单元测验
- 后端 TeacherAnalyticsController.examTrend 已改为真实数据：
  - 按课程读取历史考试
  - 按 student_class + sys_class 聚合每个班级在各场考试中的平均分
- 当前课程 12 的趋势数据可用于质量看板折线图：
  - 计科2401班：60.3 → 72.3 → 83.3
  - 计科2402班：43.0 → 54.3 → 64.7
  - 软工2401班：53.3 → 65.7 → 76.3
  - 软工2402班：48.7 → 60.7 → 71.7
- 当前教师端质量看板真实化进度：
  - 成绩分布：真实
  - 班级对比：真实
  - 知识点掌握：真实
  - 趋势图：真实
  - 难度分析：待继续真实化

## 2026-06-06 补充记忆：质量看板难度分析已接真实数据
- 后端新增接口：/api/v1/teacher/analytics/difficulty-analysis/{examId}
- 当前实现方式：
  - 读取 exam_paper_question 获取每题满分
  - 读取 answer_record 获取每题实际得分
  - 按题型聚合真实得分率 ctualRate
  - 按题型给出展示用参考值 expectedRate
- 当前 examId=5 的真实难度分析结果：
  - 选择题：题量 3，平均得分率 74.0%，参考预期 85.0%
- 由于课程 12 当前展示试卷只有 3 道单选题，因此难度图当前只会有一个真实题型柱；这属于样本结构限制，不是接口错误。
- 如果后续希望难度分析图更丰富，需要为展示样本考试补充更多题型（判断、填空、简答等）。

## 2026-06-06 补充记忆：examId=5 已升级为多题型展示样本
- 为了让教师端质量看板的难度分析更丰富，已将课程 12 的展示试卷 examPaperId=13 扩展为 6 题结构：
  - 43 选择题（20分）
  - 44 选择题（20分）
  - 45 选择题（20分）
  - 46 判断题（10分）
  - 47 填空题（10分）
  - 48 简答题（20分）
- 已重建 examId=5 的样本答题记录与成绩，使 12 名学生在 6 道题上都有真实 answer_record。
- 当前真实难度分析结果：
  - 选择题：3题，得分率 69.0%
  - 判断题：1题，得分率 74.2%
  - 填空题：1题，得分率 65.8%
  - 简答题：1题，得分率 85.4%
- 这意味着教师端质量看板当前已经具备较完整的展示闭环：
  - 分布、班级、知识点、趋势、难度 都有真实数据支撑。

  - ?????


  - `D:\123\Java_items\NLstudy\data\teacher_pages_check.txt`
  - `D:\123\Java_items\NLstudy\screenshots\teacher_analytics.png`
  - `D:\123\Java_items\NLstudy\screenshots\teacher_analytics_dashboard.png`
  - `/teacher/dashboard`
  - `/teacher/analytics`
  - `/teacher/analytics/dashboard`
  - `/teacher/course/15`
- ???? API ????
  - ?? `wanglaoshi` ????? 15 ? 7 ???


  - `Java????-????` ?? `76?`
  - `??15????A` ?? `0?`
  - ?????`stu_001 / 123456`
  - ?????`wrongQuestionId=2`


  - `http://127.0.0.1:3000/report/99`
  - ???`Java????`
  - ???`Java????-????`
  - ???`76`
  - ?????`65`


## 2026-06-07 ???????????????
- ?????????? `utf8mb4`????????????????????????????? `?` ?????
- ?????????? `examId=5` ?? 3 ????
  - `question.id=46`
  - `question.id=47`
  - `question.id=48`
- ???????? `gradeId=99` ???????????
  - ?????
  - ?????
  - ?????
  - ???????
  - ?????????
- ???????????????????????????? `studentAnswer` ????????????????????????????????
- ??????????????????
  1. ??????question ??
  2. ???????answer_record / grade detail ???


## 2026-06-07 ????????????
- ??????? 1????????? 7 ??????`question.id=33~39`?
- ??????? `gradeId=99` ????????
  - ???`#6`
  - ????`58.3`
  - ????????????????????
- ??????????????????????? `examStatus` / `status` / `submitStatus` ?????
- ????????????????????????????????????????


## 2026-06-07 ???????????????????
- ????????????`cd D:\123\Java_items\NLstudy\nlstudy && mvn -s .codex-maven-settings.xml spring-boot:run`
- ???????????`D:\123\Java_items\NLstudy\data\backend_run.log`
- ?????????????????????????????
  - ???`/api/v1/student/exams/pending`
  - ???`D:\123\Java_items\NLstudy\nlstudy\src\main\java\com\nl\nlstudy\controller\StudentExamController.java:164`
  - ??????????????????????????? `completed`
- ??????
  - ????????????????????? pending ??
  - ????? `ongoing` ??????????????? pending ??
  - ???????? `submitted/auto_submitted` ?? `allowTimes`?????? pending ??
- ??? `stu_001` ??????????????????????
  - `GET http://127.0.0.1:8080/api/v1/student/exams/pending`
  - Header: `X-User-Id: 400`
  - ?????`data: []`
- ??????????????`D:\123\Java_items\NLstudy\data\student_pages_check.js`
- ?????????
  - ?? `0`
  - ??????????????
  - ??????????????????????????
- ?????????????
  - ?????? Maven ????? Spring Boot ??????????????
  - ??? `Win32_Process` ?? `java.exe` ? `CommandLine` ????? `spring-boot:run` ????


## 2026-06-07 ??????????????????
- ?????????????`/teacher/dashboard`
- ?????
  - `D:\123\Java_items\NLstudy\demo_frontend\src\views\auth\Login.vue`
  - `D:\123\Java_items\NLstudy\demo_frontend\src\router\index.ts`
  - `D:\123\Java_items\NLstudy\demo_frontend\src\views\teacher\CourseDetail.vue`
- ???????????? `TeacherRootAlias`??? `/teacher` ? `/teacher/dashboard` ????????????????
- `TeacherCourseList.vue` ?????????????????????????
- ????? `????` ????????????
  - ?????? `getStudentRanking` ????????????
  - ??? `examId=5` ? Top 10 ???? `?? / ?? / ?? / ?? / ?? / ??` ?????
- ???????????
  - `D:\123\Java_items\NLstudy\data\teacher_pages_check.txt`
  - `D:\123\Java_items\NLstudy\data\student_pages_check.txt`


## 2026-06-07 ?????????????
- ??????????`D:\123\Java_items\NLstudy\data\teacher_special_pages_check.js`
- ????????????
  - `/teacher/exam-schedule`
  - `/teacher/monitor`
  - `/teacher/grading`
  - `/teacher/grading/history`
  - `/teacher/appeals`
- ?????
  - ???????????????
  - ???????????????????????
  - `wrongQuestionId=2` ?????????? `correctAnswer = A`
- ????? `GradingHistory.vue` ??????
  - `default-sort` ?????????????
  - `el-pagination` ? `small` ??? `size="small"`
- ????????????????????????????
  - ??????????????????
  - ????????/?????????????????????


## 2026-06-07 ???????????????
- ??????????????????????
  - ??? `wanglaoshi (103)` ?????????????? `grading_task` ??????
- ?????
  - ??????? `8` ??? `3` ?
  - ????????????????????????
- ???? `grade_review.id=9` ????????????????????????
- ???????????
  - ????????
  - ?????? `????`?????????????/???????????????????

## 2026-06-08 考务中心/监控/成绩闭环修复记录

### 本轮定位
- 考场监控“暂无逐题详情”的根因不是前端弹窗，而是学生端 submit/save 只更新 exam_record，没有把每题写入 answer_record；教师端只能看到记录，无法看到逐题答案。
- ExamRecordController 曾按 AnswerRecord.sortOrder 数据库字段排序，但该字段是 @TableField(exist=false)，会导致查询异常；已改为根据 exam_paper_question.sort_order 内存排序。
- 考务中心状态像 mock 的关键原因：部分操作只强制交卷记录，没有把考试本身落为 finished；前端“结束并结算”现在调用 /teacher/exams/{examId}/finish。
- 成绩链路此前不完整：学生提交后未稳定 upsert grade，主观题批改后也不会回写 answer_record/exam_record/grade。

### 已完成修复
- StudentExamController：保存进度与提交试卷都会 upsert answer_record；客观题自动评分，essay/code 生成 grading_task，并 upsert grade。
- ExamController：监控数据补充 id/recordId/examRecordId；结束考试会结算所有记录，并保留待阅任务导致的 grade.status=pending。
- ExamRecordController：记录详情返回逐题内容、类型、标准答案、解析、满分，并按试卷题序排序。
- GradingTaskController：待阅列表只保留真正需要人工批改的 essay/code；批改完成后回写答案得分、重算考试记录总分、更新成绩状态为 published。
- ExamSchedule.vue：考试安排页“强制交卷”调整为“结束并结算”，刷新后状态应真实持久化。

### 已验证链路
- 后端编译：nlstudy 下 ./mvnw.cmd -q -DskipTests compile 通过。
- 真实接口验证：学生 201 对考试 6 创建记录 233 并提交 3 题；接口生成 answer_record 明细与 grading_task。
- 教师 103 批改任务 28 后，记录 233 从 objective=10, subjective=0, total=10, grade=pending 更新为 objective=10, subjective=18, total=28, grade=published。
- 注意：PowerShell 直接构造中文 JSON 时可能出现 ???，浏览器/前端正常 UTF-8 请求一般不会这样；若用脚本压测，需显式 UTF-8 body。

### 后续注意
- 历史数据中存在乱码答案和旧脏 grading_task（客观题任务），本轮接口已避免新数据继续产生；旧数据如要演示干净，需要单独清洗。
- 前端 npm run build 仍有大量既有 TypeScript 类型错误，非本轮新引入；本轮只确认后端编译和关键接口闭环。


## 2026-06-08 wanglaoshi ?????????????

- ??????????????/???????????????????????????????????
- ?? `wanglaoshi (103)` ??/?????`course.id=17`???? `???????`???? `NET-WLS-2026`?
- ??????? `20` ?????? `question.source=net_seed_20260608`?
  - ??? 6 ?
  - ??? 4 ??? 40 ??????
  - ??? 4 ?
  - ??? 4 ?
  - ??? 2 ?
- ?????`data/import_original_network_questions_utf8.py`????????? source ?????????? UTF-8 ???????????
- ?????? PowerShell here-string ??? Python ?????? `????` ??????????????? `apply_patch` ? UTF-8 ????????????
- ?????`GET /api/v1/teacher/questions?pageSize=5&courseId=17`?`GET /api/v1/teacher/courses?pageSize=50` ???????????


## 2026-06-08 ???????????

### ????
1. ??????? `wanglaoshi (103)` ????? `grade` ??????????????????
2. ????????????????????????????????????????????????
3. ???? `AnalyticsOverview.vue`??????? `AnalyticsDashboard.vue`????????????????????? fallback?
4. ????????????????????????????????????????

### ??
- ?????????????????????? `???????`??????????? 15 ??? `222`?????????? 2 ? 0 ???????? 0?
- ?????????????????? A + ?? B ????
- ????????????????`classComparison` ???????????? `cd.classes || cd.data`?`distribution` ????????????????
- ???????? mock fallback???????? 0???????????

### ???
- `ExamController` ?????? `avgScore/maxScore/minScore`???????????????
- `GradeController.statistics` ?? `topStudent/bottomStudent/excellentCount/excellentRate`????? `passScore` ??????
- `GradeController.distribution/classComparison` ?? `courseId` ???
- `AnalyticsOverview.vue`???????????????????????? 0?????? 60 ????????? key?????/??????????? mock ?? fallback?
- `AnalyticsDashboard.vue`????????????????/????????? mock fallback????????/0??

### ????
- ???????`nlstudy` ? `./mvnw.cmd -q -DskipTests compile`?
- ??????`GET /api/v1/teacher/exams?pageSize=20` ??????? `avgScore/maxScore/minScore`?
- ??????? `Java????-???? (examId=5)`??????????? `74.0`???? `95.0`???? `54.0`???? `83.3%`???? `16.7%`?
- ??????????????????????????

## 2026-06-08 补充记忆：教师数据分析模块 Mock 审计

- 新增审计文档：`D:\123\Java_items\NLstudy\data\analytics_mock_audit_2026-06-08.md`。
- 当前结论：教师端数据分析不是全量 mock，但仍有“前端硬编码兜底 + 后端固定参考线 + 统计口径不清”三类问题。
- 已确认仍需整改的前端硬编码点：
  - `AnalyticsDashboard.vue` 成绩分布饼图空数据时兜底为固定人数：3/7/15/12/8。
  - `AnalyticsOverview.vue` 知识点雷达空数据时兜底为固定知识点与固定掌握度。
  - `AnalyticsOverview.vue` 题型得分率空数据时兜底为固定题型与固定得分率。
- 已确认后端半真实点：
  - `TeacherAnalyticsController.difficultyAnalysis` 的 `actualRate` 来自真实答题记录，但 `expectedRate` 仍按题型固定映射。
  - `TeacherAnalyticsController.knowledgeMastery` 当前按课程全量题目统计，未按当前考试；`knowledge_points` 未拆分多知识点；主要按 `isCorrect` 算正确率，主观题部分得分不够准确。
  - `TeacherAnalyticsController.examTrend` 当前返回原始均分；不同满分考试混在一起时应改为返回并展示 `avgRate` 百分比，同时 tooltip 保留平均分/满分。
- 样本接口验证：
  - `GET /api/v1/teacher/grades/distribution?examId=5` 返回真实分布：60-69=3、70-79=3、80-89=2、90-100=2、0-59=2。
  - `GET /api/v1/teacher/analytics/trend/12` 返回真实班级趋势。
  - `GET /api/v1/teacher/analytics/difficulty-analysis/5` 返回真实 actualRate，但 expectedRate 固定。
- 推荐整改顺序：
  1. 删除前端硬编码兜底样例，改为暂无数据。
  2. `AnalyticsOverview.vue` 接入真实知识点与题型得分率。
  3. 新增考试级知识点掌握度接口，按试卷题目与答题得分统计。
  4. 趋势接口补 `totalScore/avgRate/submittedCount`，前端默认画百分比。
  5. 难度参考线按题目 difficulty 加权，或前端隐藏参考线。

## 2026-06-08 补充记忆：教师数据分析真实化修复完成

- 后端新增接口：`GET /api/v1/teacher/analytics/knowledge/exam/{examId}`。
  - 只统计当前考试试卷题目与本场考试答题记录。
  - 支持拆分 `knowledge_points` 中的 `,，;；/、` 等多知识点。
  - 按 `answer_record.score / exam_paper_question.score` 计算掌握度，兼容主观题部分得分。
- 后端趋势接口 `/teacher/analytics/trend/{courseId}` 已补充：
  - `totalScore`
  - `avgRate`
  - `submittedCount`
  - 保留 `avgScore`
- 后端难度分析 `/teacher/analytics/difficulty-analysis/{examId}` 已将 `expectedRate` 从题型固定值改为按题目 `difficulty` 加权参考值：
  - easy/simple/简单：85
  - medium/normal/中等/普通：70
  - hard/difficult/困难：55
- 前端 `AnalyticsOverview.vue` 已去掉知识点雷达和题型得分率固定 mock 兜底：
  - 知识点改调考试级接口。
  - 题型得分率改用难度分析的真实 `actualRate`。
  - 无数据时显示“暂无知识点数据/暂无题型数据”。
- 前端 `AnalyticsDashboard.vue` 已去掉成绩分布饼图固定人数兜底：
  - 空数据显示“暂无成绩分布数据”。
  - 知识点改调考试级接口。
  - 趋势图改画 `avgRate` 百分比，tooltip 展示均分/满分/提交人数。
  - 难度图图例从“预计得分率”改为“参考得分率”。
- 已验证：
  - `D:\123\Java_items\NLstudy\nlstudy` 下 `.\mvnw.cmd -q -DskipTests compile` 通过。
  - 后端已重启并加载新代码，8080 监听 PID 曾为 60000。
  - `examId=5` 新知识点接口返回集合框架 76.3、Java基础 71.4、数据类型 70.0、变量定义 65.8。
  - `courseId=12` 趋势接口返回 `avgRate/totalScore/submittedCount`。
  - `examId=5` 难度接口参考线已变为按难度加权：选择题 75、判断题 85、填空题 70、简答题 55。

## 2026-06-08 补充记忆：阅卷评分/成绩复核联调

- 本轮定位：
  - 待阅任务页的“已批阅”tab 返回的是 `status=completed` 的真实已批改任务，但前端卡片仍显示“批改进度 0/1”和“开始批改”，视觉上像仍待批阅。
  - `/teacher/grading/tasks` 不传 status 时后端默认只返回 `pending/grading`，导致前端“全部”tab 不是全部；已改为支持 `status=all` 返回 `pending/grading/completed`。
  - 成绩复核通过时原逻辑只更新 `grade_review`，未回写 `grade/exam_record/answer_record`；已补回写链路。
- 已完成修复：
  - `GradingTaskController.taskList` 支持 `status=all`，并给任务返回 `gradeTime`。
  - `Grading.vue` 已批阅记录改为只读查看：按钮显示“查看记录”，进度条按得分率显示绿色，右侧在“全部”tab 点到 completed 任务也不能再提交。
  - `GradingTaskController.handleReview` / `handleReviewCompat` 在 approved + newScore 时回写成绩。
  - 题目级复核中 `newScore` 现在按“该题新分”处理：用新题分与 `answer_record.score` 差值修正总分和主观分；整卷复核仍按新总分处理。
  - `Appeals.vue` 题目级复核弹窗文案改为“原本题分/新本题分”，并优先使用后端返回的 `questionScore`。
- 新增/清洗复核测试数据：
  - 新脚本：`D:\123\Java_items\NLstudy\data\seed_grade_review_samples_utf8.py`。
  - 为 `wanglaoshi (103)` 可见的 `Java程序设计-期中考试 (examId=5)` 增加 4 条复核样本：
    - `id=10` pending，张三，主观题要点补分申请。
    - `id=11` pending，李四，分数显示不一致申请。
    - `id=12` rejected，张明，客观题复核驳回。
    - `id=13` approved，李娜，简答题补分通过。
- 已验证：
  - 后端编译通过：`.\mvnw.cmd -q -DskipTests compile`。
  - 后端已重启，8080 监听 PID 曾为 55996。
  - `GET /api/v1/teacher/grading/tasks?status=pending&pageSize=100` 返回 0 条，不混入 completed。
  - `GET /api/v1/teacher/grading/tasks?status=completed&pageSize=100` 返回 completed 任务并包含 `score/comment/gradeTime`。
  - `GET /api/v1/teacher/grading/tasks?status=all&pageSize=100` 可返回 completed 任务。
  - `GET /api/v1/teacher/grading/reviews?pageSize=100` 返回 pending/rejected/approved 多状态样本，中文正常，题目级样本带 `questionScore`。

## 2026-06-08 补充记忆：教师端最终冒烟与学生端启动联调

- 使用 Playwright/Edge 新增教师端全量冒烟脚本：
  - `D:\123\Java_items\NLstudy\data\teacher_full_smoke_2026-06-08.js`
  - 报告：`D:\123\Java_items\NLstudy\data\teacher_full_smoke_2026-06-08.md`
  - 截图：`D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08`
- 教师端覆盖页面：
  - 工作台、课程列表、课程管理、学生选课、题目管理、新增题目、试卷列表、组卷中心、考试安排、考场监控、实时监控、待阅任务、成绩复核、批改历史、成绩总览、质量看板。
- 教师端最终结果：
  - API 4xx/5xx：0
  - 页面 JS error：0
  - 原监控页 `ElProgress percentage=null` warning 已通过 `safePercent` 清除。
  - 批改历史后端已从实体直返改为 enriched 返回，前端可显示学生姓名、考试名、题型、满分、批改教师。
  - Element Plus 旧 `small` 分页 prop 已替换为 `size="small"`。
- 学生端新增冒烟脚本：
  - `D:\123\Java_items\NLstudy\data\student_full_smoke_2026-06-08.js`
  - 报告：`D:\123\Java_items\NLstudy\data\student_full_smoke_2026-06-08.md`
  - 截图：`D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08`
- 学生端基础覆盖：
  - 学习中心、我的课程、课程详情、成绩报告、指定成绩报告、错题本。
- 学生端已修复：
  - `/student/exams/pending` 原来会返回已有提交记录，只是标成 completed，导致首页待考区出现“已完成 + 进入考试”；已改为只返回未参加或 ongoing 可继续考试，已提交进入 history。
  - 学生首页顶部“待考”统计改为使用 `pendingExams.value.length`，与待参加考试列表一致。
- 学生端当前基础冒烟结果：
  - API 4xx/5xx：0
  - 页面 JS error：0
  - 仅剩 ECharts ticks 可读性 warning，属于图表库提示，不影响功能。
- 下一步建议：
  - 做学生端深链路：开始考试 -> 保存进度 -> 提交 -> 教师监控/阅卷/成绩报告/错题本联动。

## 2026-06-08 补充记忆：学生端成绩复核与闭环联调

- 新增学生端整体闭环方案：`D:\123\Java_items\NLstudy\data\student_closed_loop_plan_2026-06-08.md`。
- 新增学生复核闭环脚本：`D:\123\Java_items\NLstudy\data\student_review_loop_2026-06-08.js`。
- 新增学生复核闭环报告：`D:\123\Java_items\NLstudy\data\student_review_loop_2026-06-08.md`。
- 截图目录：`D:\123\Java_items\NLstudy\screenshots\student_review_loop_2026-06-08`。
- 本轮修复：
  - `StudentGradeController.detail` 现在返回 `courseId/reviews/reviewStatus`，逐题返回 `reviewStatus/reviewId`。
  - `StudentGradeController.detail/applyReview` 已校验当前登录学生只能查看/复核自己的成绩，避免跨学生旧数据污染。
  - `StudentGradeController.applyReview` 现在按“整卷/单题”分别判断重复复核，pending/approved 阻止重复提交。
  - `Report.vue` 新增整卷复核入口、逐题复核入口、复核原因弹窗、状态徽标与提交后刷新。
  - `Report.vue` 修复 `/report/:id` 先选默认课程导致指定成绩详情空白的问题，改为 `gradeId` 优先加载。
  - `Report.vue` 空知识点雷达图显示“暂无知识点数据”，避免 ECharts 空 indicator 的 JS error。
- 已验证：
  - 后端 `D:\123\Java_items\NLstudy\nlstudy` 执行 `.\\mvnw.cmd -q -DskipTests compile` 通过（实际命令为 `./mvnw.cmd -q -DskipTests compile`）。
  - 后端已重启，8080 当前监听 PID 曾为 `58900`。
  - 学生端复核闭环脚本最终结果：API 4xx/5xx=0，JS Error=0。
  - 当前 `stu_001(id=400)` 的 `gradeId=112` 已有整卷复核 pending 样本，可在教师端复核模块继续处理。
- 注意：
  - 旧记忆里的 `stu_001 userId=201` 已过期；当前登录接口返回 `stu_001 id=400`。
  - 数据库中仍有旧学生 `201/202/203/204` 与部分乱码数据，联调脚本必须动态读取当前登录用户成绩，避免硬编码旧 `gradeId`。
  - `npm run build` 仍有大量既有 TS 类型错误，不代表本轮新增功能失败；本轮通过 Playwright 与后端编译验证。


## 2026-06-08 补充记忆：学生成绩误跳/登出感知与复核闭环修复

- 用户反馈：学生前端查看成绩后报错，登录状态像被清除；学生端找不到成绩复核按钮。
- 根因定位：
  - `/student/exams/history` 只返回 `recordId/examId`，不返回真实 `gradeId`。
  - 学生首页已完成考试用 `exam.gradeId || exam.id` 拼 `/report/:id`，当 `gradeId` 缺失时会把 `examId/recordId` 当成绩 ID。
  - 上轮新增成绩归属校验后，误跳到非本人/不存在成绩会产生业务错误；用户体验像“查看成绩后异常/状态丢失”。
  - 学生侧还有硬编码 `/report/1`，课程详情的考试回顾也曾用 `exam.id` 而非 `gradeId`。
- 本轮修复：
  - `StudentExamController.historyExams` 为历史考试返回 `gradeId/gradeStatus/publishTime`。
  - `StudentCourseController.getDetail` 为课程考试列表返回 `gradeId/gradeStatus`，并展示 `published/ongoing/finished` 考试，避免 finished 考试从课程详情消失。
  - `Home.vue` 已完成考试卡片改为仅用真实 `gradeId` 跳 `/report/{gradeId}`；无成绩时显示“成绩生成中”。
  - `Home.vue` 已完成考试卡片新增“申请复核”入口，跳 `/report/{gradeId}?review=1`。
  - `CourseDetail.vue` 考试回顾改用真实 `gradeId`，新增“申请复核”入口；最近成绩报告不再硬编码 `/report/1`。
  - `StudentLayout.vue` 成绩报告菜单改为 `/report`，不再硬编码 `/report/1`。
  - `Report.vue` 支持 `?review=1` 自动打开整卷复核弹窗。
  - `StudentExamController.submitExam` 交卷返回 `gradeId/gradeStatus`。
  - `Exam.vue` 学生交卷成功后若返回 `gradeId`，直接跳转 `/report/{gradeId}`，学生可立刻查看成绩与申请复核。
- 验证：
  - 后端编译通过：`D:\123\Java_items\NLstudy\nlstudy` 下 `./mvnw.cmd -q -DskipTests compile`。
  - 后端已重启，8080 当前监听 PID 曾为 `12932`。
  - 同意闭环脚本：`D:\123\Java_items\NLstudy\data\student_teacher_review_loop_2026-06-08.js`。
  - 同意闭环报告：`D:\123\Java_items\NLstudy\data\student_teacher_review_loop_2026-06-08.md`。
  - 驳回闭环报告：`D:\123\Java_items\NLstudy\data\teacher_finish_reject_review_loop_2026-06-08.md`。
  - 同意链路结果：学生查看 `/report/114` token 不丢失；学生申请复核 `reviewId=17`；教师 approve 后总分 `0 -> 1`，学生页显示“已通过”，API 4xx/5xx=0，JS Error=0。
  - 驳回链路结果：教师结束 `examId=16` 成功；历史考试返回 `gradeId=113`；学生申请复核 `reviewId=18`；教师 reject 后成绩保持 `0.0`，`reviewStatus=rejected`。
  - 课程详情接口 `/student/courses/15` 已确认每个完成考试带 `gradeId`。
  - 全项目学生侧已确认无残留 `/report/1`。

## 2026-06-08 补充记忆：数据莫名归零/需重登问题修复

- 用户反馈：测试过程中教师端数据会莫名变成 0，需要退出重新登录才恢复。
- 定位结论：
  - 后端教师接口真实有数据：`/teacher/courses?pageSize=100` 返回 5 门课程，`/teacher/questions?pageSize=1` 返回题库总量 31，`/teacher/exams?status=ongoing&pageSize=10` 返回 2 场进行中考试。
  - 归零主要风险来自前端身份缓存不一致：跨学生/教师账号测试时，`localStorage.userInfo.id` 与 `localStorage.userId` 可能短暂不一致，接口请求头 `X-User-Id` 使用旧值时会按错误用户查数据。
  - Dashboard 题库统计表达式存在优先级问题：`a || b || Array.isArray(qRes) ? ... : 0` 会把分页对象错误折算成列表长度/兜底值，造成统计不稳定。
- 本轮修复：
  - `userStore.setUserInfo` 统一写入 `userInfo/userId/role`；非教师登录会清空 `teacherInfo`。
  - 新增 `userStore.clearSession()`，登录前先清理旧会话，避免跨角色残留。
  - `request.ts` 请求拦截器根据 `userInfo.id` 兜底修正 `localStorage.userId`，并同步 `role`。
  - `Dashboard.vue` 新增 `unwrapList/unwrapTotal`，统一兼容数组、`{list,pagination}`、`{data:{list,pagination}}`、`records` 等返回格式。
  - `Dashboard.vue` 修正题库总量、待批改数、进行中考试列表解析，不再因分页对象解包不一致显示 0。
- 验证：
  - 新脚本：`D:\123\Java_items\NLstudy\data\identity_switch_probe_2026-06-08.js`，正常学生 -> 教师切换后，工作台显示课程 5、题库 50/后续真实为 31、进行中 2，token/userId 正确。
  - 新脚本：`D:\123\Java_items\NLstudy\data\teacher_dashboard_polluted_identity_check_2026-06-08.js`，故意设置 `userId=400` 且 `userInfo=王老师(103)`，进入教师工作台后请求拦截器自动修正 `userId=103`，工作台显示课程 5、题库 31、进行中 2。
  - 后端编译通过：`./mvnw.cmd -q -DskipTests compile`。
  - 教师端全量冒烟重跑：`D:\123\Java_items\NLstudy\data\teacher_full_smoke_2026-06-08.js`，结果 API 4xx/5xx=0，页面 JS Error=0。
