-- Create health_metrics table
CREATE TABLE health_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    current_progress DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    target_date TIMESTAMP,
    achieved_date TIMESTAMP,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    time_remaining_hours BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_health_metrics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_health_metrics_user_type UNIQUE (user_id, metric_type),
    CONSTRAINT chk_health_metrics_progress CHECK (current_progress >= 0.0 AND current_progress <= 100.0)
);

-- Create indexes for better performance
CREATE INDEX idx_health_metrics_user_id ON health_metrics(user_id);
CREATE INDEX idx_health_metrics_type ON health_metrics(metric_type);
CREATE INDEX idx_health_metrics_completed ON health_metrics(is_completed);
CREATE INDEX idx_health_metrics_target_date ON health_metrics(target_date);

-- Add comments
COMMENT ON TABLE health_metrics IS 'Bảng lưu trữ các chỉ số sức khỏe của người dùng sau khi bỏ thuốc';
COMMENT ON COLUMN health_metrics.id IS 'ID duy nhất của health metric';
COMMENT ON COLUMN health_metrics.user_id IS 'ID của người dùng';
COMMENT ON COLUMN health_metrics.metric_type IS 'Loại chỉ số sức khỏe (PULSE_RATE, OXYGEN_LEVELS, etc.)';
COMMENT ON COLUMN health_metrics.current_progress IS 'Tiến độ hiện tại (0.0 - 100.0)';
COMMENT ON COLUMN health_metrics.target_date IS 'Ngày dự kiến đạt được milestone';
COMMENT ON COLUMN health_metrics.achieved_date IS 'Ngày thực tế đạt được milestone';
COMMENT ON COLUMN health_metrics.is_completed IS 'Trạng thái hoàn thành';
COMMENT ON COLUMN health_metrics.description IS 'Mô tả chi tiết về milestone';
COMMENT ON COLUMN health_metrics.time_remaining_hours IS 'Số giờ còn lại để đạt được milestone';
COMMENT ON COLUMN health_metrics.created_at IS 'Thời gian tạo';
COMMENT ON COLUMN health_metrics.updated_at IS 'Thời gian cập nhật cuối'; 