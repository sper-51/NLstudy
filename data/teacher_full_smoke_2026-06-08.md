# 教师端全功能冒烟测试（2026-06-08）

- Base: http://127.0.0.1:3000
- 账号: `wanglaoshi / 123456`

## 登录
- 登录后 URL: http://127.0.0.1:3000/teacher/dashboard

## 工作台
- URL: http://127.0.0.1:3000/teacher/dashboard
- H1: 教师工作台
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 教师工作台 欢迎回来，王老师老师！今天是 2026年06月08日 选择学期 王 王老师 本学期 5 管理课程数 累计 31 题库总量 0 待批改份数 进行中 2 进行中考试数 有 2 场考试正在进行中 最近一场：课程15联调考试A（ddd）· 剩余 22天12小时 进入监控 我的课程 共 5 门 创建课程 计算机网络原理 课程码： NET-WLS-2026 0 学生 0 考试 3 学分 进入课程管理 选课名单 ddd 课程码： 8FE21D07 6 学生 2 考试 3 学分 进入课程管理 选课名单 Web前端开发技术 课程码： 419AB2AE 28 学生 0 考试 3 学分 进入课程管理 选课名单 Java程序设计 课程码： JAVA2024001 28 学生 3 考试 4 学分 进入课程管理 选课名单 Python数据分析 课程码： PYTH2024001 25 学生 0 考试 2 学分 进入课程管理 选课名单
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\工作台.png
- API错误: 无
- 页面错误: 无

## 课程列表
- URL: http://127.0.0.1:3000/teacher/courses
- H1: 课程列表
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 课程列表 学生选课 资源题库 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 课程列表 查看和管理当前教师名下课程，继续进入课程详情完成联调。 筛选学期 课程总数 5 学生总数 87 考试总数 5 学分合计 15 我的课程 共 5 门 计算机网络原理 NET-WLS-2026 用于联调展示的原创计算机网络题库课程 0 名学生 0 场考试 3 学分 48 学时 进入课程管理 ddd 8FE21D07 1111 6 名学生 2 场考试 3 学分 64 学时 进入课程管理 Web前端开发技术 419AB2AE Web前端开发课程，涵盖HTML/CSS/JavaScript/Vue框架等核心技术 28 名学生 0 场考试 3 学分 64 学时 进入课程管理 Java程序设计 JAVA2024001 Java语言基础、面向对象编程、集合框架、IO与多线程网络编程 28 名学生 3 场考试 4 学分 56 学时 进入课程管理 Python数据分析 PYTH2024001 Python基础语法、NumPy/Pandas数据处理与Matplotlib数据可视化 25 名学生 0 场考试 2 学分 32 学时 进入课程管理
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\课程列表.png
- API错误: 无
- 页面错误: 无

## 课程管理15
- URL: http://127.0.0.1:3000/teacher/course/15
- H1: ddd
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 课程列表 学生选课 资源题库 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 返回工作台 / 课程详情 ddd 1111 课程代码：8FE21D07 3 学分 64 学时 授课教师： 课程加入码： 8FE21D07 复制 生成/刷新 查看二维码 选课学生 考试列表 题库统计 状态筛选 共 6 名学生 学号 姓名 加入时间 状态 操作 stu_001 张明 2026-06-05T16:37:15 已通过 查看详情 移除 stu_002 李娜 2026-06-06T17:38:25 已通过 查看详情 移除 stu_003 王强 2026-06-06T19:58:37 已通过 查看详情 移除 stu_004 赵芳 2026-06-06T19:58:37 已通过 查看详情 移除 stu_005 刘洋 2026-06-06T19:58:37 已通过 查看详情 移除 stu_006 陈静 2026-06-06T19:58:37 已通过 查看详情 移除 1
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\课程管理15.png
- API错误: 无
- 页面错误: 无

## 学生选课
- URL: http://127.0.0.1:3000/teacher/students
- H1: 学生管理
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 课程列表 学生选课 资源题库 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 学生管理 管理课程选课名单，导入/导出学生信息 共 0 名学生 计算机网络原理 导入学生 导出名单 批量移除 (0) 学号 姓名 班级 加入课程时间 状态 操作 No Data 共 0 条记录 10/page 1
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\学生选课.png
- API错误: 无
- 页面错误: 无

