# 智能在线考试平台 API开发文档

## 技术栈说明

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **核心框架** | SpringBoot 3.x | 快速构建Web应用，自动配置 |
| **Web框架** | SpringMVC | 处理HTTP请求，RESTful风格 |
| **持久层** | MyBatis + MyBatisPlus | 数据库操作，支持代码生成 |
| **数据库** | MySQL 8.0 | 关系型数据库，UTF-8编码 |
| **构建工具** | Maven | 项目依赖管理和构建 |
| **安全技术** | Spring Security | 用户认证与授权（JWT） |

### 前端技术栈

| 技术 | 说明 |
|------|------|
| **页面结构** | HTML5 + CSS3 |
| **脚本语言** | JavaScript (ES6+) |
| **前端框架** | Vue.js / React（可选） |
| **可视化组件** | Echarts（数据图表展示） |
| **模板引擎** | Thymeleaf（可选） |

### 开发工具与环境

| 工具 | 说明 |
|------|------|
| **开发工具** | IntelliJ IDEA / VS Code |
| **版本控制** | Git + Gitee |
| **服务器** | Tomcat（内嵌于SpringBoot） |
| **JDK版本** | JDK 17 或更高 |
| **AI辅助** | Vibe Coding + 通义灵码 |

---

## 一、接口规范说明

### 1.1 基本约定

| 项目 | 说明 |
|------|------|
| **基础路径** | `/api/v1` |
| **数据格式** | JSON |
| **字符编码** | UTF-8 |
| **认证方式** | JWT Token（在请求头中携带） |
| **签名机制** | 无（使用HTTPS） |

### 1.2 请求头说明

| 请求头 | 必填 | 说明 |
|--------|------|------|
| `Content-Type` | 是 | `application/json` |
| `Authorization` | 是 | `Bearer {token}` |

### 1.3 响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

### 1.4 响应状态码

| 状态码 | 说明 |
|--------|------|
| `200` | 请求成功 |
| `400` | 请求参数错误 |
| `401` | 未登录或token过期 |
| `403` | 无权限访问 |
| `404` | 资源不存在 |
| `500` | 服务器内部错误 |

### 1.5 分页参数

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | int | 页码（从1开始） |
| `pageSize` | int | 每页条数 |

---

## 二、认证模块 API

### 2.1 用户登录

**请求**
```
POST /api/v1/auth/login
```

**参数**
```json
{
    "username": "admin",
    "password": "admin123",
    "captcha": "abc123"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名/学号/工号 |
| password | string | 是 | 密码 |
| captcha | string | 否 | 图形验证码 |

**响应**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "userInfo": {
            "id": 1,
            "username": "admin",
            "realName": "系统管理员",
            "role": "admin",
            "avatar": "https://..."
        }
    }
}
```

---

### 2.2 用户退出

**请求**
```
POST /api/v1/auth/logout
```

**响应**
```json
{
    "code": 200,
    "message": "退出成功",
    "data": null
}
```

---

### 2.3 获取当前用户信息

**请求**
```
GET /api/v1/auth/currentUser
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "role": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "avatar": "https://..."
    }
}
```

---

### 2.4 修改密码

**请求**
```
PUT /api/v1/auth/password
```

**参数**
```json
{
    "oldPassword": "old123",
    "newPassword": "new123"
}
```

**响应**
```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": null
}
```

---

### 2.5 忘记密码

**请求**
```
POST /api/v1/auth/forgotPassword
```

**参数**
```json
{
    "username": "student001",
    "email": "student@example.com"
}
```

**响应**
```json
{
    "code": 200,
    "message": "重置链接已发送到邮箱",
    "data": null
}
```

---

## 三、用户管理模块 API（管理员）

### 3.1 获取用户列表

**请求**
```
GET /api/v1/admin/users
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| role | string | 否 | 角色：admin/teacher/student |
| status | int | 否 | 状态：0-禁用，1-启用 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "username": "teacher001",
                "realName": "张老师",
                "role": "teacher",
                "email": "teacher@example.com",
                "status": 1,
                "lastLoginTime": "2024-01-15 10:30:00",
                "createTime": "2024-01-01 08:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 100
        }
    }
}
```

---

### 3.2 创建用户

**请求**
```
POST /api/v1/admin/users
```

**参数**
```json
{
    "username": "student001",
    "password": "123456",
    "realName": "王小明",
    "role": "student",
    "email": "student@example.com",
    "phone": "13800138000"
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 100
    }
}
```

---

### 3.3 批量导入学生

