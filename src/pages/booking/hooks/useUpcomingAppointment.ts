import { useCallback, useEffect, useState } from "react";
import { getUpcomingAppointments, type UpcomingAppointment, type UpcomingAppointmentsResponse } from "../services/appointmentService";

interface GroupedAppointments {
  [date: string]: UpcomingAppointment[];
}

interface UseUpcomingAppointmentResult {
  groupedAppointments: GroupedAppointments | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
  formatDate: (dateString: string) => string;
  getStatusText: (status: string) => string;
  getStatusColor: (status: string) => string;
}

export const useUpcomingAppointment = (): UseUpcomingAppointmentResult => {
  const [groupedAppointments, setGroupedAppointments] = useState<GroupedAppointments>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [triggerRefetch, setTriggerRefetch] = useState<number>(0);

  // nhóm các cuộc hẹn theo ngày
  const groupAppointmentsByDate = (appointments: UpcomingAppointment[]): GroupedAppointments => {
    const grouped: GroupedAppointments = {};
    appointments.forEach(appointment => {
      const date = appointment.scheduleDate // YYYY-MM-DD
      if (!grouped[date]) {
        grouped[date] = [];
      }
      grouped[date].push(appointment);
    });
    return grouped;
  }

  const fetchAndGroupAppointments = useCallback(async () => {
    setLoading(true);
    setError(null);
    setGroupedAppointments({});

    try {
      // Goi đến API để lấy danh sách cuộc hẹn
      const apiResponse: UpcomingAppointmentsResponse = await getUpcomingAppointments();
      const data = apiResponse.data;

      // Nhóm các cuộc hẹn theo ngày
      const grouped = groupAppointmentsByDate(data.content);
      setGroupedAppointments(grouped);

      console.log("Cuộc hẹn đã được nhóm theo ngày:", grouped);
      console.log("Thông tin phân trang:", apiResponse.data.pageable);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Đã xảy ra lỗi không xác định.');
      }
      console.error("Lỗi trong useUpcomingAppointment:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  // Sử dụng useEffect để gọi hàm fetchAndGroupAppointments khi component mount hoặc triggerRefetch thay đổi
  useEffect(() => {
    fetchAndGroupAppointments();
  }, [fetchAndGroupAppointments, triggerRefetch]);

  const refetch = () => {
    setTriggerRefetch(prev => prev + 1);
  };

  // Format lại date cho các cuộc hẹn
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    const today = new Date();
    const tomorrow = new Date(today.getTime() + (24 * 60 * 60 * 1000)); // Ngày mai

    // Reset giờ để so sánh chỉ dựa trên ngày
    date.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);
    tomorrow.setHours(0, 0, 0, 0);

    if (date.getTime() === today.getTime()) {
      return "Hôm nay";
    } else if (date.getTime() === tomorrow.getTime()) {
      return "Ngày mai";
    } else {
      return date.toLocaleDateString('vi-VN', {
        weekday: 'long',
        day: '2-digit',
        month: '2-digit',
      });
    }
  };

  const getStatusText = (status: string): string => {
    switch (status) {
      case 'MISSED': return 'Bỏ lỡ';
      case 'CANCELLED': return 'Đã hủy';
      case 'COMPLETED': return 'Đã hoàn thành';
      case 'CONFIRMED': return 'Đã xác nhận';
      default: return 'Không xác định';
    }
  }

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'MISSED': return 'bg-orange-100 text-orange-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'CONFIRMED': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  return { groupedAppointments, loading, error, refetch, formatDate, getStatusText, getStatusColor };
}