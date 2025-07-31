package com.swp391project.SWP391_QuitSmoking_BE.enums;

public enum HealthMetricType {
    
    // Hiệu ứng tức thì (phút đến giờ)
    PULSE_RATE("Nhịp tim", "Sau 20 phút, nhịp tim của bạn sẽ trở về bình thường", 0.33),
    OXYGEN_LEVELS("Nồng độ oxy", "Sau 8 giờ, nồng độ oxy trong máu sẽ trở về bình thường", 8.0),
    CARBON_MONOXIDE("Nồng độ carbon monoxide", "Sau 24 giờ, carbon monoxide từ thuốc lá sẽ được loại bỏ hoàn toàn", 24.0),
    NICOTINE_EXPELLED("Nicotine được đào thải", "Sau 72 giờ, nicotine sẽ được đào thải khỏi cơ thể", 72.0),
    TASTE_SMELL("Vị giác và khứu giác", "Sau 3 ngày, vị giác và khứu giác sẽ được cải thiện đáng kể", 80.0),
    BREATHING("Hô hấp", "Sau 3 ngày 20 giờ, hô hấp sẽ trở về bình thường", 92.0),
    ENERGY_LEVELS("Mức năng lượng", "Sau 4 ngày 20 giờ, mức năng lượng sẽ trở về bình thường", 116.0),
    BAD_BREATH_GONE("Hơi thở hôi", "Sau 7 ngày 20 giờ, hơi thở hôi do thuốc lá sẽ biến mất", 188.0),
    
    // Hiệu ứng ngắn hạn (tuần đến tháng)
    GUMS_TEETH("Nướu và răng", "Sau 14 ngày 20 giờ, lưu thông máu ở nướu và răng sẽ tương tự người không hút thuốc", 356.0),
    TEETH_BRIGHTNESS("Độ trắng răng", "Sau 14 ngày 20 giờ, vết ố vàng trên răng do thuốc lá sẽ không tăng thêm", 356.0),
    CIRCULATION("Tuần hoàn máu", "Sau 2 tháng 28 ngày, tuần hoàn máu sẽ được cải thiện đáng kể", 2016.0),
    GUM_TEXTURE("Kết cấu nướu", "Sau 2 tháng 28 ngày, kết cấu và màu sắc nướu sẽ trở về bình thường", 2016.0),
    IMMUNITY_LUNG_FUNCTION("Miễn dịch và chức năng phổi", "Sau 4 tháng 17 ngày, hệ miễn dịch và chức năng phổi sẽ được cải thiện", 3240.0),
    
    // Hiệu ứng dài hạn (năm)
    HEART_DISEASE_RISK("Giảm nguy cơ bệnh tim", "Sau 1 năm, nguy cơ bệnh tim sẽ giảm một nửa so với người hút thuốc", 8760.0),
    LUNG_CANCER_RISK("Giảm nguy cơ ung thư phổi", "Sau 10 năm, nguy cơ ung thư phổi sẽ giảm một nửa so với người vẫn hút thuốc", 87600.0),
    HEART_ATTACK_RISK("Giảm nguy cơ đau tim", "Sau 15 năm, nguy cơ đau tim sẽ tương đương người chưa từng hút thuốc", 131400.0);
    
    private final String displayName;
    private final String description;
    private final Double targetHours;
    
    HealthMetricType(String displayName, String description, Double targetHours) {
        this.displayName = displayName;
        this.description = description;
        this.targetHours = targetHours;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Double getTargetHours() {
        return targetHours;
    }
} 