import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle
} from "@/components/ui/dialog"
import {
  Calendar,
  Clock,
  Phone,
  MapPin,
  X,
  CheckCircle,
  AlertCircle,
  Loader2,
  MessageCircle,
  RefreshCw
} from "lucide-react"
import { toast } from "react-toastify"

// Interface cho member bookings
interface MemberBooking {
  appointmentId: number
  coach: {
    coachId: string
    fullName: string
    avatar?: string
    email: string
  }
  timeSlot: {
    label: string
    startTime: string
    endTime: string
  }
  scheduleDate: string
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'REJECTED'
  method: 'phone' | 'in-person'
  location?: string
  notes?: string
  bookingTime: string
  responseMessage?: string // Message from coach when confirm/reject
}

// Mock data cho member bookings
const mockMemberBookings: MemberBooking[] = [
  {
    appointmentId: 1,
    coach: {
      coachId: "coach-1",
      fullName: "Dr. Nguyễn Văn An",
      avatar: "/placeholder-avatar.jpg",
      email: "an.nguyen@coach.com"
    },
    timeSlot: {
      label: "08:00",
      startTime: "08:00:00",
      endTime: "09:00:00"
    },
    scheduleDate: "2025-06-20",
    status: "CONFIRMED",
    method: "phone",
    notes: "Cần tư vấn về kế hoạch cai thuốc lá phù hợp",
    bookingTime: "2025-06-18T10:30:00Z",
    responseMessage: "Tôi sẽ chuẩn bị kỹ lưỡng cho buổi tư vấn. Hãy chuẩn bị danh sách câu hỏi nhé!"
  },
  {
    appointmentId: 2,
    coach: {
      coachId: "coach-2",
      fullName: "Th.S Trần Thị Bình",
      avatar: "/placeholder-avatar.jpg",
      email: "binh.tran@coach.com"
    },
    timeSlot: {
      label: "14:00",
      startTime: "14:00:00",
      endTime: "15:00:00"
    },
    scheduleDate: "2025-06-22",
    status: "PENDING",
    method: "in-person",
    location: "Phòng tư vấn tầng 2",
    notes: "Muốn tìm hiểu về chế độ dinh dưỡng sau khi cai thuốc",
    bookingTime: "2025-06-19T14:15:00Z"
  },
  {
    appointmentId: 3,
    coach: {
      coachId: "coach-1",
      fullName: "Dr. Nguyễn Văn An",
      avatar: "/placeholder-avatar.jpg",
      email: "an.nguyen@coach.com"
    },
    timeSlot: {
      label: "16:00",
      startTime: "16:00:00",
      endTime: "17:00:00"
    },
    scheduleDate: "2025-06-15",
    status: "COMPLETED",
    method: "phone",
    notes: "Tư vấn lần đầu",
    bookingTime: "2025-06-10T09:00:00Z",
    responseMessage: "Buổi tư vấn rất thành công. Hãy theo dõi kế hoạch đã thống nhất nhé!"
  },
  {
    appointmentId: 4,
    coach: {
      coachId: "coach-3",
      fullName: "BS. Lê Văn Cường",
      avatar: "/placeholder-avatar.jpg",
      email: "cuong.le@coach.com"
    },
    timeSlot: {
      label: "10:00",
      startTime: "10:00:00",
      endTime: "11:00:00"
    },
    scheduleDate: "2025-06-18",
    status: "REJECTED",
    method: "in-person",
    location: "Văn phòng",
    notes: "Cần tư vấn khẩn cấp",
    bookingTime: "2025-06-17T16:45:00Z",
    responseMessage: "Xin lỗi, tôi có việc đột xuất. Bạn có thể book slot khác được không?"
  }
]

