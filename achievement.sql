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

-- Tạo lại bảng follow
CREATE TABLE IF NOT EXISTS follow (
    followerid UUID NOT NULL,
    followingid UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (followerid, followingid),
    FOREIGN KEY (followerid) REFERENCES users(userid) ON DELETE CASCADE,
    FOREIGN KEY (followingid) REFERENCES users(userid) ON DELETE CASCADE
);

-- Tạo lại bảng notification
CREATE TABLE IF NOT EXISTS notification (
    notificationid BIGSERIAL PRIMARY KEY,
    content VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    from_userid UUID,
    is_read BOOLEAN DEFAULT FALSE,
    notification_type VARCHAR(255),
    title VARCHAR(255),
    userid UUID,
    FOREIGN KEY (from_userid) REFERENCES users(userid) ON DELETE SET NULL,
    FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);

-- Bạn có thể chạy đoạn này trong pgAdmin hoặc psql để tạo lại bảng chuẩn ORM. 