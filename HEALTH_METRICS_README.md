# Health Metrics System - BE_health

## Tổng quan
Hệ thống Health Metrics đã được implement thành công trong BE_health với đầy đủ các tính năng:

### ✅ Đã hoàn thành:
1. **HealthMetric Entity** - JPA entity cho health_metrics table
2. **HealthMetricRepository** - Repository với các method cần thiết
3. **HealthMetricService** - Service với logic penalty và progress calculation
4. **HealthController** - REST API endpoints
5. **Database Migration** - V1_3__create_health_metrics_table.sql
6. **Tích hợp với QuitPlanService** - Tự động khởi tạo health metrics khi tạo quit plan

### 🔧 Tính năng chính:

#### 1. **Penalty System**
- 1 điếu thuốc = 60 phút penalty
- Penalty không thể vượt quá target time của metric
- Progress không bao giờ < 0%

#### 2. **Target Date Logic**
- **Trường hợp 1**: Penalty < target time → Cộng penalty vào target date hiện tại
- **Trường hợp 2**: Penalty >= target time → Reset về 0% và bắt đầu lại
- **Trường hợp 3**: Metric đã completed → Target date = created_at của daily_summary + penalty

#### 3. **Progress Calculation**
- Progress = ((target_time - time_remaining) / target_time) * 100%
- Progress luôn trong khoảng 0% - 100%
- Countdown độc lập, không bị reset khi refresh

### 📊 Health Metrics Types:
1. **Immediate (20 phút)**: PULSE_RATE, BLOOD_PRESSURE
2. **Short-term (8-72 giờ)**: OXYGEN_LEVELS, CARBON_MONOXIDE, NICOTINE, SENSE_OF_TASTE, SENSE_OF_SMELL, BREATHING, COUGHING, ENERGY_LEVELS
3. **Medium-term (2-4 tuần)**: STRESS_REDUCTION, CIRCULATION, LUNG_FUNCTION, SKIN_IMPROVEMENT
4. **Long-term (1-10 năm)**: HEART_ATTACK_RISK, CANCER_RISK

### 🚀 API Endpoints:

#### 1. **GET /health/overview**
- Lấy tổng quan health metrics của user
- Response: HealthOverviewDTO với totalMetrics, completedMetrics, overallProgress

#### 2. **GET /health/metrics**
- Lấy tất cả health metrics của user
- Response: List<HealthMetricDTO>

#### 3. **POST /health/update-progress**
- Cập nhật progress dựa trên penalty từ daily_summary
- Tính toán lại target_date và progress

#### 4. **GET /health/test/sample-metrics**
- Test endpoint để lấy sample data
- Không cần authentication

### 🗄️ Database Schema:
```sql
CREATE TABLE health_metrics (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    current_progress DOUBLE PRECISION DEFAULT 0.0,
    is_completed BOOLEAN DEFAULT FALSE,
    has_regressed BOOLEAN DEFAULT FALSE,
    description TEXT,
    target_date TIMESTAMP NOT NULL,
    achieved_date TIMESTAMP,
    time_remaining_hours DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 🔄 Tích hợp với Frontend:
- Frontend đã có sẵn health metrics UI components
- Chỉ cần cập nhật API endpoints từ `/api/health` sang `/health`
- Countdown timer độc lập, không bị reset

### 🧪 Testing:
1. **Test tạo quit plan**: Health metrics sẽ tự động được khởi tạo
2. **Test penalty**: Tạo daily_summary với cigarettes > 0, gọi update-progress API
3. **Test countdown**: Progress sẽ tăng theo thời gian thực

### 📝 Lưu ý:
- Health metrics chỉ được tạo khi user có quit plan
- Penalty được tính từ tổng số điếu thuốc trong daily_summary
- Countdown hoạt động độc lập, không phụ thuộc vào refresh
- Progress luôn chính xác dựa trên target_date và thời gian hiện tại

### 🎯 Kết quả:
✅ **BE_health đã compile thành công**  
✅ **Health metrics system hoàn chỉnh**  
✅ **Logic penalty và progress chính xác**  
✅ **Tích hợp với QuitPlanService**  
✅ **API endpoints sẵn sàng sử dụng**  
✅ **Database migration đã sẵn sàng**  

**Hệ thống đã sẵn sàng để deploy và sử dụng!** 