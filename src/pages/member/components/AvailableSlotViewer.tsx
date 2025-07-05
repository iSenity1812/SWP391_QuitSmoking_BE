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
  ChevronLeft,
  ChevronRight,
  Calendar,
  Clock,
  Phone,
  MapPin,
  Star,
  UserCheck,
  CheckCircle,
  Loader2
} from "lucide-react"
import { toast } from "react-toastify"

// Interface cho Premium Member booking
interface CoachInfo {
  coachId: string
  fullName: string
  email: string
  avatar?: string
  rating: number
  totalSessions: number
  specialization: string[]
}

interface AvailableSlot {
  scheduleId: number
  coach: CoachInfo
  timeSlot: {
    timeSlotId: number
    label: string
    startTime: string
    endTime: string
  }
  scheduleDate: string
  isAvailable: boolean
  maxBookings: number
  currentBookings: number
}

interface BookingRequest {
  scheduleId: number
  notes: string
  method: 'phone' | 'in-person'
  preferredLocation?: string
}

// Mock data cho available slots
const mockAvailableSlots: AvailableSlot[] = [
  {
    scheduleId: 1,
    coach: {
      coachId: "coach-1",
      fullName: "Dr. Nguyễn Văn An",
      email: "an.nguyen@coach.com",
      avatar: "/placeholder-avatar.jpg",
      rating: 4.8,
      totalSessions: 150,
      specialization: ["Cai thuốc lá", "Tư vấn tâm lý", "Coaching"]
    },
    timeSlot: {
      timeSlotId: 1,
      label: "08:00",
      startTime: "08:00:00",
      endTime: "09:00:00"
    },
    scheduleDate: "2025-06-16",
    isAvailable: true,
    maxBookings: 1,
    currentBookings: 0
  },
  {
    scheduleId: 2,
    coach: {
      coachId: "coach-2",
      fullName: "Th.S Trần Thị Bình",
      email: "binh.tran@coach.com",
      avatar: "/placeholder-avatar.jpg",
      rating: 4.9,
      totalSessions: 200,
      specialization: ["Dinh dưỡng", "Lối sống lành mạnh", "Cai nghiện"]
    },
    timeSlot: {
      timeSlotId: 3,
      label: "10:00",
      startTime: "10:00:00",
      endTime: "11:00:00"
    },
    scheduleDate: "2025-06-17",
    isAvailable: true,
    maxBookings: 1,
    currentBookings: 0
  },
  {
    scheduleId: 3,
    coach: {
      coachId: "coach-1",
      fullName: "Dr. Nguyễn Văn An",
      email: "an.nguyen@coach.com",
      avatar: "/placeholder-avatar.jpg",
      rating: 4.8,
      totalSessions: 150,
      specialization: ["Cai thuốc lá", "Tư vấn tâm lý", "Coaching"]
    },
    timeSlot: {
      timeSlotId: 5,
      label: "13:00",
      startTime: "13:00:00",
      endTime: "14:00:00"
    },
    scheduleDate: "2025-06-18",
    isAvailable: true,
    maxBookings: 1,
    currentBookings: 0
  }
]

// Component hiển thị thông tin coach
function CoachCard({ coach }: { coach: CoachInfo }) {
  return (
    <div className="flex items-center space-x-3 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
      <Avatar className="w-12 h-12">
        <AvatarImage src={coach.avatar} alt={coach.fullName} />
        <AvatarFallback className="bg-gradient-to-br from-blue-500 to-purple-600 text-white">
          {coach.fullName.split(' ').map(n => n[0]).join('')}
        </AvatarFallback>
      </Avatar>

      <div className="flex-1">
        <h4 className="font-semibold text-slate-900 dark:text-white">
          {coach.fullName}
        </h4>
        <div className="flex items-center space-x-2 text-sm text-slate-600 dark:text-slate-400">
          <div className="flex items-center">
            <Star className="w-4 h-4 text-yellow-500 mr-1" />
            <span>{coach.rating}</span>
          </div>
          <span>•</span>
          <div className="flex items-center">
            <UserCheck className="w-4 h-4 mr-1" />
            <span>{coach.totalSessions} sessions</span>
          </div>
        </div>
        <div className="flex flex-wrap gap-1 mt-1">
          {coach.specialization.slice(0, 2).map((spec, index) => (
            <Badge key={index} variant="outline" className="text-xs">
              {spec}
            </Badge>
          ))}
          {coach.specialization.length > 2 && (
            <Badge variant="outline" className="text-xs">
              +{coach.specialization.length - 2}
            </Badge>
          )}
        </div>
      </div>
    </div>
  )
}

