# SWP391 Chat Service

Ứng dụng chat sử dụng Spring Boot WebSocket và PostgreSQL để tạo các chức năng chat giữa người dùng.

## Yêu cầu

- Java 17+
- Maven
- PostgreSQL

## Cấu trúc ứng dụng

```
└── swp391-chat
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com
    │   │   │       └── swp391
    │   │   │           └── chat
    │   │   │               ├── config
    │   │   │               │   ├── WebConfig.java
    │   │   │               │   ├── WebSocketConfig.java
    │   │   │               │   └── WebSocketEventListener.java
    │   │   │               ├── controller
    │   │   │               │   └── ChatController.java
    │   │   │               ├── model
    │   │   │               │   ├── ChatAttachment.java
    │   │   │               │   ├── ChatConversation.java
    │   │   │               │   ├── ChatMessage.java
    │   │   │               │   ├── User.java
    │   │   │               │   └── dto
    │   │   │               │       ├── ChatConversationDTO.java
    │   │   │               │       ├── ChatMessageDTO.java
    │   │   │               │       └── UserDTO.java
    │   │   │               ├── repository
    │   │   │               │   ├── ChatConversationRepository.java
    │   │   │               │   ├── ChatMessageRepository.java
    │   │   │               │   └── UserRepository.java
    │   │   │               ├── service
    │   │   │               │   ├── ChatService.java
    │   │   │               │   └── impl
    │   │   │               │       └── ChatServiceImpl.java
    │   │   │               └── ChatApplication.java
    │   │   └── resources
    │   │       ├── application.properties
    │   │       ├── schema.sql
    │   │       └── static
    │   │           └── index.html
    │   └── test
    │       └── java
    │           └── com
    │               └── swp391
    │                   └── chat
    │                       └── ChatApplicationTests.java
    ├── pom.xml
    └── README.md
```

## Cấu hình cơ sở dữ liệu

1. Tạo cơ sở dữ liệu PostgreSQL:

```sql
CREATE DATABASE swp391db;
```

2. Đảm bảo cập nhật file `application.properties` với thông tin đăng nhập PostgreSQL của bạn:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/swp391db
spring.datasource.username=<tên_người_dùng>
spring.datasource.password=<mật_khẩu>
```

## Cách chạy ứng dụng

1. Di chuyển đến thư mục dự án:

```bash
cd swp391-chat
```

2. Build dự án với Maven:

```bash
mvn clean install
```

3. Chạy ứng dụng:

```bash
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại địa chỉ: http://localhost:8080/api

## Kiểm tra WebSocket

### Sử dụng trang kiểm tra có sẵn

Truy cập trang kiểm tra tích hợp tại: http://localhost:8080/api/index.html

1. Nhập UUID và tên của người gửi và người nhận (bạn có thể tạo UUID bằng công cụ trực tuyến hoặc dùng UUID từ cơ sở dữ liệu)
2. Nhấn "Kết nối" để thiết lập kết nối WebSocket
3. Nhập tin nhắn và nhấn "Gửi" để kiểm tra gửi tin nhắn

### Kiểm tra với công cụ khác

Bạn có thể sử dụng các công cụ như [Postman](https://www.postman.com/) hoặc [WebSocket King Client](https://websocketking.com/) để kiểm tra WebSocket.

### Kiểm tra với cURL

#### 1. Tạo người dùng mới (giả định bạn đã có API tạo người dùng):

```bash
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"username":"user1","email":"user1@example.com","password":"password123"}'
```

#### 2. Tạo cuộc trò chuyện mới:

```bash
curl -X POST "http://localhost:8080/api/chat/conversations?initiatorId=<UUID_NGƯỜI_DÙNG_1>&recipientId=<UUID_NGƯỜI_DÙNG_2>"
```

#### 3. Lấy danh sách cuộc trò chuyện của người dùng:

```bash
curl -X GET http://localhost:8080/api/chat/conversations/<UUID_NGƯỜI_DÙNG>
```

#### 4. Lấy tin nhắn trong cuộc trò chuyện:

```bash
curl -X GET "http://localhost:8080/api/chat/conversations/<CONVERSATION_ID>/messages?page=0&size=20"
```

## Giải thích API WebSocket

| Endpoint | Mô tả |
|----------|-------|
| `/app/chat.addUser` | Đăng ký người dùng với WebSocket |
| `/app/chat.sendMessage` | Gửi tin nhắn đến người dùng khác |
| `/user/{userId}/queue/messages` | Nhận tin nhắn cá nhân |
| `/topic/public` | Kênh thông báo công khai |

## REST API Endpoints

| HTTP Method | Endpoint | Mô tả |
|-------------|----------|-------|
| GET | `/api/chat/conversations/{userId}` | Lấy tất cả cuộc trò chuyện của người dùng |
| GET | `/api/chat/conversations/{conversationId}/messages` | Lấy tin nhắn trong cuộc trò chuyện |
| POST | `/api/chat/conversations?initiatorId={id1}&recipientId={id2}` | Tạo cuộc trò chuyện mới |
| GET | `/api/chat/find-conversation?user1Id={id1}&user2Id={id2}` | Tìm cuộc trò chuyện giữa hai người dùng |

## Thêm dữ liệu mẫu vào PostgreSQL

Bạn có thể thêm dữ liệu mẫu vào cơ sở dữ liệu để kiểm tra ứng dụng:

1. Tạo người dùng:

```sql
INSERT INTO users (user_id, username, email, password_hash, role_id, is_active, profile_picture)
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'user1', 'user1@example.com', '$2a$10$Vc5TUX/3NNxUBR5RMP/tHOUVwOrsMbSVVnZv5QMRVw6UXnSvnVq8a', 3, true, 'profile1.jpg'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'user2', 'user2@example.com', '$2a$10$Vc5TUX/3NNxUBR5RMP/tHOUVwOrsMbSVVnZv5QMRVw6UXnSvnVq8a', 3, true, 'profile2.jpg');
```

2. Tạo cuộc trò chuyện:

```sql
INSERT INTO chat_conversation (initiator_id, recipient_id, created_at, last_message_date)
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', NOW(), NOW());
```

3. Tạo tin nhắn mẫu:

```sql
INSERT INTO chat_message (conversation_id, sender_id, receiver_id, content, sent_date)
VALUES 
(1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Xin chào!', NOW()),
(1, 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Chào bạn!', NOW());
```

## Giải quyết sự cố

- **Lỗi kết nối cơ sở dữ liệu:** Kiểm tra thông tin kết nối trong file `application.properties`
- **Lỗi WebSocket không kết nối:** Kiểm tra URL kết nối, đảm bảo đúng định dạng `/api/ws`
- **Không nhận được tin nhắn:** Kiểm tra đăng ký đúng kênh (`/user/{userId}/queue/messages`) 