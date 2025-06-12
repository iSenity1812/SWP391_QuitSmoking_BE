-- Tạo bảng User
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    profile_picture VARCHAR(255),
    notification_setting JSON
);

-- Tạo bảng Role
CREATE TABLE IF NOT EXISTS role (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

-- Tạo bảng chat_conversation
CREATE TABLE IF NOT EXISTS chat_conversation (
    conversation_id SERIAL PRIMARY KEY,
    initiator_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_message_date TIMESTAMP,
    FOREIGN KEY (initiator_id) REFERENCES users(user_id),
    FOREIGN KEY (recipient_id) REFERENCES users(user_id)
);

-- Tạo bảng chat_message
CREATE TABLE IF NOT EXISTS chat_message (
    message_id SERIAL PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    content TEXT NOT NULL,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES chat_conversation(conversation_id),
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

-- Tạo bảng chat_attachment
CREATE TABLE IF NOT EXISTS chat_attachment (
    attachment_id SERIAL PRIMARY KEY,
    message_id INT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (message_id) REFERENCES chat_message(message_id)
);

-- Tạo chỉ mục (indexes) để tối ưu truy vấn
CREATE INDEX IF NOT EXISTS idx_chat_conversation_initiator ON chat_conversation(initiator_id);
CREATE INDEX IF NOT EXISTS idx_chat_conversation_recipient ON chat_conversation(recipient_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_conversation ON chat_message(conversation_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_sender ON chat_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_receiver ON chat_message(receiver_id);
CREATE INDEX IF NOT EXISTS idx_chat_attachment_message ON chat_attachment(message_id);

-- Dữ liệu mẫu cho bảng Role
INSERT INTO role (role_id, role_name) VALUES 
(1, 'Super Admin'),
(2, 'Content Admin'),
(3, 'Normal Member'),
(4, 'Premium Member'),
(5, 'Coach')
ON CONFLICT (role_id) DO NOTHING; 