**请求**
```
POST /api/v1/admin/users/import
Content-Type: multipart/form-data
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | Excel文件 |

**响应**
```json
{
    "code": 200,
    "message": "导入成功",
    "data": {
        "total": 50,
        "success": 48,
        "failed": 2,
        "errors": ["第5行：学号重复", "第10行：邮箱格式错误"]
    }
}
```

---

### 3.4 下载导入模板

**请求**
```
GET /api/v1/admin/users/template?type=student
```

**响应**
文件下载（Excel格式）

---

### 3.5 编辑用户

**请求**
```
PUT /api/v1/admin/users/{id}
```

**参数**
```json
{
    "realName": "王小明",
    "email": "new@example.com",
    "phone": "13900139000"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 3.6 重置密码

**请求**
```
PUT /api/v1/admin/users/{id}/resetPassword
```

**响应**
```json
{
    "code": 200,
    "message": "密码已重置为：123456",
    "data": null
}
```

---

### 3.7 启用/禁用用户

**请求**
```
PUT /api/v1/admin/users/{id}/status
```

**参数**
```json
{
    "status": 0
}
```

**响应**
```json
{
    "code": 200,
    "message": "状态更新成功",
    "data": null
}
```

---

### 3.8 删除用户

**请求**
```
DELETE /api/v1/admin/users/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 3.9 导出用户

**请求**
```
GET /api/v1/admin/users/export
```

**响应**
文件下载（Excel格式）

---

## 四、学期管理 API（管理员）

### 4.1 获取学期列表

**请求**
```
GET /api/v1/admin/semesters
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "2024-2025学年第一学期",
            "startDate": "2024-09-01",
            "endDate": "2025-01-15",
            "status": 1
        }
    ]
}
```

---

### 4.2 创建学期

**请求**
```
POST /api/v1/admin/semesters
```

**参数**
```json
{
    "name": "2024-2025学年第二学期",
    "startDate": "2025-02-01",
    "endDate": "2025-07-15"
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 2
    }
}
```

---

### 4.3 编辑学期

**请求**
```
PUT /api/v1/admin/semesters/{id}
```

**参数**
```json
{
    "name": "2024-2025学年第二学期",
    "startDate": "2025-02-01",
    "endDate": "2025-07-15",
    "status": 1
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 4.4 删除学期

**请求**
```
DELETE /api/v1/admin/semesters/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

## 五、班级管理 API（管理员）

### 5.1 获取班级列表

**请求**
```
GET /api/v1/admin/classes
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 关键词搜索 |
| grade | string | 否 | 年级 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "计算机1班",
                "grade": "2024级",
                "description": "",
                "studentCount": 40,
                "createTime": "2024-01-01 08:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 5
        }
    }
}
```

---

### 5.2 创建班级

**请求**
```
POST /api/v1/admin/classes
```

**参数**
```json
{
    "name": "计算机2班",
    "grade": "2024级",
    "description": ""
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 2
    }
}
```

---

### 5.3 编辑班级

**请求**
```
PUT /api/v1/admin/classes/{id}
```

**参数**
```json
{
    "name": "计算机2班（新）",
    "grade": "2024级",
    "description": ""
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 5.4 删除班级

**请求**
```
DELETE /api/v1/admin/classes/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 5.5 分配学生到班级

**请求**
```
POST /api/v1/admin/classes/{id}/students
```

**参数**
```json
{
    "studentIds": [1, 2, 3, 4, 5]
}
```

**响应**
```json
{
    "code": 200,
    "message": "分配成功",
    "data": null
}
```

---

## 六、课程管理模块 API（教师）

### 6.1 获取课程列表

**请求**
```
GET /api/v1/teacher/courses
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int | 否 | 状态：0-下架，1-上架 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "Java程序设计",
                "code": "CS101",
                "credits": 4.0,
                "hours": 64,
                "description": "Java编程基础",
                "studentCount": 120,
                "examCount": 3,
                "status": 1,
                "courseCode": "ABC123",
                "createTime": "2024-01-01 08:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 5
        }
    }
}
```

---

### 6.2 创建课程

**请求**
```
POST /api/v1/teacher/courses
```

**参数**
```json
{
    "name": "Java程序设计",
    "code": "CS101",
    "semesterId": 1,
    "credits": 4.0,
    "hours": 64,
    "description": "Java编程基础课程"
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 1,
        "courseCode": "ABC123"
    }
}
```

---

### 6.3 编辑课程

**请求**
```
PUT /api/v1/teacher/courses/{id}
```

**参数**
```json
{
    "name": "Java程序设计（进阶）",
    "code": "CS101",
    "credits": 4.0,
    "hours": 72,
    "description": "Java编程进阶课程"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 6.4 删除课程

**请求**
```
DELETE /api/v1/teacher/courses/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 6.5 获取课程详情

**请求**
```
GET /api/v1/teacher/courses/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "Java程序设计",
        "code": "CS101",
        "teacherId": 10,
        "teacherName": "张老师",
        "semesterId": 1,
        "semesterName": "2024-2025学年第一学期",
        "credits": 4.0,
        "hours": 64,
        "description": "Java编程基础课程",
        "studentCount": 120,
        "examCount": 3,
        "status": 1,
        "courseCode": {
            "code": "ABC123",
            "expireTime": null,
            "maxUses": null,
            "usedCount": 50
        }
    }
}
```

---

### 6.6 生成/刷新课程码

**请求**
```
POST /api/v1/teacher/courses/{id}/generateCode
```

**参数**
```json
{
    "expireTime": "2025-12-31 23:59:59",
    "maxUses": 200
}
```

**响应**
```json
{
    "code": 200,
    "message": "课程码已生成",
    "data": {
        "code": "XYZ789",
        "expireTime": "2025-12-31 23:59:59",
        "maxUses": 200
    }
}
```

---

### 6.7 获取已选课学生列表

**请求**
```
GET /api/v1/teacher/courses/{id}/students
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "studentId": 101,
                "username": "2024001",
                "realName": "王小明",
                "selectTime": "2024-09-01 10:00:00",
                "className": "计算机1班"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 20,
            "total": 120
        }
    }
}
```

---

### 6.8 移除选课学生

**请求**
```
DELETE /api/v1/teacher/courses/{courseId}/students/{studentId}
```

**响应**
```json
{
    "code": 200,
    "message": "移除成功",
    "data": null
}
```

---

## 七、题库管理模块 API（教师）

### 7.1 获取题目列表

**请求**
```
GET /api/v1/teacher/questions
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| type | string | 否 | 题型 |
| difficulty | string | 否 | 难度 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "courseId": 1,
                "courseName": "Java程序设计",
                "type": "single_choice",
                "typeName": "单选题",
                "difficulty": "medium",
                "difficultyName": "中等",
                "content": "下列哪个是Java的入口类？",
                "score": 5.0,
                "knowledgePoints": "Java基础,类与对象",
                "usageCount": 50,
                "createTime": "2024-01-15 10:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 200
        }
    }
}
```

---

### 7.2 创建题目

**请求**
```
POST /api/v1/teacher/questions
```

**参数**
```json
{
    "courseId": 1,
    "type": "single_choice",
    "difficulty": "medium",
    "content": "下列哪个是Java的入口类？",
    "answer": "A",
    "analysis": "Java程序的入口是main方法，主类必须包含main方法",
    "score": 5.0,
    "knowledgePoints": "Java基础,类与对象",
    "chapter": "第一章",
    "options": [
        {"label": "A", "content": "Test.java", "isCorrect": true},
        {"label": "B", "content": "Main.java", "isCorrect": false},
        {"label": "C", "content": "Hello.java", "isCorrect": false},
        {"label": "D", "content": "Program.java", "isCorrect": false}
    ]
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 1
    }
}
```

---

### 7.3 编辑题目

**请求**
```
PUT /api/v1/teacher/questions/{id}
```

**参数**
```json
{
    "content": "下列哪个是Java的入口方法？",
    "answer": "main",
    "analysis": "Java程序的入口是main方法",
    "score": 5.0,
    "difficulty": "easy",
    "options": [...]
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 7.4 删除题目

**请求**
```
DELETE /api/v1/teacher/questions/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 7.5 批量导入题目

**请求**
```
POST /api/v1/teacher/questions/import
Content-Type: multipart/form-data
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | Excel文件 |
| courseId | long | 是 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "导入成功",
    "data": {
        "total": 100,
        "success": 95,
        "failed": 5
    }
}
```

---

### 7.6 导出题目

**请求**
```
GET /api/v1/teacher/questions/export
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| type | string | 否 | 题型 |
| difficulty | string | 否 | 难度 |

