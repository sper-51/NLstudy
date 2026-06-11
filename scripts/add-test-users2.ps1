# 添加测试用户脚本（使用X-User-Id绕过认证）
$baseUrl = "http://localhost:8080/api/v1"

# 添加教师用户（使用X-User-Id绕过认证）
Write-Host "正在添加教师用户..."
$headers = @{
    "X-User-Id" = "1"
}
$teacherBody = @{
    username = "teacher"
    password = "123456"
    realName = "张老师"
    role = "teacher"
    status = 1
} | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method Post -Body $teacherBody -ContentType "application/json" -Headers $headers | Out-Null
    Write-Host "教师用户添加成功"
} catch {
    Write-Host "教师用户可能已存在，跳过"
}

# 添加学生用户
Write-Host "正在添加学生用户..."
$studentBody = @{
    username = "student"
    password = "123456"
    realName = "王小明"
    role = "student"
    status = 1
} | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method Post -Body $studentBody -ContentType "application/json" -Headers $headers | Out-Null
    Write-Host "学生用户添加成功"
} catch {
    Write-Host "学生用户可能已存在，跳过"
}

Write-Host "`n测试用户添加完成！"
Write-Host "教师账号：teacher / 123456"
Write-Host "学生账号：student / 123456"