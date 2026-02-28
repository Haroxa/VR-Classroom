# save as start-rocketmq.ps1

Write-Host "Starting RocketMQ..." -ForegroundColor Cyan

# Stop and remove old containers
docker stop rmq-nameserver rmq-broker rmq-dashboard 2>$null
docker rm rmq-nameserver rmq-broker rmq-dashboard 2>$null

# 1. Start NameServer
Write-Host "Starting NameServer..." -ForegroundColor Yellow
docker run -d `
  --name rmq-nameserver `
  -p 9876:9876 `
  --restart always `
  apache/rocketmq:5.1.4 `
  sh mqnamesrv

if ($LASTEXITCODE -ne 0) {
    Write-Host "NameServer failed to start" -ForegroundColor Red
    exit 1
}

# 2. Start Broker
Start-Sleep -Seconds 3
Write-Host "Starting Broker..." -ForegroundColor Yellow
docker run -d `
  --name rmq-broker `
  -p 10911:10911 `
  -p 10909:10909 `
  --link rmq-nameserver:nameserver `
  -e "NAMESRV_ADDR=nameserver:9876" `
  -e "JAVA_OPT_EXT=-server -Xms512m -Xmx512m -Xmn256m" `
  --restart always `
  apache/rocketmq:5.1.4 `
  sh mqbroker

if ($LASTEXITCODE -ne 0) {
    Write-Host "Broker failed to start" -ForegroundColor Red
    exit 1
}

# 3. Start Dashboard
Start-Sleep -Seconds 3
Write-Host "Starting Dashboard..." -ForegroundColor Yellow
docker run -d `
  --name rmq-dashboard `
  -p 8080:8080 `
  --link rmq-nameserver:nameserver `
  -e "JAVA_OPTS=-Drocketmq.namesrv.addr=nameserver:9876 -Xms256m -Xmx256m" `
  --restart always `
  apacherocketmq/rocketmq-dashboard:latest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Dashboard failed to start" -ForegroundColor Red
    exit 1
}

# Wait for ready
Start-Sleep -Seconds 5

# Check status
Write-Host "`nChecking status..." -ForegroundColor Green
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host "`nRocketMQ started successfully!" -ForegroundColor Green
Write-Host "NameServer: localhost:9876" -ForegroundColor Cyan
Write-Host "Broker: localhost:10911" -ForegroundColor Cyan
Write-Host "Dashboard: http://localhost:8080" -ForegroundColor Cyan
Write-Host "`nSpringBoot config: rocketmq.name-server=localhost:9876" -ForegroundColor Magenta