**响应**
文件下载（Excel格式）

---

### 7.7 收藏题目

**请求**
```
POST /api/v1/teacher/questions/{id}/favorite
```

**响应**
```json
{
    "code": 200,
    "message": "收藏成功",
    "data": null
}
```

---

### 7.8 获取收藏列表

**请求**
```
GET /api/v1/teacher/questions/favorites
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [...],
        "pagination": {...}
    }
}
```

---

### 7.9 获取题目详情（含选项）

**请求**
```
GET /api/v1/teacher/questions/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "courseId": 1,
        "courseName": "Java程序设计",
        "type": "single_choice",
        "difficulty": "medium",
        "content": "下列哪个是Java的入口类？",
        "answer": "A",
        "analysis": "Java程序的入口是main方法",
        "score": 5.0,
        "knowledgePoints": "Java基础",
        "usageCount": 50,
        "options": [
            {"label": "A", "content": "Test.java", "isCorrect": true},
            {"label": "B", "content": "Main.java", "isCorrect": false},
            {"label": "C", "content": "Hello.java", "isCorrect": false},
            {"label": "D", "content": "Program.java", "isCorrect": false}
        ]
    }
}
```

---

## 八、试卷管理模块 API（教师）

### 8.1 获取试卷列表

**请求**
```
GET /api/v1/teacher/papers
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| status | int | 否 | 状态：0-草稿，1-已发布 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "Java期末考试",
                "courseName": "Java程序设计",
                "totalScore": 100.0,
                "questionCount": 50,
                "duration": 120,
                "type": "manual",
                "typeName": "手动组卷",
                "status": 1,
                "statusName": "已发布",
                "createTime": "2024-01-10 08:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 20
        }
    }
}
```

---

### 8.2 创建试卷

**请求**
```
POST /api/v1/teacher/papers
```

**参数**
```json
{
    "name": "Java期末考试",
    "courseId": 1,
    "totalScore": 100.0,
    "passScore": 60.0,
    "duration": 120,
    "description": "本课程期末考试",
    "type": "manual"
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "id": 1
    }
}
```

---

### 8.3 编辑试卷

**请求**
```
PUT /api/v1/teacher/papers/{id}
```

**参数**
```json
{
    "name": "Java期末考试（修订版）",
    "totalScore": 100.0,
    "passScore": 60.0,
    "duration": 120,
    "description": "本课程期末考试"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 8.4 删除试卷

**请求**
```
DELETE /api/v1/teacher/papers/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 8.5 添加试卷题目

**请求**
```
POST /api/v1/teacher/papers/{id}/questions
```

**参数**
```json
{
    "questionIds": [1, 2, 3, 4, 5],
    "scores": {
        "1": 5.0,
        "2": 5.0,
        "3": 10.0,
        "4": 10.0,
        "5": 10.0
    }
}
```

**响应**
```json
{
    "code": 200,
    "message": "添加成功",
    "data": {
        "questionCount": 5,
        "totalScore": 40.0
    }
}
```

---

### 8.6 移除试卷题目

**请求**
```
DELETE /api/v1/teacher/papers/{paperId}/questions/{questionId}
```

**响应**
```json
{
    "code": 200,
    "message": "移除成功",
    "data": null
}
```

---

### 8.7 设置题目分值

**请求**
```
PUT /api/v1/teacher/papers/{paperId}/questions/{questionId}/score
```

**参数**
```json
{
    "score": 10.0
}
```

**响应**
```json
{
    "code": 200,
    "message": "分值已更新",
    "data": null
}
```

---

### 8.8 预览试卷

**请求**
```
GET /api/v1/teacher/papers/{id}/preview
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "Java期末考试",
        "totalScore": 100.0,
        "passScore": 60.0,
        "duration": 120,
        "questions": [
            {
                "id": 1,
                "sortOrder": 1,
                "type": "single_choice",
                "content": "下列哪个是Java的入口类？",
                "score": 5.0,
                "options": [
                    {"label": "A", "content": "Test.java"},
                    {"label": "B", "content": "Main.java"},
                    {"label": "C", "content": "Hello.java"},
                    {"label": "D", "content": "Program.java"}
                ],
                "answer": "A",
                "analysis": "Java程序的入口是main方法"
            }
        ]
    }
}
```

---

### 8.9 随机组卷

**请求**
```
POST /api/v1/teacher/papers/{id}/random
```

**参数**
```json
{
    "rules": {
        "single_choice": {"count": 20, "score": 2.0, "difficulty": "easy,medium"},
        "multi_choice": {"count": 10, "score": 4.0, "difficulty": "medium"},
        "true_false": {"count": 10, "score": 1.0, "difficulty": "easy"},
        "fill_blank": {"count": 5, "score": 4.0, "difficulty": "medium"},
        "essay": {"count": 2, "score": 10.0, "difficulty": "hard"}
    },
    "knowledgePoints": ["Java基础", "面向对象", "集合"],
    "courseId": 1
}
```

**响应**
```json
{
    "code": 200,
    "message": "组卷成功",
    "data": {
        "questionCount": 47,
        "totalScore": 100.0,
        "questions": [...]
    }
}
```

---

### 8.10 保存为模板

**请求**
```
POST /api/v1/teacher/papers/{id}/saveAsTemplate
```

**参数**
```json
{
    "name": "期末考试模板",
    "description": "标准期末考试配置"
}
```

