// src/types/auth.d.ts

export type Role = 'NORMAL_MEMBER' | 'PREMIUM_MEMBER' | 'SUPER_ADMIN' | 'CONTENT_ADMIN' | 'COACH';

// Dữ liệu trả về từ Spring Boot sau khi đăng nhập/đăng ký thành công
export interface AccountResponse {
  userId: string; //UUID của người dùng
  username: string;
  email: string;
  profilePicture?: string | null; // URL của ảnh đại diện người dùng, có thể null
  role: Role; // Quyền của người dùng
  isActive: boolean; // Trạng thái hoạt động của tài khoản
  token: string; // Token JWT để xác thực người dùng
}

// Dữ liệu gửi đi khi đăng ký tài khoản mới
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

// Dữ liệu gửi đi khi đăng nhập
export interface LoginRequest {
  email: string;
  password: string;
}

// Kiểu cho phản hồi lỗi từ API (nếu Spring Boot của bạn trả về cấu trúc lỗi này)
// Dựa trên cách bạn cấu hình GlobalExceptionHandler
export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: number;
}

export interface ApiResponse<T> {
  status: number;      // Mã trạng thái tùy chỉnh của bạn (1073741824, v.v.)
  message: string;     // Thông báo tổng quan từ server
  data: T | null;      // Dữ liệu khi request thành công, hoặc null khi có lỗi
  error?: string | { [key: string]: string } | null;
  errorCode: string | null; // Mã lỗi tùy chỉnh (ví dụ: "DUPLICATE_EMAIL", "INVALID_CREDENTIALS")
  timestamp: string;   // Thời gian xảy ra response (ISO 8601 string)
}

export interface ValidationApiError {
  status: number;
  message: string;
  data: null;
  error: {
    [key: string]: string; // Map của các lỗi validation (fieldName: errorMessage)
  };
  errorCode: string;
  timestamp: string;
}