## 题目管理
- URL: http://127.0.0.1:3000/teacher/questions
- H1: 题目管理
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 题目管理 新增题目 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 题目管理 共 31 道题目 新增题目 题型筛选 难度筛选 所属课程 重置 题型 题目内容 难度 所属课程 分值 使用次数 状态 操作 判断 以太网交换机的每个端口通常可以隔离一个冲突域。 中等 4分 0 禁用 预览 编辑 收藏 删除 单选 DNS 的主要作用是（ ）。 中等 5分 0 禁用 预览 编辑 收藏 删除 多选 关于 TCP 三次握手，下列说法正确的有（ ）。 中等 8分 0 禁用 预览 编辑 收藏 删除 多选 下列地址范围中，属于 IPv4 私有地址范围的有（ ）。 中等 8分 0 禁用 预览 编辑 收藏 删除 多选 关于 CRC 校验，下列说法正确的有（ ）。 困难 8分 0 禁用 预览 编辑 收藏 删除 多选 关于 HTTPS，下列说法正确的有（ ）。 困难 8分 0 禁用 预览 编辑 收藏 删除 判断 IPv4 地址长度为 32 位。 简单 4分 0 禁用 预览 编辑 收藏 删除 判断 UDP 是面向连接的可靠传输协议。 简单 4分 0 禁用 预览 编辑 收藏 删除 简答 简述 TCP 和 UDP 的主要区别，并各举一个适合使用的应用场景。 中等 15分 0 禁用 预览 编辑 收藏 删除 填空 在 IPv4 局域网中，ARP 协议常用于根据 IP 地址解析对应的 ____ 地址。 困难 5分 0 禁用 预览 编辑 收藏 删除 共 10 条
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\题目管理.png
- API错误: 无
- 页面错误: 无

## 新增题目
- URL: http://127.0.0.1:3000/teacher/questions/new
- H1: 新增题目
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 题目管理 新增题目 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 新增题目 基本信息 题型 * 单选题 多选题 判断题 填空题 简答题 题目内容 * 0 / 2000 选项设置 A 正确答案 B 正确答案 C 正确答案 D 正确答案 添加选项 题目解析（可选） 0 / 2000 属性设置 难度等级 * 请选择难度 所属课程 * 请选择课程 分值 * 知识点标签 输入或选择知识点 所属章节 启用状态 启用 取消 保存草稿 创建题目
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\新增题目.png
- API错误: 无
- 页面错误: 无

## 试卷列表
- URL: http://127.0.0.1:3000/teacher/papers
- H1: 试卷管理
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 试卷列表 组卷中心 考务中心 阅卷评分 数据分析 王老师 教师 试卷管理 共 8 份试卷 新建试卷 所属课程 状态筛选 重置筛选 333 ddd 已发布 总分：30 3 题 手动组卷 预览 编辑 222 ddd 已发布 总分：2 1 题 手动组卷 预览 编辑 课程15联调试卷A ddd 已发布 总分：5 1 题 手动组卷 预览 编辑 111 ddd 已发布 总分：2 1 题 手动组卷 预览 编辑 发发发 Web前端开发技术 已发布 总分：5 1 题 手动组卷 预览 编辑 Web前端开发-期末试卷 Web前端开发技术 已发布 总分：15 3 题 手动组卷 预览 编辑 Java程序设计-期中试卷 Java程序设计 已发布 总分：100 3 题 手动组卷 预览 编辑 Python数据分析-随堂测验 Python数据分析 已发布 总分：15 3 题 手动组卷 预览 编辑
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\试卷列表.png
  - 交互 试卷预览/查看按钮: OK
- API错误: 无
- 页面错误: 无

## 组卷中心
- URL: http://127.0.0.1:3000/teacher/papers/builder
- H1: 组卷中心
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 试卷列表 组卷中心 考务中心 阅卷评分 数据分析 王老师 教师 组卷中心 智能组卷 / 手动选题，快速构建高质量试卷 保存草稿 预览试卷 发布为考试 手动选题 智能组卷 题库筛选 题型 全部题型 难度 全部难度 课程 选择课程 知识点 选择知识点 题目列表 （共 31 题） 全选当前页 单选题 困难 5 分 TCP 拥塞控制中，慢开始阶段拥塞窗口的典型变化特点是（ ）。 计算机网络原理 TCP,拥塞控制 简答题 困难 15 分 简述 HTTPS 相比 HTTP 在安全性上的改进，并说明证书的作用。 计算机网络原理 HTTPS,TLS 简答题 中等 15 分 简述 TCP 和 UDP 的主要区别，并各举一个适合使用的应用场景。 计算机网络原理 TCP,UDP 填空题 困难 5 分 在 IPv4 局域网中，ARP 协议常用于根据 IP 地址解析对应的 ____ 地址。 计算机网络原理 ARP,MAC地址 填空题 中等 5 分 DNS 中用于表示 IPv6 地址解析结果的资源记录类型是 ____。 计算机网络原理 DNS,IPv6 填空题 中等 5 分 HTTP 服务默认使用的 TCP 端口号是 ____。 计算机网络原理 端口号,HTTP 填空题 简单 5 分 IPv4 地址 192.168.1.10 中，第一个十进制字段是 ____。 计算机网络原理 IP地址 判断题 中等 4 分 DNS 只能把域名解析为 IPv4 
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\组卷中心.png
- API错误: 无
- 页面错误: 无