**响应**
```json
{
    "code": 200,
    "message": "模板保存成功",
    "data": {
        "templateId": 1
    }
}
```

---

## 九、考试安排模块 API（教师）

### 9.1 获取考试列表

**请求**
```
GET /api/v1/teacher/exams
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| status | string | 否 | 状态 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "name": "Java期末考试",
                "courseName": "Java程序设计",
                "examPaperName": "Java期末考试",
                "startTime": "2024-01-20 09:00:00",
                "endTime": "2024-01-20 11:00:00",
                "duration": 120,
                "totalScore": 100.0,
                "passScore": 60.0,
                "studentCount": 120,
                "submitCount": 115,
                "status": "ongoing",
                "statusName": "进行中",
                "examType": "formal",
                "examTypeName": "正式考试"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 15
        }
    }
}
```

---

### 9.2 发布考试

**请求**
```
POST /api/v1/teacher/exams
```

**参数**
```json
{
    "name": "Java期末考试",
    "examPaperId": 1,
    "courseId": 1,
    "startTime": "2024-01-20 09:00:00",
    "endTime": "2024-01-20 11:00:00",
    "duration": 120,
    "allowTimes": 1,
    "isRandomOrder": 1,
    "isRandomOptions": 1,
    "examType": "formal",
    "studentFilter": {
        "type": "course",
        "courseSelectionIds": [1, 2, 3]
    }
}
```

**响应**
```json
{
    "code": 200,
    "message": "发布成功",
    "data": {
        "examId": 1,
        "studentCount": 120
    }
}
```

---

### 9.3 编辑考试

**请求**
```
PUT /api/v1/teacher/exams/{id}
```

**参数**
```json
{
    "name": "Java期末考试（调整）",
    "startTime": "2024-01-20 10:00:00",
    "endTime": "2024-01-20 12:00:00",
    "duration": 120
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 9.4 取消考试

**请求**
```
PUT /api/v1/teacher/exams/{id}/cancel
```

**响应**
```json
{
    "code": 200,
    "message": "考试已取消",
    "data": null
}
```

---

### 9.5 强制交卷

**请求**
```
POST /api/v1/teacher/exams/{examId}/forceSubmit/{studentId}
```

**响应**
```json
{
    "code": 200,
    "message": "强制交卷成功",
    "data": null
}
```

---

### 9.6 获取考试监控

**请求**
```
GET /api/v1/teacher/exams/{id}/monitor
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examId": 1,
        "examName": "Java期末考试",
        "totalStudents": 120,
        "submittedCount": 100,
        "ongoingCount": 15,
        "pendingCount": 5,
        "students": [
            {
                "studentId": 101,
                "studentName": "王小明",
                "status": "ongoing",
                "startTime": "2024-01-20 09:05:00",
                "answeredCount": 30,
                "totalQuestions": 50,
                "switchScreenCount": 2,
                "lastActiveTime": "2024-01-20 10:30:00"
            }
        ]
    }
}
```

---

### 9.7 获取缺考学生列表

**请求**
```
GET /api/v1/teacher/exams/{id}/absent
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {"studentId": 105, "studentName": "李四", "reason": ""}
    ]
}
```

---

### 9.8 安排补考

**请求**
```
POST /api/v1/teacher/exams/{id}/makeup
```

**参数**
```json
{
    "studentIds": [101, 102, 105],
    "examPaperId": 1,
    "startTime": "2024-01-25 14:00:00",
    "endTime": "2024-01-25 16:00:00",
    "duration": 120
}
```

**响应**
```json
{
    "code": 200,
    "message": "补考安排成功",
    "data": {
        "makeupExamId": 10
    }
}
```

---

## 十、试卷批改模块 API（教师）

### 10.1 获取待批改列表

**请求**
```
GET /api/v1/teacher/grading/tasks
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| status | string | 否 | 状态 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "taskId": 1,
                "examRecordId": 100,
                "examId": 1,
                "examName": "Java期末考试",
                "studentId": 101,
                "studentName": "王小明",
                "questionId": 50,
                "questionType": "essay",
                "questionContent": "请简述面向对象的三特性...",
                "score": null,
                "status": "pending",
                "createTime": "2024-01-20 11:05:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 20,
            "total": 100
        }
    }
}
```

---

### 10.2 批改题目

**请求**
```
POST /api/v1/teacher/grading/tasks/{taskId}/submit
```

**参数**
```json
{
    "score": 8.0,
    "comment": "回答基本正确，但不够深入"
}
```

**响应**
```json
{
    "code": 200,
    "message": "批改成功",
    "data": null
}
```

---

### 10.3 批量批改

**请求**
```
POST /api/v1/teacher/grading/batch
```

**参数**
```json
{
    "tasks": [
        {"taskId": 1, "score": 8.0, "comment": "回答基本正确"},
        {"taskId": 2, "score": 9.0, "comment": "回答很好"},
        {"taskId": 3, "score": 7.0, "comment": "回答一般"}
    ]
}
```

**响应**
```json
{
    "code": 200,
    "message": "批量批改成功",
    "data": {
        "successCount": 3,
        "failedCount": 0
    }
}
```

---

### 10.4 获取批改详情

**请求**
```
GET /api/v1/teacher/grading/examRecord/{examRecordId}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examRecordId": 100,
        "examName": "Java期末考试",
        "studentName": "王小明",
        "totalScore": 85.0,
        "objectiveScore": 60.0,
        "subjectiveScore": 25.0,
        "questions": [
            {
                "questionId": 50,
                "type": "essay",
                "content": "请简述面向对象的三特性...",
                "score": 8.0,
                "fullScore": 10.0,
                "studentAnswer": "封装、继承、多态...",
                "comment": "回答基本正确"
            }
        ]
    }
}
```

---

### 10.5 确认成绩

**请求**
```
POST /api/v1/teacher/grading/examRecord/{examRecordId}/confirm
```

**响应**
```json
{
    "code": 200,
    "message": "成绩已确认",
    "data": null
}
```

---

### 10.6 获取批改记录

**请求**
```
GET /api/v1/teacher/grading/history
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [...],
        "pagination": {...}
    }
}
```

---

### 10.7 获取成绩复核申请列表

**请求**
```
GET /api/v1/teacher/grading/reviews
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| status | string | 否 | 状态 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "reviewId": 1,
                "gradeId": 100,
                "examId": 1,
                "examName": "Java期末考试",
                "studentId": 101,
                "studentName": "王小明",
                "questionId": 5,
                "reason": "我认为这道题我的答案也是正确的",
                "status": "pending",
                "createTime": "2024-01-21 10:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 10,
            "total": 5
        }
    }
}
```

---

### 10.8 处理成绩复核

**请求**
```
POST /api/v1/teacher/grading/reviews/{reviewId}/handle
```

**参数**
```json
{
    "status": "approved",
    "teacherComment": "经核实，学生的答案确实正确，原有评分有误",
    "originalScore": 3.0,
    "newScore": 5.0
}
```

**响应**
```json
{
    "code": 200,
    "message": "复核处理完成",
    "data": null
}
```

---

## 十一、成绩管理模块 API（教师）

### 11.1 获取成绩列表

**请求**
```
GET /api/v1/teacher/grades
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| courseId | long | 否 | 课程ID |
| classId | long | 否 | 班级ID |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "gradeId": 1,
                "examId": 1,
                "examName": "Java期末考试",
                "studentId": 101,
                "studentName": "王小明",
                "className": "计算机1班",
                "totalScore": 85.0,
                "objectiveScore": 60.0,
                "subjectiveScore": 25.0,
                "rank": 5,
                "percentile": 95.5,
                "status": "published",
                "publishTime": "2024-01-21 12:00:00"
            }
        ],
        "pagination": {
            "page": 1,
            "pageSize": 20,
            "total": 120
        }
    }
}
```

---

### 11.2 获取成绩详情

**请求**
```
GET /api/v1/teacher/grades/{gradeId}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "gradeId": 1,
        "examId": 1,
        "examName": "Java期末考试",
        "studentId": 101,
        "studentName": "王小明",
        "className": "计算机1班",
        "totalScore": 85.0,
        "objectiveScore": 60.0,
        "subjectiveScore": 25.0,
        "rank": 5,
        "percentile": 95.5,
        "status": "published",
        "questions": [
            {
                "questionId": 1,
                "type": "single_choice",
                "content": "...",
                "fullScore": 5.0,
                "score": 5.0,
                "studentAnswer": "A",
                "correctAnswer": "A",
                "isCorrect": true
            }
        ]
    }
}
```

---

### 11.3 获取成绩统计

**请求**
```
GET /api/v1/teacher/grades/exam/{examId}/statistics
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examId": 1,
        "examName": "Java期末考试",
        "totalStudents": 120,
        "submittedCount": 118,
        "avgScore": 72.5,
        "maxScore": 98.0,
        "minScore": 35.0,
        "passCount": 100,
        "passRate": 84.7,
        "scoreDistribution": {
            "0-60": 20,
            "60-70": 25,
            "70-80": 35,
            "80-90": 30,
            "90-100": 10
        },
        "classComparison": [
            {"className": "计算机1班", "avgScore": 75.0, "passRate": 90.0},
            {"className": "计算机2班", "avgScore": 70.0, "passRate": 80.0}
        ]
    }
}
```

---

### 11.4 导出成绩

**请求**
```
GET /api/v1/teacher/grades/export
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 是 | 考试ID |
| classId | long | 否 | 班级ID |

