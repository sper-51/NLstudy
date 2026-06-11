
<#
 智能在线考试平台 - API联调测试脚本
 作者：系统自动生成
 日期：2026年6月
 用途：验证前后端接口联调状态
#>

$baseUrl = "http://localhost:8080/api/v1"
$headers = @{'X-User-Id' = '101'}

function Test-API {
    param(
        [string]$method,
        [string]$endpoint,
        [string]$body = $null,
        [string]$description
    )
    
    $url = "$baseUrl$endpoint"
    Write-Host "`n=== 测试: $description ==="
    Write-Host "接口: $method $url"
    
    try {
        if ($body) {
            Write-Host "请求体: $body"
            $response = Invoke-RestMethod -Uri $url -Method $method -Body $body -ContentType 'application/json' -Headers $headers -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $url -Method $method -Headers $headers -ErrorAction Stop
        }
        
        Write-Host "状态: ✅ 成功"
        Write-Host "响应: $(ConvertTo-Json $response -Depth 2)"
        return $true
    } catch {
        Write-Host "状态: ❌ 失败"
        Write-Host "错误: $($_.Exception.Message)"
        return $false
    }
}

function Test-Auth {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "认证模块测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    $body = @{username='teacher1'; password='123456'} | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/auth/teacher/login' -body $body -description '教师登录'
    
    $body = @{username='student1'; password='123456'} | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/auth/student/login' -body $body -description '学生登录'
    
    $body = @{username='admin'; password='admin123'} | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/auth/admin/login' -body $body -description '管理员登录'
}

function Test-Courses {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "课程管理模块测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/teacher/courses' -description '获取课程列表'
    Test-API -method 'GET' -endpoint '/teacher/courses/1/students' -description '获取课程学生'
    Test-API -method 'DELETE' -endpoint '/teacher/courses/1/students/206' -description '移除选课学生'
    
    $body = @{name='测试课程'; description='测试描述'; status=1} | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/teacher/courses' -body $body -description '创建课程'
}

function Test-Questions {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "题库管理模块测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/teacher/questions?page=1&size=10' -description '获取题目列表'
    
    $body = @{
        type='single_choice'
        content='测试题目内容'
        answer='A'
        analysis='测试解析'
        knowledgePoints='测试知识点'
        difficulty='easy'
        courseId=1
    } | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/teacher/questions' -body $body -description '创建题目'
}

function Test-Papers {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "试卷管理模块测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/teacher/papers' -description '获取试卷列表'
    
    $body = @{
        name='测试试卷'
        courseId=1
        duration=90
        totalScore=100
    } | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/teacher/papers' -body $body -description '创建试卷'
    
    Test-API -method 'GET' -endpoint '/teacher/papers/4/preview' -description '试卷预览（含题目）'
    
    $body = @{questionIds=@(1,2,3); scores=@(10,20,15)} | ConvertTo-Json
    Test-API -method 'POST' -endpoint '/teacher/papers/4/questions' -body $body -description '添加题目到试卷'
}

function Test-Exams {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "考试管理模块测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/teacher/exams' -description '获取考试列表'
    Test-API -method 'GET' -endpoint '/teacher/exams/1/monitor' -description '考试监控'
    Test-API -method 'GET' -endpoint '/teacher/exams/1/detail' -description '考试详情'
    
    # 批改任务
    Write-Host "`n--- 批改与复核 ---"
    Test-API -method 'GET' -endpoint '/teacher/grading/tasks' -description '待批改任务列表'
    Test-API -method 'GET' -endpoint '/teacher/grading/reviews' -description '成绩复核列表'
    Test-API -method 'GET' -endpoint '/teacher/grading/history' -description '批改历史'
}

function Test-Student {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "学生端接口测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/student/exams' -description '学生考试列表'
    Test-API -method 'GET' -endpoint '/student/exams/1' -description '学生考试详情'
}

function Test-Admin {
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    Write-Host "管理端接口测试"
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    Test-API -method 'GET' -endpoint '/admin/semesters' -description '学期列表'
    Test-API -method 'GET' -endpoint '/admin/logs/login' -description '登录日志'
    Test-API -method 'GET' -endpoint '/admin/logs/operation' -description '操作日志'
    Test-API -method 'GET' -endpoint '/admin/logs/statistics' -description '日志统计'
    Test-API -method 'GET' -endpoint '/admin/monitor/online/trend' -description '在线趋势'
    Test-API -method 'GET' -endpoint '/admin/monitor/online/current' -description '实时在线统计'
    Test-API -method 'GET' -endpoint '/admin/monitor/exam/status' -description '考试状态统计'
    Test-API -method 'GET' -endpoint '/admin/monitor/health' -description '系统健康状态'
    Test-API -method 'GET' -endpoint '/admin/dashboard/stats' -description '仪表盘统计'
    Test-API -method 'GET' -endpoint '/admin/dashboard/trend' -description '仪表盘趋势'
}

Write-Host "════════════════════════════════════════════════"
Write-Host "  智能在线考试平台 - API联调测试脚本"
Write-Host "════════════════════════════════════════════════"

Test-Auth
Test-Courses
Test-Questions
Test-Papers
Test-Exams
Test-Student
Test-Admin

Write-Host "`n════════════════════════════════════════════════"
Write-Host "  测试完成"
Write-Host "════════════════════════════════════════════════"