## 考试安排
- URL: http://127.0.0.1:3000/teacher/exam-schedule
- H1: 考试安排
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 考试安排 考场监控 阅卷评分 数据分析 王老师 教师 考试安排 管理所有考试的发布、监控与安排 发布新考试 全部 (9) 待发布 (0) 进行中 (2) 已结束 (6) 222 已结束 2026-06-07T00:00:00 ~ 2026-06-08T09:53:12 时长 120 分钟 2 应考人数 2 已交卷 0 未交卷 交卷进度 100% 编辑 安排补考 课程15联调考试A 进行中 进行中 2026-06-06T00:00:00 ~ 2026-06-30T23:59:59 时长 30 分钟 8 应考人数 8 已交卷 0 未交卷 交卷进度 100% 编辑 查看监控 结束并结算 Java程序设计-期中考试 已结束 2026-06-05T20:00:00 ~ 2026-06-05T21:30:00 时长 90 分钟 12 应考人数 12 已交卷 0 未交卷 交卷进度 100% 编辑 安排补考 111 进行中 进行中 2026-06-05T00:00:00 ~ 2026-06-30T00:00:00 时长 120 分钟 5 应考人数 5 已交卷 0 未交卷 交卷进度 100% 编辑 查看监控 结束并结算 111 已结束 2026-06-01T00:00:00 ~ 2026-06-08T11:40:05 时长 120 分钟 2 应考人数 2 已交卷 0 未交卷 交卷进度 100% 编辑 安排补考 Java程序设
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\考试安排.png
- API错误: 无
- 页面错误: 无

## 考场监控
- URL: http://127.0.0.1:3000/teacher/monitor
- H1: 考试监控列表
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 考试安排 考场监控 阅卷评分 数据分析 王老师 教师 考试监控列表 实时监控所有考试的进行状态 2 进行中 2 今日结束 全部状态 课程15联调考试A 进行中 剩余 540小时9分钟 2026-06-06 00:00 ~ 2026-06-30 23:59 应考 8 人 已交卷 8 人 参与率 100.0% 进入监控 111 进行中 剩余 516小时9分钟 2026-06-05 00:00 ~ 2026-06-30 00:00 应考 5 人 已交卷 5 人 参与率 100.0% 进入监控 222 已结束 2026-06-07 00:00 ~ 2026-06-08 09:53 应考 2 人 已交卷 2 人 参与率 100.0% 查看详情 Java程序设计-期中考试 已结束 2026-06-05 20:00 ~ 2026-06-05 21:30 应考 12 人 已交卷 12 人 参与率 100.0% 查看详情 111 已结束 2026-06-01 00:00 ~ 2026-06-08 11:40 应考 2 人 已交卷 2 人 参与率 100.0% 查看详情 Java程序设计-第二次单元测验 已结束 2026-04-15 14:00 ~ 2026-04-15 15:20 应考 12 人 已交卷 12 人 参与率 100.0% 查看详情 Java程序设计-第一次单元测验 已结束 2026-03-10 14:00 ~ 
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\考场监控.png
  - 交互 进入监控按钮: OK
- API错误: 无
- 页面错误: 无

## 实时监控6
- URL: http://127.0.0.1:3000/teacher/exam/6/monitor
- H1: 考场监控
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 数据分析 王老师 教师 返回 考场监控 实时监控学生考试状态与异常行为 自动刷新中（每10秒） 结束考试 立即刷新 Python数据分析-单元测验 Python数据分析 2026-02-10T19:00:00 ~ 2026-02-10T20:00:00 剩余时间 00:00:00 参与率 61% 学生状态总览 33 在线人数 100% 20 已提交 61% 13 答题中 39% 0 未开始 0% 5 异常 15% 异常告警 李娜（401） 频繁切屏 杨磊（406） 频繁切屏 郑丽（409） 频繁切屏 学生详情列表 状态筛选 学生姓名 学号 状态 进入时间 答题进度 切屏次数 最后活跃 操作 张三 306 答题中 19:05:00 1/3 0 117天前 李四 307 答题中 19:05:00 1/3 0 117天前 张明 400 答题中 19:00:00 6/13 0 117天前 李娜 401 异常 19:00:00 11/16 5 117天前 王强 402 已提交 19:00:00 5/12 2 2天前 赵芳 403 已提交 19:00:00 13/16 1 2天前 刘洋 404 答题中 19:00:00 8/23 4 117天前 陈静 405 已提交 19:00:00 10/18 1 2天前 杨磊 406 异常 19:00:00 5/20 5 117天前 周婷 407 答题中 19:00:00
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\实时监控6.png
- API错误: 无
- 页面错误: 无