**响应**
文件下载（Excel格式）

---

### 11.5 获取试卷分析

**请求**
```
GET /api/v1/teacher/grades/exam/{examId}/analysis
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examId": 1,
        "examName": "Java期末考试",
        "totalScore": 100.0,
        "questionCount": 50,
        "questions": [
            {
                "questionId": 1,
                "type": "single_choice",
                "content": "...",
                "fullScore": 2.0,
                "correctCount": 100,
                "totalCount": 118,
                "correctRate": 84.7,
                "avgScore": 1.69
            }
        ],
        "knowledgeAnalysis": [
            {"knowledgePoint": "Java基础", "correctRate": 85.0},
            {"knowledgePoint": "面向对象", "correctRate": 72.0},
            {"knowledgePoint": "集合", "correctRate": 65.0}
        ]
    }
}
```

---

## 十二、数据分析模块 API（教师）

### 12.1 获取班级成绩对比

**请求**
```
GET /api/v1/teacher/analysis/classComparison
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| courseId | long | 否 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examName": "Java期末考试",
        "classes": [
            {"className": "计算机1班", "avgScore": 75.0, "passRate": 90.0, "studentCount": 40},
            {"className": "计算机2班", "avgScore": 70.0, "passRate": 80.0, "studentCount": 38}
        ]
    }
}
```

---

### 12.2 获取成绩分布

**请求**
```
GET /api/v1/teacher/analysis/scoreDistribution
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| courseId | long | 否 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examName": "Java期末考试",
        "distribution": [
            {"range": "0-60", "count": 20, "percentage": 16.9},
            {"range": "60-70", "count": 25, "percentage": 21.2},
            {"range": "70-80", "count": 35, "percentage": 29.7},
            {"range": "80-90", "count": 30, "percentage": 25.4},
            {"range": "90-100", "count": 8, "percentage": 6.8}
        ]
    }
}
```

---

### 12.3 获取知识点掌握度

**请求**
```
GET /api/v1/teacher/analysis/knowledgeMastery
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 否 | 考试ID |
| courseId | long | 是 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "courseName": "Java程序设计",
        "knowledgePoints": [
            {"name": "Java基础", "correctRate": 85.0, "questionCount": 10},
            {"name": "面向对象", "correctRate": 72.0, "questionCount": 8},
            {"name": "集合框架", "correctRate": 65.0, "questionCount": 12},
            {"name": "异常处理", "correctRate": 78.0, "questionCount": 5}
        ]
    }
}
```

---

### 12.4 获取考试趋势

**请求**
```
GET /api/v1/teacher/analysis/examTrend
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 是 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "courseName": "Java程序设计",
        "exams": [
            {"examName": "第一章测验", "avgScore": 78.5, "examDate": "2024-10-15"},
            {"examName": "期中考试", "avgScore": 72.0, "examDate": "2024-11-15"},
            {"examName": "期末考试", "avgScore": 75.0, "examDate": "2025-01-15"}
        ]
    }
}
```

