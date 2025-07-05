import { Calendar, CalendarDays, Clock, Loader2, Mail, RefreshCcw, User, Video } from "lucide-react"
import { useUpcomingAppointment } from "../hooks/useUpcomingAppointment"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";

interface AppointmentCardProps {
  appointment: {
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
    status: string
    note: string
    bookingTime: string
  }
  getStatusColor: (status: string) => string
  getStatusText: (status: string) => string
  formatDate: (date: string) => string
}
const AppointmentCard = ({ appointment, getStatusText, getStatusColor, formatDate }: AppointmentCardProps) => {
  const [isJoinable, setIsJoinable] = useState(false);

  useEffect(() => {
    const checkJoinability = () => {
      // Nút chỉ hiển thị cho các cuộc hẹn đã 'CONFIRMED'
      if (appointment.status !== 'CONFIRMED') {
        setIsJoinable(false);
        return;
      }

      const now = new Date();
      const appointmentStart = new Date(`${appointment.scheduleDate}T${appointment.timeSlot.startTime}`);
      const appointmentEnd = new Date(`${appointment.scheduleDate}T${appointment.timeSlot.endTime}`);

      // Tính thời điểm 10 phút trước khi cuộc hẹn bắt đầu
      const tenMinutesBefore = new Date(appointmentStart.getTime() - 10 * 60 * 1000);
      // Thời gian kết thúc của cuộc hẹn + 5 phút
      const fiveMinutesAfter = new Date(appointmentEnd.getTime() + 5 * 60 * 1000);

      // Nút sẽ hiển thị trong khoảng từ 10 phút trước giờ hẹn cho đến khi kết thúc
      const shouldBeJoinable = now >= tenMinutesBefore && now <= fiveMinutesAfter;

      // Chỉ cập nhật state nếu giá trị thay đổi để tránh re-render không cần thiết
      if (isJoinable !== shouldBeJoinable) {
        setIsJoinable(shouldBeJoinable);
      }
    };

    // Kiểm tra ngay khi component được mount
    checkJoinability();

    // Sau đó kiểm tra lại mỗi 30 giây để cập nhật trạng thái nút
    const intervalId = setInterval(checkJoinability, 30000); // 30 giây

    // Dọn dẹp interval khi component bị unmount
    return () => clearInterval(intervalId);
  }, [appointment, isJoinable]);

  // Hàm tiện ích mới để cắt bỏ giây từ chuỗi thời gian (nếu chưa có ở đâu khác)
  const formatTime = (timeString: string): string => {
    if (!timeString) return '';
    const parts = timeString.split(':');
    return parts.length >= 2 ? `${parts[0]}:${parts[1]}` : timeString;
  };

  return (
    <div className="
      p-4 bg-white/70 dark:bg-slate-800/70 rounded-xl shadow-lg border border-emerald-100 dark:border-slate-700/50
      hover:bg-white dark:hover:bg-slate-700 transition-all duration-300 transform hover:scale-105
      flex flex-col
    ">
      {/* Header Section: Tên & Trạng thái */}
      <div className="flex items-center justify-between pb-3 mb-3 border-b border-gray-100 dark:border-slate-700">
        <div className="flex items-center space-x-3">
          <User className="w-6 h-6 text-emerald-600 dark:text-emerald-400 flex-shrink-0" /> {/* Icon lớn hơn */}
          <span className="font-bold text-lg text-slate-800 dark:text-slate-200 leading-tight">
            {appointment.fullName}
          </span>
        </div>
        <Badge className={getStatusColor(appointment.status)}>
          {getStatusText(appointment.status)}
        </Badge>
      </div>

      {/* Body Section: Thông tin chi tiết */}
      <div className="flex-grow space-y-2 text-sm text-slate-700 dark:text-slate-300"> {/* flex-grow để đẩy ghi chú xuống cuối */}
        <div className="flex items-center space-x-2">
          <Clock className="w-5 h-5 text-emerald-500 dark:text-emerald-400 flex-shrink-0" />
          <span className="font-medium">Ngày:</span>
          <span>{formatDate(appointment.scheduleDate)}</span> {/* Sử dụng formatDate */}
        </div>
        <div className="flex items-center space-x-2">
          <Clock className="w-5 h-5 text-emerald-500 dark:text-emerald-400 flex-shrink-0" />
          <span className="font-medium">Thời gian:</span>
          <span>{formatTime(appointment.timeSlot.startTime)} - {formatTime(appointment.timeSlot.endTime)}</span>
        </div>
        <div className="flex items-center space-x-2">
          <Mail className="w-5 h-5 text-emerald-500 dark:text-emerald-400 flex-shrink-0" /> {/* Icon email */}
          <span className="font-medium">Email:</span>
          <span className="break-all">{appointment.email}</span> {/* break-all để ngắt dòng email dài */}
        </div>
      </div>

      {/* Note Section: Ghi chú */}
      {appointment.note && (
        <div className="
          text-sm text-slate-600 dark:text-slate-400 bg-emerald-50 dark:bg-slate-700/50 rounded-lg p-3 mt-4
          border border-emerald-100 dark:border-slate-600
        ">
          <span className="font-semibold text-emerald-800 dark:text-emerald-300 block mb-1">Ghi chú:</span>
          <p className="text-slate-700 dark:text-slate-300 leading-snug">{appointment.note}</p> {/* leading-snug để khoảng cách dòng vừa phải */}
        </div>
      )}

      {/* Meeting */}
      {isJoinable && (
        <div className="mt-4 flex justify-end">
          <Link
            to={`/meeting/${appointment.appointmentId}`}
            className="text-emerald-600 hover:text-emerald-700 dark:text-emerald-400 dark:hover:text-emerald-300"
            target="_blank">
            <Button variant="outline" size="sm" className="flex items-center space-x-2">
              <Video className="w-4 h-4 mr-2" />
              <span>Tham gia cuộc gọi</span>
            </Button>
          </Link>
        </div>
      )}
    </div>
  );
};