// Modal booking appointment
interface BookingModalProps {
  slot: AvailableSlot
  onClose: () => void
  onBook: (booking: BookingRequest) => void
}

function BookingModal({ slot, onClose, onBook }: BookingModalProps) {
  const [notes, setNotes] = useState("")
  const [method, setMethod] = useState<'phone' | 'in-person'>('phone')
  const [location, setLocation] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)

  const handleBooking = async () => {
    setIsLoading(true)

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500))

      const bookingData: BookingRequest = {
        scheduleId: slot.scheduleId,
        notes,
        method,
        preferredLocation: method === 'in-person' ? location : undefined
      }

      onBook(bookingData)
      setIsLoading(false)
      setShowSuccess(true)

      // Show success toast
      toast.success("Đã gửi yêu cầu book lịch thành công! Coach sẽ xác nhận trong thời gian sớm nhất.", {
        position: "top-right",
        autoClose: 4000,
      })

      // Auto close after success
      setTimeout(() => {
        setShowSuccess(false)
        onClose()
      }, 2000)
    } catch (error) {
      toast.error("Có lỗi xảy ra khi gửi yêu cầu. Vui lòng thử lại!" + error);
      setIsLoading(false)
      toast.error("Có lỗi xảy ra. Vui lòng thử lại!", {
        position: "top-right",
        autoClose: 3000,
      })
    }
  }

  if (showSuccess) {
    return (
      <div className="text-center space-y-4 py-8">
        <div className="mx-auto w-16 h-16 bg-green-100 dark:bg-green-900/20 rounded-full flex items-center justify-center animate-bounce">
          <CheckCircle className="w-8 h-8 text-green-600 dark:text-green-400" />
        </div>
        <div>
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
            Yêu cầu đã được gửi! 🎉
          </h3>
          <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
            Coach sẽ xem xét và phản hồi sớm nhất
          </p>
        </div>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="text-center space-y-4 py-8">
        <Loader2 className="w-12 h-12 text-blue-500 animate-spin mx-auto" />
        <div>
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
            Đang gửi yêu cầu...
          </h3>
          <p className="text-sm text-slate-600 dark:text-slate-400">
            Vui lòng đợi trong giây lát
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Coach Info */}
      <CoachCard coach={slot.coach} />

      {/* Slot Info */}
      <div className="space-y-3">
        <h4 className="font-medium text-slate-900 dark:text-white">
          Thông tin buổi tư vấn
        </h4>
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div className="flex items-center space-x-2">
            <Calendar className="w-4 h-4 text-blue-500" />
            <span>{new Date(slot.scheduleDate).toLocaleDateString('vi-VN')}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Clock className="w-4 h-4 text-green-500" />
            <span>{slot.timeSlot.startTime.slice(0, 5)} - {slot.timeSlot.endTime.slice(0, 5)}</span>
          </div>
        </div>
      </div>

      {/* Booking Method */}
      <div className="space-y-3">
        <h4 className="font-medium text-slate-900 dark:text-white">
          Phương thức tư vấn
        </h4>
        <div className="grid grid-cols-2 gap-3">
          <Button
            variant={method === 'phone' ? 'default' : 'outline'}
            onClick={() => setMethod('phone')}
            className="flex items-center justify-center space-x-2"
          >
            <Phone className="w-4 h-4" />
            <span>Điện thoại</span>
          </Button>
          <Button
            variant={method === 'in-person' ? 'default' : 'outline'}
            onClick={() => setMethod('in-person')}
            className="flex items-center justify-center space-x-2"
          >
            <MapPin className="w-4 h-4" />
            <span>Trực tiếp</span>
          </Button>
        </div>
      </div>

      {/* Location (if in-person) */}
      {method === 'in-person' && (
        <div className="space-y-2">
          <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
            Địa điểm mong muốn
          </label>
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="Ví dụ: Phòng tư vấn tầng 2, văn phòng..."
            className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-slate-500 dark:placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      )}

      {/* Notes */}
      <div className="space-y-2">
        <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
          Ghi chú (tùy chọn)
        </label>
        <textarea
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          placeholder="Mô tả tình trạng hiện tại, mục tiêu cần hỗ trợ..."
          rows={3}
          className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-slate-500 dark:placeholder-slate-400 focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        />
      </div>

      {/* Action Buttons */}
      <div className="flex space-x-3 pt-4 border-t border-slate-200 dark:border-slate-700">
        <Button variant="outline" onClick={onClose} className="flex-1">
          Hủy
        </Button>
        <Button onClick={handleBooking} className="flex-1 bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700">
          Đặt lịch
        </Button>
      </div>
    </div>
  )
}