---

### 12.5 获取学生排名

**请求**
```
GET /api/v1/teacher/analysis/studentRanking
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| examId | long | 是 | 考试ID |
| classId | long | 否 | 班级ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examName": "Java期末考试",
        "rankings": [
            {"rank": 1, "studentName": "王小明", "className": "计算机1班", "totalScore": 98.0},
            {"rank": 2, "studentName": "李四", "className": "计算机2班", "totalScore": 95.0},
            {"rank": 3, "studentName": "张三", "className": "计算机1班", "totalScore": 93.0}
        ]
    }
}
```

---

### 12.6 获取课程统计

**请求**
```
GET /api/v1/teacher/analysis/courseStatistics
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 是 | 课程ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "courseId": 1,
        "courseName": "Java程序设计",
        "examCount": 5,
        "totalStudents": 120,
        "avgParticipation": 95.0,
        "avgPassRate": 85.0,
        "exams": [
            {"examName": "第一章测验", "participationRate": 98.0, "passRate": 90.0},
            {"examName": "第二章测验", "participationRate": 95.0, "passRate": 88.0}
        ]
    }
}
```

---

## 十三、学生端 API

### 13.1 首页 - 获取课程列表

**请求**
```
GET /api/v1/student/courses
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "courseId": 1,
            "courseName": "Java程序设计",
            "teacherName": "张老师",
            "credits": 4.0,
            "examCount": 3,
            "pendingExamCount": 1,
            "ongoingExamCount": 0,
            "completedExamCount": 2
        }
    ]
}
```

---

### 13.2 首页 - 获取课程详情

**请求**
```
GET /api/v1/student/courses/{courseId}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "courseId": 1,
        "courseName": "Java程序设计",
        "courseCode": "CS101",
        "teacherName": "张老师",
        "credits": 4.0,
        "hours": 64,
        "description": "Java编程基础",
        "exams": [
            {
                "examId": 1,
                "examName": "Java期末考试",
                "startTime": "2024-01-20 09:00:00",
                "endTime": "2024-01-20 11:00:00",
                "status": "completed",
                "statusName": "已完成",
                "myScore": 85.0,
                "hasWrongQuestions": true
            },
            {
                "examId": 2,
                "examName": "Java章节测验",
                "startTime": "2024-01-25 14:00:00",
                "endTime": "2024-01-25 15:00:00",
                "status": "pending",
                "statusName": "待参加"
            }
        ]
    }
}
```

---

### 13.3 使用课程码加课

**请求**
```
POST /api/v1/student/courses/join
```

**参数**
```json
{
    "courseCode": "ABC123"
}
```

**响应**
```json
{
    "code": 200,
    "message": "加课成功",
    "data": {
        "courseId": 1,
        "courseName": "Java程序设计"
    }
}
```

---

### 13.4 获取待参加考试列表

**请求**
```
GET /api/v1/student/exams/pending
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "examId": 2,
            "examName": "Java章节测验",
            "courseName": "Java程序设计",
            "startTime": "2024-01-25 14:00:00",
            "endTime": "2024-01-25 15:00:00",
            "duration": 60,
            "totalScore": 100.0
        }
    ]
}
```

---

### 13.5 考试资格验证

**请求**
```
GET /api/v1/student/exams/{examId}/qualification
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "qualified": true,
        "reasons": [],
        "examInfo": {
            "examId": 2,
            "examName": "Java章节测验",
            "duration": 60,
            "allowTimes": 1,
            "examTimes": 0
        }
    }
}
```

---

### 13.6 开始考试

**请求**
```
POST /api/v1/student/exams/{examId}/start
```

**响应**
```json
{
    "code": 200,
    "message": "考试开始",
    "data": {
        "examRecordId": 100,
        "examId": 2,
        "questions": [
            {
                "questionId": 1,
                "sortOrder": 1,
                "type": "single_choice",
                "content": "下列哪个是Java的入口类？",
                "score": 5.0,
                "options": [
                    {"label": "A", "content": "Test.java"},
                    {"label": "B", "content": "Main.java"},
                    {"label": "C", "content": "Hello.java"},
                    {"label": "D", "content": "Program.java"}
                ],
                "isMarked": false,
                "myAnswer": null
            }
        ],
        "totalQuestions": 50,
        "duration": 60,
        "endTime": "2024-01-25 15:00:00",
        "source": "exam"
    }
}
```

---

### 13.7 保存答题进度（断点续考）

**请求**
```
POST /api/v1/student/exams/records/{examRecordId}/save
```

**参数**
```json
{
    "answers": {
        "1": {"answer": "A", "isMarked": false},
        "2": {"answer": "B,C", "isMarked": true},
        "3": {"answer": "true", "isMarked": false}
    },
    "saveType": "auto",
    "networkStatus": "normal"
}
```

**响应**
```json
{
    "code": 200,
    "message": "保存成功",
    "data": {
        "snapshotId": 1,
        "saveTime": "2024-01-25 14:30:00"
    }
}
```

---

### 13.8 恢复答题进度

**请求**
```
GET /api/v1/student/exams/records/{examRecordId}/restore
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "examRecordId": 100,
        "questions": [...],
        "lastSaveTime": "2024-01-25 14:30:00",
        "answeredCount": 30,
        "totalQuestions": 50
    }
}
```

---

### 13.9 提交试卷

**请求**
```
POST /api/v1/student/exams/records/{examRecordId}/submit
```

**响应**
```json
{
    "code": 200,
    "message": "提交成功",
    "data": {
        "examRecordId": 100,
        "totalScore": null,
        "objectiveScore": 60.0,
        "message": "客观题已评分，主观题待批改"
    }
}
```

---

### 13.10 获取成绩列表

**请求**
```
GET /api/v1/student/grades
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "gradeId": 1,
                "examId": 1,
                "examName": "Java期末考试",
                "courseName": "Java程序设计",
                "totalScore": 85.0,
                "objectiveScore": 60.0,
                "subjectiveScore": 25.0,
                "rank": 5,
                "status": "published",
                "publishTime": "2024-01-21 12:00:00"
            }
        ],
        "pagination": {...}
    }
}
```

---

