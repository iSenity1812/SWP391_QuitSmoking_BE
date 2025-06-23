Write-Host "Testing Achievement API..." -ForegroundColor Green
Write-Host ""

Write-Host "1. Getting all achievements..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/achievements" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "2. Getting achievement by ID 1..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/achievements/1" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "3. Creating a new test achievement..." -ForegroundColor Yellow
$newAchievement = @{
    name = "Test Achievement"
    iconUrl = "/icons/test.png"
    criteria = '{"type": "daysWithoutSmoking", "value": 5}'
    description = "Test achievement description"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/achievements" -Method Post -Body $newAchievement -ContentType "application/json"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "4. Getting all achievements again to see the new one..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/achievements" -Method Get
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "Test completed!" -ForegroundColor Green
Read-Host "Press Enter to continue" 