// Status configuration
const statusConfig = {
  PENDING: {
    label: "Chờ xác nhận",
    color: "bg-yellow-100 border-yellow-300 text-yellow-800 dark:bg-yellow-900/20 dark:border-yellow-700 dark:text-yellow-300",
    icon: <Clock className="w-4 h-4" />
  },
  CONFIRMED: {
    label: "Đã xác nhận",
    color: "bg-green-100 border-green-300 text-green-800 dark:bg-green-900/20 dark:border-green-700 dark:text-green-300",
    icon: <CheckCircle className="w-4 h-4" />
  },
  CANCELLED: {
    label: "Đã hủy",
    color: "bg-gray-100 border-gray-300 text-gray-800 dark:bg-gray-900/20 dark:border-gray-700 dark:text-gray-300",
    icon: <X className="w-4 h-4" />
  },
  COMPLETED: {
    label: "Hoàn thành",
    color: "bg-blue-100 border-blue-300 text-blue-800 dark:bg-blue-900/20 dark:border-blue-700 dark:text-blue-300",
    icon: <CheckCircle className="w-4 h-4" />
  },
  REJECTED: {
    label: "Bị từ chối",
    color: "bg-red-100 border-red-300 text-red-800 dark:bg-red-900/20 dark:border-red-700 dark:text-red-300",
    icon: <AlertCircle className="w-4 h-4" />
  }
}

// Cancel booking modal
interface CancelBookingModalProps {
  booking: MemberBooking
  onClose: () => void
  onCancel: (appointmentId: number) => void
}

function CancelBookingModal({ booking, onClose, onCancel }: CancelBookingModalProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [reason, setReason] = useState("")

  const handleCancel = async () => {
    setIsLoading(true)

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000))

      onCancel(booking.appointmentId)
      toast.success("Đã hủy lịch hẹn thành công", {
        position: "top-right",
        autoClose: 3000,
      })
      onClose()

    } catch (error) {
      console.error('Cancel error:', error)
      toast.error("Có lỗi xảy ra. Vui lòng thử lại!", {
        position: "top-right",
        autoClose: 3000,
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="text-center">
        <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
        <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
          Xác nhận hủy lịch hẹn
        </h3>
        <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
          Bạn có chắc chắn muốn hủy lịch hẹn với {booking.coach.fullName}?
        </p>
      </div>

      <div className="p-4 bg-slate-50 dark:bg-slate-800 rounded-lg space-y-2">
        <div className="flex items-center space-x-2 text-sm">
          <Calendar className="w-4 h-4 text-blue-500" />
          <span>{new Date(booking.scheduleDate).toLocaleDateString('vi-VN')}</span>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <Clock className="w-4 h-4 text-green-500" />
          <span>{booking.timeSlot.startTime.slice(0, 5)} - {booking.timeSlot.endTime.slice(0, 5)}</span>
        </div>
      </div>

      <div className="space-y-2">
        <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
          Lý do hủy (tùy chọn)
        </label>
        <textarea
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          placeholder="Ví dụ: Có việc đột xuất, thay đổi lịch trình..."
          rows={3}
          className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-slate-500 dark:placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        />
      </div>

      <div className="flex space-x-3">
        <Button variant="outline" onClick={onClose} className="flex-1" disabled={isLoading}>
          Giữ lại lịch hẹn
        </Button>
        <Button
          variant="destructive"
          onClick={handleCancel}
          className="flex-1"
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
              Đang hủy...
            </>
          ) : (
            'Xác nhận hủy'
          )}
        </Button>
      </div>
    </div>
  )
}

