# Test Công Thức Mới: Progress = ((targetMinutes - penaltyMinutes) / targetMinutes) * 100%

## Công Thức Mới
```java
// CÔNG THỨC MỚI: Progress = ((targetMinutes - penaltyMinutes) / targetMinutes) * 100%
if (penaltyMinutes >= targetMinutes) {
    progress = 0.0; // Reset về 0%
} else {
    progress = ((targetMinutes - penaltyMinutes) / targetMinutes) * 100.0;
}
```

## Test Cases

### 1. PULSE_RATE (20 phút)
- **Target**: 20 phút
- **0 điếu**: penalty = 0 phút → Progress = ((20-0)/20) × 100 = **100%**
- **0.3 điếu**: penalty = 18 phút < 20 phút → Progress = ((20-18)/20) × 100 = **10%**
- **1 điếu**: penalty = 60 phút > 20 phút → **Reset về 0%**

### 2. OXYGEN_LEVELS (480 phút = 8 giờ)
- **Target**: 480 phút
- **0 điếu**: penalty = 0 phút → Progress = ((480-0)/480) × 100 = **100%**
- **1 điếu**: penalty = 60 phút < 480 phút → Progress = ((480-60)/480) × 100 = **87.5%**
- **5 điếu**: penalty = 300 phút < 480 phút → Progress = ((480-300)/480) × 100 = **37.5%**
- **10 điếu**: penalty = 600 phút > 480 phút → **Reset về 0%**

### 3. CARBON_MONOXIDE (1440 phút = 24 giờ)
- **Target**: 1440 phút
- **0 điếu**: penalty = 0 phút → Progress = ((1440-0)/1440) × 100 = **100%**
- **5 điếu**: penalty = 300 phút < 1440 phút → Progress = ((1440-300)/1440) × 100 = **79.2%**
- **20 điếu**: penalty = 1200 phút < 1440 phút → Progress = ((1440-1200)/1440) × 100 = **16.7%**
- **25 điếu**: penalty = 1500 phút > 1440 phút → **Reset về 0%**

### 4. LUNG_CANCER_RISK (5256000 phút = 10 năm)
- **Target**: 5256000 phút
- **0 điếu**: penalty = 0 phút → Progress = ((5256000-0)/5256000) × 100 = **100%**
- **100 điếu**: penalty = 6000 phút < 5256000 phút → Progress = ((5256000-6000)/5256000) × 100 = **99.9%**
- **1000 điếu**: penalty = 60000 phút < 5256000 phút → Progress = ((5256000-60000)/5256000) × 100 = **98.9%**

## Ưu Điểm Của Công Thức Mới

1. **Đơn giản hơn**: Không cần tính thời gian đã trôi qua
2. **Trực quan hơn**: Penalty trực tiếp làm giảm progress
3. **Dễ hiểu hơn**: Progress = phần còn lại sau khi trừ penalty
4. **Nhất quán**: Áp dụng cho tất cả metrics
5. **Công bằng**: Penalty tỷ lệ thuận với target time

## Điều Kiện Reset
- Khi `penaltyMinutes >= targetMinutes` → Progress = 0%
- Điều này đảm bảo rằng nếu hút quá nhiều, progress sẽ reset về 0%

## Kết Luận
Công thức mới này đơn giản, trực quan và dễ hiểu hơn nhiều so với logic phức tạp trước đó. 