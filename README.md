# SWP391 QuitSmoking Backend

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Git

### Run Application
```bash
# Clone and navigate
cd SWP391_QuitSmoking_BE-develop2

# Run application
mvn spring-boot:run -DskipTests
```

## 📋 Features Implemented

### ✅ Core Systems
- **Authentication & User Management**
- **Plan Management & Tracking** 
- **Chat System** (Conversations, Messages, Attachments)
- **Blog System** (Posts, Comments)
- **Achievement System** (Achievements, Member Progress)
- **Notification System**
- **Payment System** (VnPay Integration)

### 🔧 Technical Stack
- Spring Boot 3.5.0
- Java 21
- H2 Database (Development)
- Spring Security + JWT
- VnPay Payment Gateway
- Maven

### 🌐 API Endpoints

#### Health & Status
- `GET /api/health` - System health check
- `GET /api/status` - Service status

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

#### Payment
- `POST /api/payment/create-order` - Create payment order
- `GET /api/payment/vnpay/callback` - VnPay callback

#### Chat System
- `GET /api/chat/conversations` - Get conversations
- `POST /api/chat/messages` - Send message

#### Blog System
- `GET /api/blogs` - Get blog posts
- `POST /api/blogs` - Create blog post

#### Achievement System
- `GET /api/achievements` - Get achievements
- `POST /api/achievements/unlock` - Unlock achievement

## 💳 Payment Configuration

### VnPay Sandbox
```properties
vnpay.tmn-code=2QXUI4J4
vnpay.hash-secret=KNCSMSHUXNKMWVZQKQZRSIRBQVHXUTBT
vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=http://localhost:8080/api/payment/vnpay/callback
```

## 🧪 Testing

```bash
# Run tests
mvn test

# Run specific test
mvn test -Dtest=PaymentSystemTest
```

## 📊 Database

- **Development**: H2 In-Memory Database
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)

## 🔒 Security

- JWT Authentication
- CORS enabled for `http://localhost:3000`
- Password encryption with BCrypt

## 📝 Default Users

- **Super Admin**: `superadmin` / (auto-generated password)

## 🚨 Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Database Issues
```bash
# Reset H2 database (restart application)
mvn clean spring-boot:run
```

### VnPay Payment Issues
- Check sandbox credentials
- Verify return URL configuration
- Test with minimum amount: 10,000 VND

## 📞 Support

- Backend runs on: http://localhost:8080
- Health check: http://localhost:8080/api/health
- API docs: http://localhost:8080/swagger-ui.html (if configured) 