export const UpcomingAppointments = () => {
  const { groupedAppointments, loading, error, refetch, getStatusColor, getStatusText, formatDate } = useUpcomingAppointment();

  if (loading) {
    return (
      <Card className="bg-white dark:bg-slate-800 rounded-xl border border-emerald-100 dark:border-slate-700 p-6 space-y-6 shadow-sm mb-6">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-slate-100 font-semibold">
            <Calendar className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
            <span>Lịch Hẹn Sắp Tới</span>
          </CardTitle>
        </CardHeader>

        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-center space-y-2">
              <Loader2 className="w-6 h-6 animate-spin mx-auto text-emerald-600 dark:text-emerald-400" />
              <p className="text-slate-500 dark:text-slate-400">Đang tải...</p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-emerald-100 dark:border-slate-700">
        <CardHeader>
          <CardTitle className="text-slate-900 dark:text-slate-100 font-semibold">
            <Calendar className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
            <span>Lịch Hẹn Sắp Tới</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-sm text-red-600">Có lỗi xảy ra khi tải lịch hẹn. Vui lòng thử lại.</p>
            <button onClick={refetch} className="mt-4 px-4 py-2 bg-emerald-500 text-white rounded hover:bg-emerald-600">
              Thử lại
            </button>
          </div>
        </CardContent>
      </Card>
    )
  }

  // Nếu có lịch hẹn
  const hasAppointments = groupedAppointments && Object.keys(groupedAppointments).length > 0;

  return (
    <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-emerald-100 dark:border-slate-700">
      <CardHeader>
        <CardTitle className="flex items-center justify-between text-slate-900 dark:text-slate-100 font-semibold">
          <div className="flex items-center space-x-2">
            <Calendar className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
            <span>Lịch Hẹn Sắp Tới</span>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={refetch}
            className="ml-auto text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 dark:text-emerald-400 dark:hover:bg-slate-700"
          >
            <RefreshCcw className="w-4 h-4" />
            <span className="sr-only">Làm mới</span>
          </Button>
        </CardTitle>
      </CardHeader>

      <CardContent>
        {!hasAppointments ? (
          <div className="text-center py-8">
            <CalendarDays className="w-12 h-12 mx-auto text-slate-300 dark:text-slate-600 mb-4" />
            <p className="text-slate-500 dark:text-slate-400">Bạn chưa có lịch hẹn nào sắp tới.</p>
          </div>
        ) : (
          <div className="space-y-4 max-h-[590px] overflow-y-auto pr-4 custom-scrollbar">
            {Object.entries(groupedAppointments).map(([date, appointments]) => (
              <div key={date}>
                <h3 className="font-semibold text-md text-slate-600 dark:text-slate-400 mb-2">{formatDate(date)}</h3>
                <div className="grid grid-cols-1 gap-4">
                  {appointments.map((appointment) => (
                    <AppointmentCard
                      key={appointment.appointmentId}
                      appointment={appointment}
                      getStatusColor={getStatusColor}
                      getStatusText={getStatusText}
                      formatDate={formatDate}
                    />
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}