## 待阅任务
- URL: http://127.0.0.1:3000/teacher/grading
- H1: 待阅任务
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 待阅任务 成绩复核 批改历史 数据分析 王老师 教师 待阅任务 批改主观题与编程题，高效完成阅卷工作 未批阅 已批阅 全部 待批改总数0 今日已完成0 按课程筛选 按考试筛选 按题型筛选 批改队列 （0 条待处理） 暂无待批改任务 所有任务已完成 选择一道题目开始批改 从左侧队列中选择待批改的任务
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\待阅任务.png
  - 交互 切换已批阅: OK
  - 交互 查看已批阅记录: OK
  - 已批阅摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 待阅任务 成绩复核 批改历史 数据分析 王老师 教师 待阅任务 批改主观题与编程题，高效完成阅卷工作 未批阅 已批阅 全部 已批改数1 今日已完成0 按课程筛选 按考试筛选 按题型筛选 已批阅记录 （1 条记录） 王 王小明 Python数据分析-单元测验 简答题 第 3 题 · 简答题 · 20 分 请简述 ArrayList 和 LinkedList 的主要区别。 批改结果 18/20 查看记录 批改工作区 王小明 | Python数据分析-单元测验 | 第 3 题 ← 上一题 → 下一题 Ctrl+S 提交 题目内容 简答题 满分：20 分 请简述 ArrayList 和 LinkedList 的主要区别。 学生答案 Ar
- API错误: 无
- 页面错误: 无

## 成绩复核
- URL: http://127.0.0.1:3000/teacher/appeals
- H1: 成绩复核
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 待阅任务 成绩复核 批改历史 数据分析 王老师 教师 成绩复核 处理学生提交的成绩复核申请 全部状态 4 待处理 5 已通过 2 已驳回 张 张明 待处理 400 · 222 2026-06-08 11:20:04 处理复核 申请原因 自动化联调复核申请 2026-06-08T03-20-04-305Z：核查得分与参考答案匹配情况。 张 张明 待处理 400 · Python数据分析-单元测验 2026-06-08 11:17:12 处理复核 申请题目 Java源文件编译后会生成对应的.class文件。 申请原因 自动化联调复核申请 2026-06-08T03-17-12-734Z：核查本题得分与参考答案匹配情况。 张 张三 待处理 306 · Java程序设计-期中考试 2026-06-08 10:34:08 处理复核 申请题目 请简述 ArrayList 和 LinkedList 的主要区别。 申请原因 主观题要点基本覆盖，建议复核是否可补给过程分。 李 李四 待处理 307 · Java程序设计-期中考试 2026-06-08 10:26:08 处理复核 申请题目 请简述 ArrayList 和 LinkedList 的主要区别。 申请原因 总分与答题详情显示不一致，请老师核对本题得分。 张 张明 已通过 400 · 课程15联调考试A 2026-06-08 11:43:43 申请原因 我想
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\成绩复核.png
  - 交互 打开复核处理弹窗: OK
  - 弹窗摘要: 处理成绩复核 张 张明·222 申请原因 自动化联调复核申请 2026-06-08T03-20-04-305Z：核查得分与参考答案匹配情况。 原始答卷 学生作答： (无作答内容) 参考答案： (暂无参考答案) 原总分 分 新总分 教师意见 / 评语 取消 驳回 通过
- API错误: 无
- 页面错误: 无

