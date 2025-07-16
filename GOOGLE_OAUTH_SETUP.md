# Google OAuth Setup Guide

## 1. Google Cloud Console Setup

### 1.1 Tạo Project
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project có sẵn
3. Enable Google+ API và Google Identity API

### 1.2 Tạo OAuth 2.0 Credentials
1. Vào **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth 2.0 Client IDs**
3. Chọn **Web application**
4. Điền thông tin:
   - **Name**: Quit Smoking App
   - **Authorized JavaScript origins**: 
     - `http://localhost:5173` (development)
     - `http://localhost:3000` (development)
   - **Authorized redirect URIs**:
     - `http://localhost:5173` (development)
     - `http://localhost:3000` (development)

### 1.3 Lấy Client ID và Client Secret
- Copy **Client ID** và **Client Secret**
- Cập nhật vào `application.properties`:
  ```properties
  spring.security.oauth2.client.registration.google.client-id=YOUR_ACTUAL_CLIENT_ID
  spring.security.oauth2.client.registration.google.client-secret=YOUR_ACTUAL_CLIENT_SECRET
  ```

## 2. Database Migration

### 2.1 Chạy Migration SQL
Chạy file `google_oauth_accounts_migration.sql` trong PostgreSQL:

```sql
-- Tạo bảng Google OAuth Accounts
CREATE TABLE IF NOT EXISTS google_oauth_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_id VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    given_name VARCHAR(100),
    family_name VARCHAR(100),
    picture_url VARCHAR(500),
    locale VARCHAR(10),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP,
    user_id UUID NOT NULL,
    
    CONSTRAINT fk_google_oauth_user 
        FOREIGN KEY (user_id) 
        REFERENCES users("UserID") 
        ON DELETE CASCADE
);

-- Tạo indexes
CREATE INDEX IF NOT EXISTS idx_google_oauth_google_id ON google_oauth_accounts(google_id);
CREATE INDEX IF NOT EXISTS idx_google_oauth_user_id ON google_oauth_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_google_oauth_email ON google_oauth_accounts(email);
```

## 3. Frontend Setup

### 3.1 Cài đặt Dependencies
```bash
npm install @react-oauth/google react-hot-toast
```

### 3.2 Cấu hình Google OAuth Provider
Trong `App.tsx`, wrap app với `GoogleOAuthProvider`:
```tsx
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  return (
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      {/* Your app components */}
    </GoogleOAuthProvider>
  );
}
```

### 3.3 Sử dụng Google Login Button
```tsx
import { useGoogleLogin } from '@react-oauth/google';

const googleLogin = useGoogleLogin({
  onSuccess: async (tokenResponse) => {
    // Handle successful login
  },
  onError: (error) => {
    // Handle error
  }
});
```

## 4. Testing

### 4.1 Test Backend
1. Khởi động backend: `mvn spring-boot:run`
2. Test endpoint: `POST /api/auth/google/login`
3. Gửi request với Google ID token

### 4.2 Test Frontend
1. Khởi động frontend: `npm run dev`
2. Click "Đăng nhập với Google"
3. Chọn Google account
4. Kiểm tra redirect và authentication

## 5. Troubleshooting

### 5.1 "OAuth client was not found"
- Kiểm tra Client ID trong Google Cloud Console
- Đảm bảo Authorized JavaScript origins đúng
- Restart backend sau khi cập nhật application.properties

### 5.2 Cross-Origin-Opener-Policy Error
- Đã được xử lý trong vite.config.ts
- Restart frontend dev server

### 5.3 Database Connection Issues
- Kiểm tra PostgreSQL connection
- Chạy migration SQL
- Kiểm tra bảng `google_oauth_accounts` đã được tạo

## 6. Production Deployment

### 6.1 Update URLs
- Thay đổi `localhost` thành domain thật
- Cập nhật Authorized JavaScript origins và redirect URIs
- Cập nhật `application.frontend.url` trong backend

### 6.2 Security
- Sử dụng HTTPS
- Cấu hình CORS đúng
- Bảo mật Client Secret 