import axios from 'axios';

const axiosConfig = axios.create({
  baseURL: 'http://localhost:8080', // Đã bỏ /api ở đây để đúng root path
  timeout: 10000, // 10 seconds timeout
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  }
});

// Thêm Request Interceptor: Thêm JWT token vào header cho mỗi request
axiosConfig.interceptors.request.use(
  (config) => {
    const isAuthEndpoint = config.url?.includes('/auth/login') || config.url?.includes('/auth/register');
    if (!isAuthEndpoint) {
      const token = localStorage.getItem('jwt_token'); // Lấy token từ localStorage
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`; // Thêm token vào header
      }
    }
    return config;
  },
  (err) => {
    return Promise.reject(err);
  }
);

// Thêm Response Interceptor: Xử lý lỗi chung (ví dụ: 401 Unauthorized, 403 Forbidden, 500 Internal Server Error)
axiosConfig.interceptors.response.use(
  (response) => {
    // Trả về response nếu không có lỗi
    return response;
  },
  (err) => {
    if (err.response) {
      // Xử lý lỗi dựa trên mã trạng thái HTTP
      switch (err.response.status) {
        case 401:
          console.error('Unauthorized access - redirecting to login');
          // Có thể redirect đến trang login hoặc thông báo lỗi
          break;
        case 403:
          console.error('Forbidden access - you do not have permission');
          // Có thể thông báo cho người dùng rằng họ không có quyền truy cập
          break;
        case 404:
          console.error('Resource not found - check the URL');
          // Có thể thông báo cho người dùng rằng tài nguyên không tồn tại
          break;
        case 500:
          console.error('Internal Server Error - please try again later');
          // Có thể thông báo cho người dùng rằng có lỗi máy chủ
          break;
        case 503:
          console.error('Service Unavailable - server is down or overloaded');
          // Có thể thông báo cho người dùng rằng dịch vụ hiện không khả dụng
          break;
        case 400:
          console.error('Bad Request - check your input');
          // Có thể thông báo cho người dùng rằng yêu cầu không hợp lệ
          break;
        default:
          console.error('An error occurred:', err.response.data);
      }
    } else {
      console.error('Network error or server is down:', err.message);
    }
    //Trả về lỗi để các component gọi API có thể bắt và xử lý cụ thể
    return Promise.reject(err);
  }
)

export default axiosConfig;