// Main component - Available Slots Viewer for Premium Members
export function AvailableSlotViewer() {
  const [currentWeekStart, setCurrentWeekStart] = useState<Date>(() => {
    const today = new Date()
    const startOfWeek = new Date(today)
    const day = startOfWeek.getDay()
    const diff = startOfWeek.getDate() - day
    startOfWeek.setDate(diff)
    return startOfWeek
  })

  const [selectedSlot, setSelectedSlot] = useState<AvailableSlot | null>(null)
  const [availableSlots] = useState<AvailableSlot[]>(mockAvailableSlots)

  const formatWeekRange = (startDate: Date) => {
    const endDate = new Date(startDate)
    endDate.setDate(startDate.getDate() + 6)

    const startStr = startDate.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' })
    const endStr = endDate.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })

    return `${startStr} - ${endStr}`
  }

  const goToPreviousWeek = () => {
    const prevWeek = new Date(currentWeekStart)
    prevWeek.setDate(currentWeekStart.getDate() - 7)
    setCurrentWeekStart(prevWeek)
  }

  const goToNextWeek = () => {
    const nextWeek = new Date(currentWeekStart)
    nextWeek.setDate(currentWeekStart.getDate() + 7)
    setCurrentWeekStart(nextWeek)
  }

  const handleBookSlot = (bookingData: BookingRequest) => {
    console.log('Booking request:', bookingData)
    // TODO: Integrate with API
  }

  const groupSlotsByDate = (slots: AvailableSlot[]) => {
    return slots.reduce((acc, slot) => {
      const date = slot.scheduleDate
      if (!acc[date]) {
        acc[date] = []
      }
      acc[date].push(slot)
      return acc
    }, {} as Record<string, AvailableSlot[]>)
  }

  const groupedSlots = groupSlotsByDate(availableSlots)

  return (
    <div className="space-y-6">
      {/* Header */}
      <Card className="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-slate-800 dark:to-slate-700 border-0">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="w-6 h-6 text-blue-600" />
            <span>Đặt lịch tư vấn với Coach</span>
          </CardTitle>
          <p className="text-slate-600 dark:text-slate-400">
            Chọn khung giờ phù hợp để được tư vấn bởi các Coach chuyên nghiệp
          </p>
        </CardHeader>
      </Card>

      {/* Week Navigation */}
      <Card>
        <CardContent className="p-4">
          <div className="flex items-center justify-between">
            <Button variant="outline" size="sm" onClick={goToPreviousWeek}>
              <ChevronLeft className="w-4 h-4 mr-1" />
              Tuần trước
            </Button>

            <h3 className="font-semibold text-lg">
              {formatWeekRange(currentWeekStart)}
            </h3>

            <Button variant="outline" size="sm" onClick={goToNextWeek}>
              Tuần sau
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Available Slots */}
      <div className="space-y-6">
        {Object.keys(groupedSlots).length === 0 ? (
          <Card>
            <CardContent className="p-8 text-center">
              <Calendar className="w-12 h-12 text-slate-400 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-slate-900 dark:text-white mb-2">
                Không có lịch trống
              </h3>
              <p className="text-slate-600 dark:text-slate-400">
                Chưa có Coach nào mở lịch trong tuần này. Vui lòng thử tuần khác.
              </p>
            </CardContent>
          </Card>
        ) : (
          Object.entries(groupedSlots).map(([date, slots]) => (
            <Card key={date}>
              <CardHeader>
                <CardTitle className="text-lg">
                  {new Date(date).toLocaleDateString('vi-VN', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {slots.map((slot) => (
                    <div
                      key={slot.scheduleId}
                      className="border border-slate-200 dark:border-slate-700 rounded-lg p-4 hover:border-blue-300 dark:hover:border-blue-600 transition-colors cursor-pointer"
                      onClick={() => setSelectedSlot(slot)}
                    >
                      <div className="space-y-3">
                        {/* Coach info */}
                        <div className="flex items-center space-x-2">
                          <Avatar className="w-8 h-8">
                            <AvatarImage src={slot.coach.avatar} alt={slot.coach.fullName} />
                            <AvatarFallback className="text-xs bg-gradient-to-br from-blue-500 to-purple-600 text-white">
                              {slot.coach.fullName.split(' ').map(n => n[0]).join('')}
                            </AvatarFallback>
                          </Avatar>
                          <div>
                            <p className="font-medium text-sm text-slate-900 dark:text-white">
                              {slot.coach.fullName}
                            </p>
                            <div className="flex items-center text-xs text-slate-600 dark:text-slate-400">
                              <Star className="w-3 h-3 text-yellow-500 mr-1" />
                              <span>{slot.coach.rating}</span>
                            </div>
                          </div>
                        </div>

                        {/* Time slot */}
                        <div className="flex items-center justify-between">
                          <div className="flex items-center space-x-2 text-sm">
                            <Clock className="w-4 h-4 text-green-500" />
                            <span className="font-medium">
                              {slot.timeSlot.startTime.slice(0, 5)} - {slot.timeSlot.endTime.slice(0, 5)}
                            </span>
                          </div>
                          <Badge variant="outline" className="text-xs">
                            Có sẵn
                          </Badge>
                        </div>

                        {/* Specialization */}
                        <div className="flex flex-wrap gap-1">
                          {slot.coach.specialization.slice(0, 2).map((spec, index) => (
                            <Badge key={index} variant="secondary" className="text-xs">
                              {spec}
                            </Badge>
                          ))}
                        </div>

                        {/* Book button */}
                        <Button
                          size="sm"
                          className="w-full bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700"
                          onClick={(e) => {
                            e.stopPropagation()
                            setSelectedSlot(slot)
                          }}
                        >
                          Đặt lịch ngay
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      {/* Booking Modal */}
      {selectedSlot && (
        <Dialog open={!!selectedSlot} onOpenChange={() => setSelectedSlot(null)}>
          <DialogContent className="max-w-lg">
            <DialogHeader>
              <DialogTitle>Đặt lịch tư vấn</DialogTitle>
            </DialogHeader>
            <BookingModal
              slot={selectedSlot}
              onClose={() => setSelectedSlot(null)}
              onBook={handleBookSlot}
            />
          </DialogContent>
        </Dialog>
      )}
    </div>
  )
}
