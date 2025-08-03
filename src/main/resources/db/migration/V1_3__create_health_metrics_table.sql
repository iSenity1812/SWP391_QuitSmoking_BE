-- Migration script để tạo health_metrics table
-- V1_3__create_health_metrics_table.sql

CREATE TABLE IF NOT EXISTS health_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    current_progress DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    has_regressed BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    target_date TIMESTAMP NOT NULL,
    achieved_date TIMESTAMP,
    time_remaining_hours DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    CONSTRAINT fk_health_metrics_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_health_metrics_user_type UNIQUE (user_id, metric_type)
);

-- Tạo index để tối ưu performance
CREATE INDEX IF NOT EXISTS idx_health_metrics_user_id ON health_metrics(user_id);
CREATE INDEX IF NOT EXISTS idx_health_metrics_type ON health_metrics(metric_type);
CREATE INDEX IF NOT EXISTS idx_health_metrics_completed ON health_metrics(is_completed);
CREATE INDEX IF NOT EXISTS idx_health_metrics_regressed ON health_metrics(has_regressed); 