# 测试脚本 - test-api.ps1

# 基础URL
$baseUrl = "http://localhost:8080/api"

# 测试token (实际测试时需要替换为真实token)
$token = "Bearer your-token-here"

# 测试结果存储
$testResults = @()

# 测试函数
function Test-API {
    param(
        [string]$method,
        [string]$endpoint,
        [object]$body = $null,
        [bool]$requireAuth = $false
    )
    
    $headers = @{}
    if ($requireAuth) {
        $headers["Authorization"] = $token
    }
    
    try {
        if ($body) {
            $jsonBody = $body | ConvertTo-Json
            $response = Invoke-WebRequest -Uri "$baseUrl$endpoint" -Method $method -Headers $headers -Body $jsonBody -ContentType "application/json" -UseBasicParsing
        } else {
            $response = Invoke-WebRequest -Uri "$baseUrl$endpoint" -Method $method -Headers $headers -UseBasicParsing
        }
        
        $result = @{
            endpoint = $endpoint
            method = $method
            status = "Success"
            statusCode = $response.StatusCode
            response = $response.Content
        }
        
        $testResults += $result
        Write-Host "✅ $method $endpoint - Success" -ForegroundColor Green
    } catch {
        $result = @{
            endpoint = $endpoint
            method = $method
            status = "Failed"
            statusCode = $_.Exception.Response.StatusCode
            error = $_.Exception.Message
        }
        
        $testResults += $result
        Write-Host "❌ $method $endpoint - Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试 GET /api/posts
Write-Host "\n=== 测试 GET /api/posts ===" -ForegroundColor Cyan
Test-API -method "GET" -endpoint "/posts?page=0"

# 测试 POST /api/posts (需要认证)
Write-Host "\n=== 测试 POST /api/posts ===" -ForegroundColor Cyan
$postBody = @{
    title = "测试帖子"
    content = "测试内容"
    images = @("images/test.png")
    likeCount = 0
}
Test-API -method "POST" -endpoint "/posts" -body $postBody -requireAuth $true

# 测试 GET /api/posts/{id}
Write-Host "\n=== 测试 GET /api/posts/{id} ===" -ForegroundColor Cyan
Test-API -method "GET" -endpoint "/posts/1"

# 测试 GET /api/comments
Write-Host "\n=== 测试 GET /api/comments ===" -ForegroundColor Cyan
$commentBody = @{
    postId = "1"
    page = 0
}
Test-API -method "GET" -endpoint "/comments" -body $commentBody

# 测试 POST /api/comments (需要认证)
Write-Host "\n=== 测试 POST /api/comments ===" -ForegroundColor Cyan
$newCommentBody = @{
    content = "测试评论"
    postId = 1
}
Test-API -method "POST" -endpoint "/comments" -body $newCommentBody -requireAuth $true

# 测试 GET /api/users/posts (需要认证)
Write-Host "\n=== 测试 GET /api/users/posts ===" -ForegroundColor Cyan
$userPostsBody = @{
    page = 0
}
Test-API -method "GET" -endpoint "/users/posts" -body $userPostsBody -requireAuth $true

# 测试 GET /api/users/comments (需要认证)
Write-Host "\n=== 测试 GET /api/users/comments ===" -ForegroundColor Cyan
$userCommentsBody = @{
    page = 0
}
Test-API -method "GET" -endpoint "/users/comments" -body $userCommentsBody -requireAuth $true

# 测试 GET /api/oss/sign
Write-Host "\n=== 测试 GET /api/oss/sign ===" -ForegroundColor Cyan
Test-API -method "GET" -endpoint "/oss/sign"

# 输出测试结果
Write-Host "\n=== 测试结果汇总 ===" -ForegroundColor Cyan
$testResults | Format-Table -AutoSize

# 统计成功和失败的测试用例
$successCount = ($testResults | Where-Object { $_.status -eq "Success" }).Count
$failedCount = ($testResults | Where-Object { $_.status -eq "Failed" }).Count

Write-Host "\n测试完成: 成功 $successCount 个，失败 $failedCount 个" -ForegroundColor Yellow