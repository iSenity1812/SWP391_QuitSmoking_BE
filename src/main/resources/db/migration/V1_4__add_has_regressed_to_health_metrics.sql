-- Add has_regressed column to health_metrics table
ALTER TABLE health_metrics 
ADD COLUMN has_regressed BOOLEAN NOT NULL DEFAULT FALSE;

-- Add comment
COMMENT ON COLUMN health_metrics.has_regressed IS 'Đánh dấu health metric đã từng tụt xuống dưới 100% sau khi hoàn thành'; 