## 批改历史
- URL: http://127.0.0.1:3000/teacher/grading/history
- H1: 批改历史
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 待阅任务 成绩复核 批改历史 数据分析 王老师 教师 批改历史 查看所有批改记录，追溯批改详情 今日批改17 本周批改17 平均时长- 按考试筛选 至 批改教师 查询 重置 学生姓名 考试名称 题目类型 得分 评语摘要 批改时间 批改教师 操作 王小明 Python数据分析-单元测验 简答题 18 / 20 测试批改通过 2026-06-08T09:51:30 王老师 李四 Web前端开发-期末考试 主观题 1 / 10 2026-06-08T08:44:01 王老师 张三 Web前端开发-期末考试 主观题 1 / 10 2026-06-05T15:31:45 王老师 郑丽 - 单选题 0 / 5 [???????????] 2026-06-05T15:20:03 王老师 吴鹏 - 单选题 0 / 5 [???????????] 2026-06-05T15:20:03 王老师 周婷 - 单选题 0 / 5 [???????????] 2026-06-05T15:20:03 王老师 杨磊 - 单选题 0 / 5 [???????????] 2026-06-05T15:20:03 王老师 陈静 - 单选题 0 / 5 [???????????] 2026-06-05T15:20:03 王老师 刘洋 - 单选题 8 / 5 2026-06-05T15:20:03 王老师 赵芳 - 单选题 6 / 5 2
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\批改历史.png
- API错误: 无
- 页面错误: 无

## 成绩总览
- URL: http://127.0.0.1:3000/teacher/analytics
- H1: 成绩分析
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 数据分析 成绩总览 质量看板 王老师 教师 成绩分析 多维度数据分析，全面掌握考试情况 Java程序设计 Java程序设计-期中考试 按班级筛选 按分数段筛选 平均分 74 较上次 +0 最高分 95 张三 最低分 54 周婷 及格率 83.3% 10/12 人及格 成绩分布 班级成绩对比 知识点掌握度 题型得分率 成绩排名 Top 10 Bottom 5 排名 学号 姓名 班级 得分 排名变化 🥇 306 张三 - 95 - 🥈 403 赵芳 - 92 - 🥉 307 李四 - 88 - 4 404 刘洋 - 83 - 5 408 吴鹏 - 79 - 6 400 张明 - 76 - 7 405 陈静 - 71 - 8 406 杨磊 - 67 - 9 401 李娜 - 64 - 10 409 郑丽 - 61 -
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\成绩总览.png
- API错误: 无
- 页面错误: 无

## 质量看板
- URL: http://127.0.0.1:3000/teacher/analytics/dashboard
- H1: 数据看板
- 摘要: 智考平台 教师端 主菜单 工作台 课程教学 资源题库 试卷管理 考务中心 阅卷评分 数据分析 成绩总览 质量看板 王老师 教师 数据看板 Java程序设计-期中考试 Java程序设计-期中考试 自动刷新 更新于 11:50:48 参与率 100% 平均分 74 较上次 +0 及格率 83.3% 10/12 人及格 优秀率 16.7% 2 人达到优秀 成绩分布 班级对比 知识点正确率 成绩趋势（多场考试对比） 题目难度分析
- 截图: D:\123\Java_items\NLstudy\screenshots\teacher_full_smoke_2026-06-08\质量看板.png
- API错误: 无
- 页面错误: 无

## 汇总
- API 4xx/5xx 数量: 0
- 页面 JS 错误数量: 0
## 日志摘录
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/questions?page=1&pageSize=10
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/questions/knowledge-points
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/papers?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/papers/20/preview
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/questions/knowledge-points
- api:200 http://127.0.0.1:3000/api/v1/teacher/questions/for-paper?pageSize=200
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams?pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams?page=1&pageSize=1000
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams/8/detail
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams/8/monitor
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams/6/detail
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams/6/monitor
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/grading/tasks?pageSize=100&status=pending
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?page=1&pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams?page=1&pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/grading/tasks?pageSize=100&status=completed
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/grading/reviews?page=1&pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/grading/history?pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:304 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/courses?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/statistics
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/statistics
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/classComparison?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/distribution?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/classComparison?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/distribution?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/knowledge/exam/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/knowledge/exam/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/difficulty-analysis/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/ranking?page=1&pageSize=50
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/difficulty-analysis/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/ranking?page=1&pageSize=50
- debug: [vite] connecting...
- debug: [vite] connected.
- api:200 http://127.0.0.1:3000/src/api/student.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/src/api/auth.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/src/api/request.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/src/api/teacher.ts?t=1780890511080
- api:200 http://127.0.0.1:3000/api/v1/teacher/exams?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/statistics
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/classComparison?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/exam/5/statistics
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/classComparison?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/knowledge/exam/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/trend/12
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/knowledge/exam/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/trend/12
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/difficulty-analysis/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/distribution?examId=5
- api:200 http://127.0.0.1:3000/api/v1/teacher/analytics/difficulty-analysis/5
- api:200 http://127.0.0.1:3000/api/v1/teacher/grades/distribution?examId=5