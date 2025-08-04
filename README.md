# BE_combined - Quit Smoking Backend

## Mô tả
BE_combined là sự kết hợp của:
- **BE_s**: Tất cả các module hoạt động tốt (Blog, VNPay, Leaderboard, Follow, Achievement)
- **Health Metrics**: Module health_metrics từ BE_health_new

## Cấu trúc

### ✅ Các module hoạt động tốt (từ BE_s):
- **Blog System**: Quản lý bài viết, comments
- **VNPay Payment**: Thanh toán online
- **Leaderboard**: Bảng xếp hạng người dùng
- **Follow System**: Hệ thống follow/unfollow
- **Achievement System**: Hệ thống thành tựu
- **User Management**: Quản lý người dùng
- **Quit Plan**: Kế hoạch bỏ thuốc
- **Daily Summary**: Tóm tắt hàng ngày
- **Coach System**: Hệ thống coach
- **Appointment**: Đặt lịch hẹn

### ✅ Health Metrics (từ BE_health_new):
- **HealthMetric Entity**: Theo dõi tiến độ sức khỏe
- **HealthMetricService**: Logic tính toán health metrics
- **HealthController**: API endpoints cho health
- **Health DTOs**: Data transfer objects

## Tích hợp

### Health Metrics Integration:
- **QuitPlanService**: Tự động khởi tạo health metrics khi tạo quit plan mới
- **HealthMetricService**: Tính toán tiến độ dựa trên quit date và penalty
- **16 loại health metrics**: Từ PULSE_RATE đến HEART_ATTACK_RISK

## Cách chạy

```bash
# Compile
mvn clean compile

# Run
mvn spring-boot:run
```

## Database
- **Database**: QuitSmokingDB
- **DDL Auto**: update (tự động tạo bảng)
- **Port**: 8080

## API Endpoints

### Health Metrics:
- `GET /api/health/overview` - Lấy tổng quan health
- `GET /api/health/metrics` - Lấy tất cả health metrics
- `POST /api/health/update-progress` - Cập nhật tiến độ

### Các endpoints khác:
- Blog: `/api/blogs/*`
- VNPay: `/api/vnpay/*`
- Leaderboard: `/api/leaderboard/*`
- Follow: `/api/follow/*`
- Achievement: `/api/achievements/*`

## Lưu ý
- Tất cả module từ BE_s hoạt động bình thường
- Health metrics được tích hợp tự động khi tạo quit plan
- Không có conflict giữa các module 