// Main component - Member Bookings Management
export function MyBookingsPage() {
  const [bookings, setBookings] = useState<MemberBooking[]>(mockMemberBookings)
  const [selectedBooking, setSelectedBooking] = useState<MemberBooking | null>(null)
  const [cancelBooking, setCancelBooking] = useState<MemberBooking | null>(null)
  const [filter, setFilter] = useState<'all' | 'pending' | 'confirmed' | 'completed'>('all')

  const handleCancelBooking = (appointmentId: number) => {
    setBookings(prev =>
      prev.map(booking =>
        booking.appointmentId === appointmentId
          ? { ...booking, status: 'CANCELLED' as const }
          : booking
      )
    )
    setCancelBooking(null)
  }

  const filteredBookings = bookings.filter(booking => {
    if (filter === 'all') return true
    return booking.status.toLowerCase() === filter.toUpperCase()
  })

  const getStatusBadge = (status: MemberBooking['status']) => {
    const config = statusConfig[status]
    return (
      <Badge className={`${config.color} border px-2 py-1`}>
        <span className="flex items-center space-x-1">
          {config.icon}
          <span>{config.label}</span>
        </span>
      </Badge>
    )
  }

  const canCancelBooking = (booking: MemberBooking) => {
    return booking.status === 'PENDING' || booking.status === 'CONFIRMED'
  }

  const sortedBookings = filteredBookings.sort((a, b) =>
    new Date(b.scheduleDate).getTime() - new Date(a.scheduleDate).getTime()
  )

  return (
    <div className="space-y-6">
      {/* Header */}
      <Card className="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-slate-800 dark:to-slate-700 border-0">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="w-6 h-6 text-blue-600" />
            <span>Lịch hẹn của tôi</span>
          </CardTitle>
          <p className="text-slate-600 dark:text-slate-400">
            Quản lý các lịch hẹn tư vấn với Coach
          </p>
        </CardHeader>
      </Card>

      {/* Filter Tabs */}
      <Card>
        <CardContent className="p-4">
          <div className="flex space-x-2 overflow-x-auto">
            {[
              { key: 'all', label: 'Tất cả' },
              { key: 'pending', label: 'Chờ xác nhận' },
              { key: 'confirmed', label: 'Đã xác nhận' },
              { key: 'completed', label: 'Hoàn thành' }
            ].map(({ key, label }) => (
              <Button
                key={key}
                variant={filter === key ? 'default' : 'outline'}
                size="sm"
                onClick={() => setFilter(key as typeof filter)}
                className="whitespace-nowrap"
              >
                {label}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Bookings List */}
      <div className="space-y-4">
        {sortedBookings.length === 0 ? (
          <Card>
            <CardContent className="p-8 text-center">
              <Calendar className="w-12 h-12 text-slate-400 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-slate-900 dark:text-white mb-2">
                Chưa có lịch hẹn nào
              </h3>
              <p className="text-slate-600 dark:text-slate-400 mb-4">
                {filter === 'all'
                  ? 'Bạn chưa đặt lịch hẹn nào với Coach'
                  : `Không có lịch hẹn nào ở trạng thái "${filter === 'pending' ? 'Chờ xác nhận' : filter === 'confirmed' ? 'Đã xác nhận' : 'Hoàn thành'}"`
                }
              </p>
              <Button>
                Đặt lịch ngay
              </Button>
            </CardContent>
          </Card>
        ) : (
          sortedBookings.map((booking) => (
            <Card key={booking.appointmentId} className="hover:shadow-md transition-shadow">
              <CardContent className="p-6">
                <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
                  {/* Left side - Booking info */}
                  <div className="flex-1 space-y-3">
                    {/* Coach info */}
                    <div className="flex items-center space-x-3">
                      <Avatar className="w-10 h-10">
                        <AvatarImage src={booking.coach.avatar} alt={booking.coach.fullName} />
                        <AvatarFallback className="text-sm bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                          {booking.coach.fullName.split(' ').map(n => n[0]).join('')}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <h4 className="font-semibold text-slate-900 dark:text-white">
                          {booking.coach.fullName}
                        </h4>
                        <p className="text-sm text-slate-600 dark:text-slate-400">
                          {booking.coach.email}
                        </p>
                      </div>
                    </div>

                    {/* Time & Method info */}
                    <div className="flex flex-wrap items-center gap-4 text-sm">
                      <div className="flex items-center space-x-2">
                        <Calendar className="w-4 h-4 text-blue-500" />
                        <span>{new Date(booking.scheduleDate).toLocaleDateString('vi-VN')}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        <Clock className="w-4 h-4 text-green-500" />
                        <span>{booking.timeSlot.startTime.slice(0, 5)} - {booking.timeSlot.endTime.slice(0, 5)}</span>
                      </div>
                      <div className="flex items-center space-x-2">
                        {booking.method === 'phone' ? (
                          <Phone className="w-4 h-4 text-purple-500" />
                        ) : (
                          <MapPin className="w-4 h-4 text-orange-500" />
                        )}
                        <span>{booking.method === 'phone' ? 'Điện thoại' : 'Trực tiếp'}</span>
                        {booking.location && (
                          <span className="text-slate-500">- {booking.location}</span>
                        )}
                      </div>
                    </div>

                    {/* Status & Response message */}
                    <div className="space-y-2">
                      {getStatusBadge(booking.status)}
                      {booking.responseMessage && (
                        <div className="flex items-start space-x-2 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                          <MessageCircle className="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" />
                          <div>
                            <p className="text-sm font-medium text-slate-900 dark:text-white">
                              Tin nhắn từ Coach:
                            </p>
                            <p className="text-sm text-slate-600 dark:text-slate-400">
                              {booking.responseMessage}
                            </p>
                          </div>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Right side - Actions */}
                  <div className="flex flex-col space-y-2 md:min-w-[150px]">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setSelectedBooking(booking)}
                    >
                      Xem chi tiết
                    </Button>

                    {canCancelBooking(booking) && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCancelBooking(booking)}
                        className="text-red-600 border-red-200 hover:bg-red-50 dark:text-red-400 dark:border-red-800 dark:hover:bg-red-900/20"
                      >
                        Hủy lịch hẹn
                      </Button>
                    )}

                    {booking.status === 'REJECTED' && (
                      <Button
                        variant="outline"
                        size="sm"
                        className="text-blue-600 border-blue-200 hover:bg-blue-50 dark:text-blue-400 dark:border-blue-800 dark:hover:bg-blue-900/20"
                      >
                        <RefreshCw className="w-4 h-4 mr-1" />
                        Đặt lại
                      </Button>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      {/* Booking Details Modal */}
      {selectedBooking && (
        <Dialog open={!!selectedBooking} onOpenChange={() => setSelectedBooking(null)}>
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle>Chi tiết lịch hẹn</DialogTitle>
            </DialogHeader>
            <div className="space-y-6">
              {/* Coach info */}
              <div className="flex items-center space-x-3 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                <Avatar className="w-12 h-12">
                  <AvatarImage src={selectedBooking.coach.avatar} alt={selectedBooking.coach.fullName} />
                  <AvatarFallback className="bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                    {selectedBooking.coach.fullName.split(' ').map(n => n[0]).join('')}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <h4 className="font-semibold text-slate-900 dark:text-white">
                    {selectedBooking.coach.fullName}
                  </h4>
                  <p className="text-sm text-slate-600 dark:text-slate-400">
                    {selectedBooking.coach.email}
                  </p>
                </div>
              </div>

              {/* Booking details */}
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Ngày hẹn</p>
                  <div className="flex items-center space-x-2">
                    <Calendar className="w-4 h-4 text-blue-500" />
                    <span>{new Date(selectedBooking.scheduleDate).toLocaleDateString('vi-VN')}</span>
                  </div>
                </div>
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Thời gian</p>
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4 text-green-500" />
                    <span>{selectedBooking.timeSlot.startTime.slice(0, 5)} - {selectedBooking.timeSlot.endTime.slice(0, 5)}</span>
                  </div>
                </div>
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Phương thức</p>
                  <div className="flex items-center space-x-2">
                    {selectedBooking.method === 'phone' ? (
                      <Phone className="w-4 h-4 text-purple-500" />
                    ) : (
                      <MapPin className="w-4 h-4 text-orange-500" />
                    )}
                    <span>{selectedBooking.method === 'phone' ? 'Điện thoại' : 'Trực tiếp'}</span>
                  </div>
                </div>
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Trạng thái</p>
                  {getStatusBadge(selectedBooking.status)}
                </div>
              </div>

              {selectedBooking.location && (
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Địa điểm</p>
                  <p className="text-sm text-slate-600 dark:text-slate-400">
                    {selectedBooking.location}
                  </p>
                </div>
              )}

              {selectedBooking.notes && (
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Ghi chú của bạn</p>
                  <p className="text-sm text-slate-600 dark:text-slate-400 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                    {selectedBooking.notes}
                  </p>
                </div>
              )}

              {selectedBooking.responseMessage && (
                <div className="space-y-1">
                  <p className="font-medium text-slate-700 dark:text-slate-300">Tin nhắn từ Coach</p>
                  <div className="flex items-start space-x-2 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                    <MessageCircle className="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" />
                    <p className="text-sm text-slate-600 dark:text-slate-400">
                      {selectedBooking.responseMessage}
                    </p>
                  </div>
                </div>
              )}

              <div className="space-y-1">
                <p className="font-medium text-slate-700 dark:text-slate-300">Thời gian đặt</p>
                <p className="text-sm text-slate-600 dark:text-slate-400">
                  {new Date(selectedBooking.bookingTime).toLocaleString('vi-VN')}
                </p>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      )}

      {/* Cancel Booking Modal */}
      {cancelBooking && (
        <Dialog open={!!cancelBooking} onOpenChange={() => setCancelBooking(null)}>
          <DialogContent className="max-w-md">
            <CancelBookingModal
              booking={cancelBooking}
              onClose={() => setCancelBooking(null)}
              onCancel={handleCancelBooking}
            />
          </DialogContent>
        </Dialog>
      )}
    </div>
  )
}
