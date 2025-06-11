-- Xóa bảng nếu tồn tại
DROP TABLE IF EXISTS Transaction CASCADE;
DROP TABLE IF EXISTS TransactionMethod CASCADE;
DROP TABLE IF EXISTS Member CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS Role CASCADE;

-- Tạo lại bảng
CREATE TABLE Role (
  RoleID SERIAL PRIMARY KEY,
  RoleName VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE "user" (
  UserID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  Username VARCHAR(50) UNIQUE NOT NULL,
  Email VARCHAR(100) UNIQUE NOT NULL,
  PasswordHash VARCHAR(255) NOT NULL,
  CreatedAt TIMESTAMP DEFAULT now(),
  RoleID INT NOT NULL REFERENCES Role(RoleID),
  IsActive BOOLEAN DEFAULT TRUE,
  profilePicture VARCHAR(255),
  NotificationSetting JSON
);

CREATE TABLE Member (
  MemberID UUID PRIMARY KEY,
  SubscriptionID INT,
  StartDate TIMESTAMP,
  EndDate TIMESTAMP,
  SubscriptionStatus BOOLEAN DEFAULT FALSE,
  Streak INT
);

CREATE TABLE TransactionMethod (
  TransactionMethodID SERIAL PRIMARY KEY,
  MethodName VARCHAR(100) UNIQUE NOT NULL,
  IsActive BOOLEAN NOT NULL DEFAULT TRUE,
  Description TEXT
);

CREATE TABLE Transaction (
  TransactionID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  MemberID UUID NOT NULL REFERENCES Member(MemberID),
  TransactionMethodID INT NOT NULL REFERENCES TransactionMethod(TransactionMethodID),
  Amount DECIMAL(10,2) NOT NULL,
  TransactionDate TIMESTAMP DEFAULT now(),
  Status VARCHAR(50)
);

-- Insert dữ liệu mẫu
INSERT INTO Role (RoleName) VALUES
  ('Super Admin'), ('Content Admin'), ('Normal Member'), ('Premium Member'), ('Coach');

INSERT INTO "user" (Username, Email, PasswordHash, RoleID)
VALUES
  ('user1', 'user1@example.com', 'hash1', 3),
  ('user2', 'user2@example.com', 'hash2', 4);

-- Lấy UUID của user vừa tạo để insert vào Member
-- Bạn cần chạy lệnh sau để lấy UUID:
-- SELECT UserID FROM "user";
-- Sau đó thay vào MemberID bên dưới, ví dụ:
-- INSERT INTO Member (MemberID, SubscriptionID, StartDate, EndDate, SubscriptionStatus, Streak)
-- VALUES ('uuid-cua-user1', NULL, now(), NULL, FALSE, 0);

-- Insert TransactionMethod mẫu
INSERT INTO TransactionMethod (MethodName, Description) VALUES
  ('Credit Card', 'Thanh toán qua thẻ tín dụng'),
  ('Paypal', 'Thanh toán qua Paypal'); 