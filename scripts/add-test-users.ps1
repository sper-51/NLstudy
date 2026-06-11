# 添加测试用户脚本
# 使用前请确保后端服务已启动

$baseUrl = "http://localhost:8080/api/v1"

# 1. 先登录管理员账号
Write-Host "正在登录管理员账号..."
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/admin/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginResponse.data.token
Write-Host "登录成功，获取到Token"

# 2. 添加教师用户
Write-Host "`n正在添加教师用户..."
$headers = @{
    "Authorization" = "Bearer $token"
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

# 3. 添加学生用户
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