# stop-rocketmq.ps1
param(
    [switch]$Delete  # 加 -Delete 参数会删除容器
)

Write-Host "Stopping RocketMQ containers..." -ForegroundColor Cyan

docker stop rmq-nameserver rmq-broker rmq-dashboard | Out-Null

if ($Delete) {
    Write-Host "Deleting containers..." -ForegroundColor Yellow
    docker rm rmq-nameserver rmq-broker rmq-dashboard | Out-Null
    Write-Host "Containers deleted!" -ForegroundColor Green
} else {
    Write-Host "Containers stopped (保留，下次快速启动)" -ForegroundColor Green
}

docker ps --filter "name=rmq" --format "table {{.Names}}\t{{.Status}}"