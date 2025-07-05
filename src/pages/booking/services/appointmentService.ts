import axiosConfig from "@/config/axiosConfig"
import { authService } from "@/services/authService"
import { isAxiosError } from "axios"

export interface UpcomingAppointment {
  appointmentId: number
  fullName: string
  email: string
  scheduleDate: string
  timeSlot: {
    timeSlotId: number
    label: string
    startTime: string
    endTime: string
    deleted: boolean
  }
  status: 'CONFIRMED' | 'MISSED' | 'CANCELLED' | 'COMPLETED'
  note: string
  bookingTime: string
}

export interface UpcomingAppointmentsData {
  content: UpcomingAppointment[]
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      sorted: boolean
      unsorted: boolean
      empty: boolean
    }
    offset: number
    unpaged: boolean
    paged: boolean
  }
  last: boolean
  totalPages: number
  totalElements: number
  first: boolean
  numberOfElements: number
  size: number
  number: number
  sort: {
    sorted: boolean
    unsorted: boolean
    empty: boolean
  }
  empty: boolean
}

export interface UpcomingAppointmentsResponse {
  status: number;
  message: string;
  data: UpcomingAppointmentsData; // <--- Sửa ở đây để có lớp 'data'
  error: null; // Có thể là 'object | null' nếu lỗi có cấu trúc
  errorCode: null; // Có thể là 'string | null'
  timestamp: string;
}


export const getUpcomingAppointments = async (): Promise<UpcomingAppointmentsResponse> => {
  try {
    const token = authService.getToken()
    if (!token) throw new Error("Người dùng chưa đăng nhập")

    // Helper to format date as YYYY-MM-DD in local timezone
    const formatDate = (date: Date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    // Tính date range (today + 2 days)
    const now = new Date()
    const startDate = formatDate(now);
    const endDate = formatDate(new Date(now.getTime() + 2 * (24 * 60 * 60 * 1000)));
    const response = await axiosConfig.get<UpcomingAppointmentsResponse>(`/appointments/my-appointments?startDate=${startDate}&endDate=${endDate}&page=0&size=10&sort=coachSchedule.scheduleDate&sort=asc`); // Ko cần truyền token vì axiosConfig đã tự động thêm header Authorization
    return response.data;
  } catch (error: unknown) {
    console.error("Lỗi khi tải các cuộc hẹn sắp tới:", error);
    if (isAxiosError(error)) {
      // Nếu là lỗi Axios, trả về thông báo lỗi từ response
      if (error.response) {
        // Lỗi từ server (có phản hồi HTTP error status code)
        console.error("Phản hồi lỗi từ server:", error.response.data);
        console.error("Mã trạng thái HTTP:", error.response.status);
        // Bạn có thể ném một lỗi cụ thể hơn hoặc trả về một giá trị lỗi mặc định
        throw new Error(`Lỗi từ máy chủ: ${error.response.status} - ${error.response.data?.message || 'Lỗi không xác định.'}`);
      } else if (error.request) {
        // Yêu cầu đã được gửi nhưng không nhận được phản hồi (ví dụ: mạng bị ngắt)
        console.error("Không nhận được phản hồi từ server.");
        throw new Error("Lỗi mạng hoặc máy chủ không phản hồi.");
      } else {
        // Lỗi trong quá trình thiết lập yêu cầu Axios
        console.error("Lỗi thiết lập yêu cầu:", error.message);
        throw new Error(`Lỗi khi tạo yêu cầu: ${error.message}`);
      }
    } else {
      // Nếu không phải là lỗi Axios, có thể là lỗi khác
      if (typeof error === "object" && error !== null && "message" in error) {
        console.error("Lỗi không phải từ Axios:", (error as { message: string }).message);
      } else {
        console.error("Lỗi không phải từ Axios:", error);
      }
      throw error;
    }
  }
}