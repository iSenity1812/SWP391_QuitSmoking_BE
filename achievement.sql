-- Tạo bảng qualification cho coach
CREATE TABLE IF NOT EXISTS qualification (
    coach_id UUID NOT NULL,
    qualification_name VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255),
    qualification_url VARCHAR(512),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    request_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approve_by VARCHAR(36),
    CONSTRAINT pk_qualification PRIMARY KEY (coach_id, qualification_name),
    CONSTRAINT fk_qualification_coach FOREIGN KEY (coach_id) REFERENCES coach(coach_id)
); 