### 13.11 获取成绩详情

**请求**
```
GET /api/v1/student/grades/{gradeId}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "gradeId": 1,
        "examId": 1,
        "examName": "Java期末考试",
        "courseName": "Java程序设计",
        "totalScore": 85.0,
        "objectiveScore": 60.0,
        "subjectiveScore": 25.0,
        "rank": 5,
        "classAvgScore": 72.5,
        "classRank": "5/120",
        "questions": [
            {
                "questionId": 1,
                "type": "single_choice",
                "content": "...",
                "fullScore": 5.0,
                "score": 5.0,
                "myAnswer": "A",
                "correctAnswer": "A",
                "isCorrect": true
            }
        ]
    }
}
```

---

### 13.12 申请成绩复核

**请求**
```
POST /api/v1/student/grades/{gradeId}/review
```

**参数**
```json
{
    "questionId": 5,
    "reason": "我认为这道题的答案应该是多选AB"
}
```

**响应**
```json
{
    "code": 200,
    "message": "复核申请已提交",
    "data": {
        "reviewId": 1
    }
}
```

---

### 13.13 获取错题本列表

**请求**
```
GET /api/v1/student/wrongQuestions
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sourceType | string | 否 | 来源类型：exam/practice |
| courseId | long | 否 | 课程ID |
| examId | long | 否 | 考试ID |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "wrongQuestionId": 1,
                "questionId": 10,
                "sourceType": "exam",
                "sourceTypeName": "考试错题",
                "examId": 1,
                "examName": "Java期末考试",
                "courseName": "Java程序设计",
                "questionType": "single_choice",
                "content": "下列哪个是Java的入口类？",
                "myAnswer": "B",
                "correctAnswer": "A",
                "wrongTimes": 1,
                "lastWrongTime": "2024-01-20 11:00:00",
                "practiceCount": 0,
                "isFavorited": false
            }
        ],
        "pagination": {...}
    }
}
```

---

### 13.14 获取错题详情

**请求**
```
GET /api/v1/student/wrongQuestions/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "wrongQuestionId": 1,
        "questionId": 10,
        "sourceType": "exam",
        "examName": "Java期末考试",
        "question": {
            "type": "single_choice",
            "content": "下列哪个是Java的入口类？",
            "options": [
                {"label": "A", "content": "Test.java"},
                {"label": "B", "content": "Main.java"},
                {"label": "C", "content": "Hello.java"},
                {"label": "D", "content": "Program.java"}
            ],
            "correctAnswer": "A",
            "analysis": "Java程序的入口是main方法"
        },
        "myAnswer": "B",
        "mySolution": "我认为是Main.java",
        "wrongTimes": 1,
        "practiceCount": 2,
        "correctCount": 1,
        "isFavorited": false
    }
}
```

---

### 13.15 更新解题思路备注

**请求**
```
PUT /api/v1/student/wrongQuestions/{id}/solution
```

**参数**
```json
{
    "mySolution": "这道题需要注意Java程序的入口方法名是main而不是类名"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 13.16 收藏错题

**请求**
```
POST /api/v1/student/wrongQuestions/{id}/favorite
```

**响应**
```json
{
    "code": 200,
    "message": "收藏成功",
    "data": null
}
```

---

### 13.17 取消收藏错题

**请求**
```
DELETE /api/v1/student/wrongQuestions/{id}/favorite
```

**响应**
```json
{
    "code": 200,
    "message": "取消收藏成功",
    "data": null
}
```

---

### 13.18 错题重练

**请求**
```
POST /api/v1/student/wrongQuestions/{id}/practice
```

**参数**
```json
{
    "answer": "A"
}
```

**响应**
```json
{
    "code": 200,
    "message": "提交成功",
    "data": {
        "isCorrect": true,
        "correctAnswer": "A",
        "analysis": "Java程序的入口是main方法",
        "practiceRecordId": 1
    }
}
```

---

### 13.19 获取错题报告

**请求**
```
GET /api/v1/student/wrongQuestions/report
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| courseId | long | 否 | 课程ID |
| examId | long | 否 | 考试ID |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "totalWrongQuestions": 50,
        "practicedCount": 30,
        "correctRate": 70.0,
        "weakKnowledgePoints": [
            {"name": "面向对象", "wrongCount": 15, "masteryRate": 40.0},
            {"name": "集合框架", "wrongCount": 12, "masteryRate": 50.0}
        ],
        "improvementTrend": [
            {"month": "2024-10", "correctRate": 60.0},
            {"month": "2024-11", "correctRate": 65.0},
            {"month": "2024-12", "correctRate": 70.0}
        ]
    }
}
```

---

## 十四、学生题库模块 API

### 14.1 获取题库列表

**请求**
```
GET /api/v1/student/questionBanks
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "bankId": 1,
            "name": "我的错题收藏",
            "description": "收藏的重要错题",
            "source": "original",
            "sourceName": "自建",
            "questionCount": 20,
            "isShared": false,
            "createTime": "2024-01-10 10:00:00"
        }
    ]
}
```

---

### 14.2 创建题库

**请求**
```
POST /api/v1/student/questionBanks
```

**参数**
```json
{
    "name": "Java高频考题",
    "description": "收集的高频考题"
}
```

**响应**
```json
{
    "code": 200,
    "message": "创建成功",
    "data": {
        "bankId": 2
    }
}
```

---

### 14.3 编辑题库

**请求**
```
PUT /api/v1/student/questionBanks/{id}
```

**参数**
```json
{
    "name": "Java高频考题（更新）",
    "description": "更新的描述"
}
```

**响应**
```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

---

### 14.4 删除题库

