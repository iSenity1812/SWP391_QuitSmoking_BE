# Test Health Metrics System

## Bước 1: Khởi động BE_health
```bash
cd BE_health
mvn spring-boot:run
```

## Bước 2: Test API Endpoints

### 2.1 Test Sample Metrics (Không cần auth)
```bash
curl -X GET "http://localhost:8080/health/test/sample-metrics" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Lấy sample health metrics thành công",
  "data": [
    {
      "id": "uuid",
      "metricType": "PULSE_RATE",
      "currentProgress": 0.0,
      "isCompleted": false,
      "hasRegressed": false,
      "description": "Sau 20 phút, nhịp tim của bạn sẽ trở về bình thường",
      "targetDate": "2024-01-01T10:20:00",
      "achievedDate": null,
      "timeRemainingHours": 0.33,
      "timeRemainingFormatted": "20 phút",
      "displayName": "Nhịp tim"
    }
    // ... 15 more metrics
  ],
  "error": null,
  "errorCode": null,
  "timestamp": "2024-01-01T10:00:00"
}
```

### 2.2 Test Health Overview (Cần auth)
```bash
curl -X GET "http://localhost:8080/health/overview" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2.3 Test Health Metrics (Cần auth)
```bash
curl -X GET "http://localhost:8080/health/metrics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2.4 Test Update Progress (Cần auth)
```bash
curl -X POST "http://localhost:8080/health/update-progress" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Bước 3: Test Tích hợp với QuitPlan

### 3.1 Tạo QuitPlan mới
```bash
curl -X POST "http://localhost:8080/api/quit-plans" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "initialSmokingAmount": 10,
    "cigarettesPerPack": 20,
    "pricePerPack": 50000,
    "reductionType": "IMMEDIATE"
  }'
```

**Expected Result:**
- QuitPlan được tạo thành công
- Health metrics tự động được khởi tạo cho user
- 16 health metrics với progress = 0%

### 3.2 Test Penalty Logic
1. Tạo DailySummary với cigarettes > 0
2. Gọi update-progress API
3. Kiểm tra progress bị giảm do penalty

## Bước 4: Test Countdown Logic

### 4.1 Kiểm tra Progress tăng theo thời gian
- Đợi 1-2 phút
- Gọi lại metrics API
- Progress của PULSE_RATE và BLOOD_PRESSURE sẽ tăng

### 4.2 Test Penalty ảnh hưởng
- Tạo daily_summary với 5 điếu thuốc
- Gọi update-progress
- Target date sẽ bị delay 5 giờ
- Progress sẽ giảm

## Bước 5: Test Edge Cases

### 5.1 Penalty > Target Time
- Tạo daily_summary với rất nhiều điếu thuốc
- Gọi update-progress
- Metric sẽ reset về 0% và bắt đầu lại

### 5.2 Completed Metric bị Penalty
- Đợi metric hoàn thành (progress = 100%)
- Tạo daily_summary với cigarettes > 0
- Gọi update-progress
- Metric sẽ bị regress và target date bị delay

## Bước 6: Verify Database

### 6.1 Kiểm tra health_metrics table
```sql
SELECT * FROM health_metrics WHERE user_id = 'YOUR_USER_ID';
```

### 6.2 Kiểm tra migration
```sql
SELECT * FROM flyway_schema_history WHERE script LIKE '%health_metrics%';
```

## Expected Results:

✅ **Sample API trả về 16 health metrics**  
✅ **Progress tăng theo thời gian thực**  
✅ **Penalty làm delay target date**  
✅ **Countdown độc lập, không bị reset**  
✅ **Tích hợp với QuitPlan hoạt động**  
✅ **Database migration chạy thành công**  

## Troubleshooting:

### Nếu server không khởi động:
1. Kiểm tra port 8080 có bị chiếm không
2. Kiểm tra database connection
3. Xem log lỗi trong console

### Nếu API trả về 404:
1. Kiểm tra endpoint URL đúng không
2. Kiểm tra server đã khởi động chưa
3. Kiểm tra CORS configuration

### Nếu database error:
1. Chạy migration script
2. Kiểm tra database connection
3. Kiểm tra table health_metrics đã được tạo chưa 