**请求**
```
DELETE /api/v1/student/questionBanks/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 14.5 添加题目到题库

**请求**
```
POST /api/v1/student/questionBanks/{bankId}/questions
```

**参数**
```json
{
    "questionId": 10,
    "question": {
        "type": "single_choice",
        "content": "这是一道新题目...",
        "options": [...],
        "answer": "A",
        "analysis": "解析..."
    }
}
```

**响应**
```json
{
    "code": 200,
    "message": "添加成功",
    "data": {
        "bankQuestionId": 1
    }
}
```

---

### 14.6 从题库移除题目

**请求**
```
DELETE /api/v1/student/questionBanks/{bankId}/questions/{questionId}
```

**响应**
```json
{
    "code": 200,
    "message": "移除成功",
    "data": null
}
```

---

### 14.7 分享题库

**请求**
```
POST /api/v1/student/questionBanks/{bankId}/share
```

**参数**
```json
{
    "expireTime": "2025-12-31 23:59:59"
}
```

**响应**
```json
{
    "code": 200,
    "message": "分享成功",
    "data": {
        "shareCode": "XYZ123",
        "shareLink": "/api/v1/student/questionBanks/join?code=XYZ123",
        "expireTime": "2025-12-31 23:59:59"
    }
}
```

---

### 14.8 加入分享的题库

**请求**
```
POST /api/v1/student/questionBanks/join
```

**参数**
```json
{
    "shareCode": "XYZ123"
}
```

**响应**
```json
{
    "code": 200,
    "message": "加入成功",
    "data": {
        "bankId": 5,
        "bankName": "Java高频考题",
        "questionCount": 30
    }
}
```

---

### 14.9 导入教师公开题库

**请求**
```
POST /api/v1/student/questionBanks/import
```

**参数**
```json
{
    "teacherQuestionId": 100,
    "bankId": 2
}
```

**响应**
```json
{
    "code": 200,
    "message": "导入成功",
    "data": {
        "importedQuestionId": 200
    }
}
```

---

## 十五、消息通知模块 API

### 15.1 获取通知列表

**请求**
```
GET /api/v1/notifications
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 否 | 通知类型 |
| isRead | int | 否 | 是否已读：0-未读，1-已读 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "type": "exam",
                "typeName": "考试通知",
                "title": "Java期末考试即将开始",
                "content": "请同学们提前做好准备...",
                "relatedId": 1,
                "isRead": false,
                "createTime": "2024-01-19 10:00:00"
            }
        ],
        "pagination": {...},
        "unreadCount": 5
    }
}
```

---

### 15.2 标记通知已读

**请求**
```
PUT /api/v1/notifications/{id}/read
```

**响应**
```json
{
    "code": 200,
    "message": "标记成功",
    "data": null
}
```

---

### 15.3 标记全部已读

**请求**
```
PUT /api/v1/notifications/readAll
```

**响应**
```json
{
    "code": 200,
    "message": "全部已读",
    "data": null
}
```

---

### 15.4 删除通知

**请求**
```
DELETE /api/v1/notifications/{id}
```

**响应**
```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

## 十六、系统监控模块 API（管理员）

### 16.1 获取在线用户

**请求**
```
GET /api/v1/admin/monitor/onlineUsers
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "totalOnline": 50,
        "students": 30,
        "teachers": 18,
        "admins": 2,
        "users": [
            {
                "userId": 101,
                "username": "student001",
                "realName": "王小明",
                "role": "student",
                "loginTime": "2024-01-20 09:00:00",
                "lastActiveTime": "2024-01-20 10:30:00",
                "ipAddress": "192.168.1.100"
            }
        ]
    }
}
```

---

### 16.2 获取系统状态

**请求**
```
GET /api/v1/admin/monitor/systemStatus
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "cpuUsage": 45.5,
        "memoryUsage": 62.3,
        "diskUsage": 55.0,
        "mysqlConnections": 25,
        "maxConnections": 100,
        "qps": 150,
        "avgResponseTime": 120
    }
}
```

---

### 16.3 获取操作日志

**请求**
```
GET /api/v1/admin/logs/operations
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | long | 否 | 用户ID |
| operation | string | 否 | 操作类型 |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 1,
                "username": "admin",
                "operation": "登录",
                "module": "认证",
                "method": "login",
                "params": "{\"username\":\"admin\"}",
                "result": "成功",
                "ipAddress": "192.168.1.1",
                "executionTime": 50,
                "createTime": "2024-01-20 10:00:00"
            }
        ],
        "pagination": {...}
    }
}
```

---

### 16.4 获取登录日志

**请求**
```
GET /api/v1/admin/logs/login
```

**参数**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 否 | 用户名 |
| status | int | 否 | 登录状态 |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页条数 |

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 1,
                "username": "admin",
                "status": 1,
                "statusName": "成功",
                "ipAddress": "192.168.1.1",
                "loginMethod": "password",
                "createTime": "2024-01-20 10:00:00"
            }
        ],
        "pagination": {...}
    }
}
```

---

### 16.5 数据备份

**请求**
```
POST /api/v1/admin/backup
```

**响应**
```json
{
    "code": 200,
    "message": "备份启动成功",
    "data": {
        "backupId": 1,
        "fileName": "backup_20240120_100000.sql",
        "status": "running"
    }
}
```

---

## 十七、错误码对照表

| 错误码 | 说明 |
|--------|------|
| 10001 | 用户名或密码错误 |
| 10002 | 账户已被禁用 |
| 10003 | 验证码错误 |
| 10004 | Token过期 |
| 10005 | 无权限访问 |
| 20001 | 课程码无效或已过期 |
| 20002 | 已经在该课程 |
| 20003 | 课程不存在 |
| 30001 | 考试资格不符合 |
| 30002 | 考试已开始或已结束 |
| 30003 | 考试次数已用完 |
| 30004 | 存在未完成的考试 |
| 40001 | 成绩复核申请已提交 |
| 40002 | 成绩复核申请不存在 |

---

## 附录

### 附录A：题目类型枚举

| 值 | 说明 |
|------|------|
| single_choice | 单选题 |
| multi_choice | 多选题 |
| true_false | 判断题 |
| fill_blank | 填空题 |
| essay | 简答题 |

### 附录B：难度枚举

| 值 | 说明 |
|------|------|
| easy | 简单 |
| medium | 中等 |
| hard | 困难 |

### 附录C：角色枚举

| 值 | 说明 |
|------|------|
| admin | 管理员 |
| teacher | 教师 |
| student | 学生 |

### 附录D：状态枚举

| 状态 | 说明 |
|------|------|
| pending | 待处理 |
| ongoing | 进行中 |
| submitted | 已提交 |
| finished | 已结束 |
| published | 已发布 |
| graded | 已评分 |

