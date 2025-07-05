"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Label } from "@/components/ui/label"
// import { Checkbox } from "@/components/ui/checkbox"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
// import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
  ChevronLeft,
  ChevronRight,
  Filter,
  Star,
  User,
  Calendar,
  Users,
  Info,
  Plus,
  Settings,
  Loader2,
  AlertCircle,
} from "lucide-react"

// API Response Types
interface TimeSlotResponse {
  status: number
  message: string
  data: TimeSlot[]
  error: null
  errorCode: null
  timestamp: string
}

interface TimeSlot {
  timeSlotId: number
  label: string
  startTime: string
  endTime: string
  deleted: boolean
}

interface Coach {
  coachId: string
  username: string
  email: string
  fullName: string
  specialties: string
  rating: number
}

interface CoachSchedule {
  scheduleId: number
  coach: Coach
  timeSlot: TimeSlot
  scheduleDate: string
  booked: boolean
}

interface CoachScheduleResponse {
  status: number
  message: string
  data: CoachSchedule[]
  error: any
  errorCode: string
  timestamp: string
}

interface FilterState {
  specialties: string[]
  minRating: number
}

// Specialty options based on the provided enums
const specialtyOptions = [
  { key: "BEHAVIORAL_THERAPY", label: "Tư vấn hành vi" },
  { key: "NRT_GUIDANCE", label: "Hướng dẫn liệu pháp thay thế Nicotine" },
  { key: "STRESS_MANAGEMENT", label: "Quản lý căng thẳng" },
  { key: "RELAPSE_PREVENTION", label: "Phòng ngừa tái nghiện" },
  { key: "MOTIVATION_MINDSET", label: "Động lực và Tư duy" },
  { key: "VAPING_CESSATION", label: "Hỗ trợ cai thuốc lá điện tử" },
  { key: "YOUTH_CESSATION", label: "Hỗ trợ thanh thiếu niên cai thuốc" },
]

// Fallback mock data in case API fails
const fallbackTimeSlots: TimeSlot[] = [
  { timeSlotId: 1, label: "08:00", startTime: "08:00:00", endTime: "09:00:00", deleted: false },
  { timeSlotId: 2, label: "09:00", startTime: "09:00:00", endTime: "10:00:00", deleted: false },
  { timeSlotId: 3, label: "10:00", startTime: "10:00:00", endTime: "11:00:00", deleted: false },
  { timeSlotId: 4, label: "11:00", startTime: "11:00:00", endTime: "12:00:00", deleted: false },
  { timeSlotId: 5, label: "14:00", startTime: "14:00:00", endTime: "15:00:00", deleted: false },
  { timeSlotId: 6, label: "15:00", startTime: "15:00:00", endTime: "16:00:00", deleted: false },
  { timeSlotId: 7, label: "16:00", startTime: "16:00:00", endTime: "17:00:00", deleted: false },
  { timeSlotId: 8, label: "17:00", startTime: "17:00:00", endTime: "18:00:00", deleted: false },
]

interface WeekNavigationProps {
  currentWeekStart: Date
  onWeekChange: (date: Date) => void
  selectedSlots: Set<string>
  onSlotSelectionChange: () => void
}

function WeekNavigation({ currentWeekStart, onWeekChange, selectedSlots, onSlotSelectionChange }: WeekNavigationProps) {
  const formatWeekRange = (startDate: Date) => {
    const endDate = new Date(startDate)
    endDate.setDate(startDate.getDate() + 6)

    const startStr = `${startDate.getDate().toString().padStart(2, "0")}/${(startDate.getMonth() + 1).toString().padStart(2, "0")}/${startDate.getFullYear()}`
    const endStr = `${endDate.getDate().toString().padStart(2, "0")}/${(endDate.getMonth() + 1).toString().padStart(2, "0")}/${endDate.getFullYear()}`

    return `${startStr} - ${endStr}`
  }

  const goToPreviousWeek = () => {
    const prevWeek = new Date(currentWeekStart)
    prevWeek.setDate(currentWeekStart.getDate() - 7)
    onWeekChange(prevWeek)
  }

  const goToNextWeek = () => {
    const nextWeek = new Date(currentWeekStart)
    nextWeek.setDate(currentWeekStart.getDate() + 7)
    onWeekChange(nextWeek)
  }

  const goToCurrentWeek = () => {
    const today = new Date()
    const monday = new Date(today)
    monday.setDate(today.getDate() - today.getDay() + 1)
    onWeekChange(monday)
  }

  return (
    <div className="flex items-center justify-between mb-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="sm" onClick={goToPreviousWeek}>
          <ChevronLeft className="w-4 h-4" />
        </Button>

        <div className="text-lg font-semibold text-slate-900">{formatWeekRange(currentWeekStart)}</div>

        <Button variant="outline" size="sm" onClick={goToNextWeek}>
          <ChevronRight className="w-4 h-4" />
        </Button>

        <Button variant="outline" size="sm" onClick={goToCurrentWeek}>
          Tuần này
        </Button>
      </div>

      <div className="flex items-center space-x-2">
        {selectedSlots.size > 0 && (
          <Badge variant="secondary" className="bg-emerald-100 text-emerald-700">
            {selectedSlots.size} slot đã chọn
          </Badge>
        )}
        <Button
          variant="outline"
          size="sm"
          className="bg-gradient-to-r from-emerald-50 to-emerald-100 border-emerald-200 hover:from-emerald-100 hover:to-emerald-200"
          onClick={onSlotSelectionChange}
        >
          <Settings className="w-4 h-4 mr-2" />
          Quản lý lựa chọn
        </Button>
      </div>
    </div>
  )
}

interface FilterSidebarProps {
  filters: FilterState
  onFiltersChange: (filters: FilterState) => void
  isOpen: boolean
  onToggle: () => void
}

function FilterSidebar({ filters, onFiltersChange, isOpen, onToggle }: FilterSidebarProps) {
  const handleSpecialtyChange = (specialty: string, checked: boolean) => {
    const newSpecialties = checked
      ? [...filters.specialties, specialty]
      : filters.specialties.filter((s) => s !== specialty)

    onFiltersChange({ ...filters, specialties: newSpecialties })
  }

  const clearFilters = () => {
    onFiltersChange({
      specialties: [],
      minRating: 0,
    })
  }

  return (
    <>
      {/* Mobile Filter Toggle */}
      <div className="lg:hidden mb-4">
        <Button
          variant="outline"
          onClick={onToggle}
          className="w-full border-emerald-200 text-emerald-700 hover:bg-emerald-50"
        >
          <Filter className="w-4 h-4 mr-2" />
          Bộ lọc
        </Button>
      </div>

      {/* Filter Sidebar */}
      <div
        className={`
        ${isOpen ? "block" : "hidden"} lg:block
        bg-white rounded-xl border border-emerald-100 p-6 space-y-6 shadow-sm
      `}
      >
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900">Bộ lọc</h3>
          <Button
            variant="ghost"
            size="sm"
            onClick={clearFilters}
            className="text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50"
          >
            Xóa tất cả
          </Button>
        </div>

        {/* Specialties */}
        <div className="space-y-3">
          <Label className="text-sm font-medium text-slate-700">Chuyên môn</Label>
          <div className="space-y-2 max-h-48 overflow-y-auto">
            {specialtyOptions.map((specialty) => (
              <div key={specialty.key} className="flex items-start space-x-2">
                <input
                  type="checkbox"
                  id={specialty.key}
                  checked={filters.specialties.includes(specialty.key)}
                  onChange={(e) => handleSpecialtyChange(specialty.key, e.target.checked)}
                  className="border-emerald-300 data-[state=checked]:bg-emerald-500 data-[state=checked]:border-emerald-500 mt-0.5"
                />
                <Label htmlFor={specialty.key} className="text-sm text-slate-600 cursor-pointer leading-5">
                  {specialty.label}
                </Label>
              </div>
            ))}
          </div>
        </div>

        {/* Rating */}
        <div className="space-y-3">
          <Label className="text-sm font-medium text-slate-700">Đánh giá tối thiểu</Label>
          <div className="flex items-center space-x-2">
            <input
              type="range"
              min="0"
              max="5"
              step="0.1"
              value={filters.minRating}
              onChange={(e) => onFiltersChange({ ...filters, minRating: Number.parseFloat(e.target.value) })}
              className="flex-1 h-2 bg-emerald-200 rounded-lg appearance-none cursor-pointer"
            />
            <div className="flex items-center space-x-1 min-w-[60px]">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="text-sm font-medium">{filters.minRating.toFixed(1)}</span>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}

interface CoachSelectionModalProps {
  isOpen: boolean
  onClose: () => void
  coaches: Coach[]
  timeSlot: TimeSlot
  date: string
  onBook: (coachId: string) => void
}

function CoachSelectionModal({ isOpen, onClose, coaches, timeSlot, onBook }: Omit<CoachSelectionModalProps, 'date'>) {
  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr)
    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

  const getSpecialtyLabel = (specialtyKey: string) => {
    const specialty = specialtyOptions.find((s) => s.key === specialtyKey)
    return specialty ? specialty.label : specialtyKey
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            <Users className="w-5 h-5 text-emerald-600" />
            <span>Chọn Coach</span>
          </DialogTitle>
          <div className="text-sm text-slate-600">
            {formatDate(new Date().toISOString().split('T')[0])} • {timeSlot.startTime.slice(0, 5)} - {timeSlot.endTime.slice(0, 5)}
          </div>
        </DialogHeader>

        <div className="space-y-4">
          {coaches.map((coach) => (
            <div
              key={coach.coachId}
              className="border border-emerald-100 rounded-lg p-4 hover:border-emerald-200 transition-colors"
            >
              <div className="flex items-start space-x-4">
                <div className="w-16 h-16 rounded-full bg-emerald-100 flex items-center justify-center">
                  <User className="w-8 h-8 text-emerald-600" />
                </div>

                <div className="flex-1 space-y-2">
                  <div className="flex items-center justify-between">
                    <h4 className="font-semibold text-slate-900">{coach.fullName}</h4>
                  </div>

                  <div className="flex items-center space-x-4 text-sm text-slate-600">
                    <div className="flex items-center space-x-1">
                      <Star className="w-4 h-4 text-yellow-400 fill-current" />
                      <span>{coach.rating.toFixed(1)}</span>
                    </div>
                    <div className="text-slate-500">@{coach.username}</div>
                  </div>

                  <div className="flex flex-wrap gap-1">
                    {coach.specialties.split(",").map((spec, index) => (
                      <Badge
                        key={index}
                        variant="secondary"
                        className="text-xs bg-emerald-50 text-emerald-700 border-emerald-200"
                      >
                        {getSpecialtyLabel(spec.trim())}
                      </Badge>
                    ))}
                  </div>

                  <div className="flex items-center justify-between pt-2">
                    <div className="text-xs text-slate-500">{coach.email}</div>

                    <div className="flex space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        className="border-emerald-200 text-emerald-700 hover:bg-emerald-50"
                      >
                        <Info className="w-4 h-4 mr-1" />
                        Hồ sơ
                      </Button>
                      <Button
                        size="sm"
                        onClick={() => onBook(coach.coachId)}
                        className="bg-emerald-500 hover:bg-emerald-600 text-white"
                      >
                        Đặt lịch
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  )
}

interface TimeSlotCellProps {
  timeSlot: TimeSlot
  date: string
  coaches: Coach[]
  onSlotClick: () => void
  isSelected: boolean
  onToggleSelection: () => void
  isPast: boolean
}

function TimeSlotCell({
  timeSlot: _timeSlot,
  date: _date,
  coaches,
  onSlotClick,
  isSelected,
  onToggleSelection,
  isPast,
}: TimeSlotCellProps) {
  // const [showPopover, setShowPopover] = useState(false)

  // Determine cell state based on the reference image
  const getCellState = () => {
    if (isPast) return "past"
    if (coaches.length === 0) return "not-registered"
    return "available"
  }

  const cellState = getCellState()

  const getCellStyles = () => {
    const baseStyles = "min-h-[100px] p-3 rounded-lg transition-all duration-200 cursor-pointer border-2"

    switch (cellState) {
      case "past":
        return `${baseStyles} bg-slate-100 border-slate-200 text-slate-500`
      case "not-registered":
        return `${baseStyles} bg-white border-slate-200 text-slate-600 hover:border-slate-300`
      case "available":
        if (isSelected) {
          return `${baseStyles} bg-emerald-100 border-emerald-400 text-emerald-700`
        }
        return `${baseStyles} bg-emerald-50 border-emerald-200 text-emerald-600 hover:border-emerald-300 hover:bg-emerald-100`
      default:
        return baseStyles
    }
  }

  const handleClick = () => {
    if (isPast) return

    if (coaches.length === 0) {
      // For not registered slots, just toggle selection
      onToggleSelection()
    } else {
      // For available slots, show coach selection
      onSlotClick()
    }
  }

  const renderContent = () => {
    switch (cellState) {
      case "past":
        return (
          <div className="text-center">
            <div className="text-xs">Không thể đăng ký</div>
            <div className="text-xs text-red-500 mt-1">Đã qua</div>
          </div>
        )
      case "not-registered":
        return (
          <div className="text-center">
            <div className="text-xs">Chưa đăng ký</div>
            {isSelected && (
              <div className="mt-2">
                <Plus className="w-4 h-4 mx-auto text-emerald-600" />
              </div>
            )}
          </div>
        )
      case "available":
        if (coaches.length === 1) {
          return (
            <div className="text-center space-y-2">
              <div className="w-8 h-8 rounded-full bg-emerald-200 flex items-center justify-center mx-auto">
                <User className="w-4 h-4 text-emerald-700" />
              </div>
              <div>
                <div className="text-xs font-medium">Có sẵn</div>
                <div className="text-xs">Tạo lịch hẹn</div>
              </div>
              {isSelected && <div className="text-xs text-emerald-600">✓ Đã chọn</div>}
            </div>
          )
        } else {
          return (
            <div className="text-center space-y-2">
              <div className="flex -space-x-1 justify-center">
                {coaches.slice(0, 3).map((coach, index) => (
                  <div
                    key={coach.coachId}
                    className="w-6 h-6 rounded-full bg-emerald-200 border border-white flex items-center justify-center"
                    style={{ zIndex: 3 - index }}
                  >
                    <User className="w-3 h-3 text-emerald-700" />
                  </div>
                ))}
                {coaches.length > 3 && (
                  <div className="w-6 h-6 rounded-full bg-emerald-300 border border-white flex items-center justify-center">
                    <span className="text-xs font-medium text-emerald-800">+{coaches.length - 3}</span>
                  </div>
                )}
              </div>
              <div>
                <div className="text-xs font-medium">Có sẵn</div>
                <div className="text-xs">{coaches.length} coach</div>
              </div>
              {isSelected && <div className="text-xs text-emerald-600">✓ Đã chọn</div>}
            </div>
          )
        }
      default:
        return null
    }
  }

  return (
    <div className={getCellStyles()} onClick={handleClick}>
      {renderContent()}
    </div>
  )
}

export function CoachBookingCalendar() {
  const [currentWeekStart, setCurrentWeekStart] = useState(() => {
    const today = new Date()
    const monday = new Date(today)
    monday.setDate(today.getDate() - today.getDay() + 1)
    return monday
  })

  const [filters, setFilters] = useState<FilterState>({
    specialties: [],
    minRating: 0,
  })

  const [isFilterOpen, setIsFilterOpen] = useState(false)
  const [selectedSlot, setSelectedSlot] = useState<{
    timeSlot: TimeSlot
    date: string
    coaches: Coach[]
  } | null>(null)

  const [timeSlots, setTimeSlots] = useState<TimeSlot[]>([])
  const [coachSchedules, setCoachSchedules] = useState<CoachSchedule[]>([])
  const [selectedSlots, setSelectedSlots] = useState<Set<string>>(new Set())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Fetch time slots with error handling and fallback
  useEffect(() => {
    const fetchTimeSlots = async () => {
      try {
        setError(null)
        const response = await fetch("/api/timeslots")

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        const contentType = response.headers.get("content-type")
        if (!contentType || !contentType.includes("application/json")) {
          throw new Error("Response is not JSON")
        }

        const data: TimeSlotResponse = await response.json()
        if (data.status === 200) {
          setTimeSlots(data.data.filter((slot) => !slot.deleted))
        } else {
          throw new Error(data.message || "Failed to fetch time slots")
        }
      } catch (error) {
        console.error("Error fetching time slots:", error)
        setError("Không thể tải time slots từ server, sử dụng dữ liệu mẫu")
        // Use fallback data
        setTimeSlots(fallbackTimeSlots)
      }
    }

    fetchTimeSlots()
  }, [])

  // Fetch coach schedules when week changes
  useEffect(() => {
    const fetchCoachSchedules = async () => {
      if (timeSlots.length === 0) return

      setLoading(true)
      try {
        setError(null)
        const startDate = currentWeekStart.toISOString().split("T")[0]
        const endDate = new Date(currentWeekStart)
        endDate.setDate(currentWeekStart.getDate() + 6)
        const endDateStr = endDate.toISOString().split("T")[0]

        const response = await fetch("/coaches/schedules/available/by-date-and-timeslot", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            startDate,
            endDate: endDateStr,
            timeSlotIds: timeSlots.map((slot) => slot.timeSlotId),
          }),
        })

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }

        const data: CoachScheduleResponse = await response.json()
        if (data.status === 200) {
          setCoachSchedules(data.data.filter((schedule) => !schedule.booked))
        } else {
          throw new Error(data.message || "Failed to fetch coach schedules")
        }
      } catch (error) {
        console.error("Error fetching coach schedules:", error)
        setError("Không thể tải lịch trình coach từ server")
        // Keep existing schedules or set empty array
        setCoachSchedules([])
      } finally {
        setLoading(false)
      }
    }

    fetchCoachSchedules()
  }, [currentWeekStart, timeSlots])

  const getWeekDates = (startDate: Date) => {
    const dates = []
    for (let i = 0; i < 7; i++) {
      const date = new Date(startDate)
      date.setDate(startDate.getDate() + i)
      dates.push(date)
    }
    return dates
  }

  const weekDates = getWeekDates(currentWeekStart)
  const dayLabels = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"]

  const filterCoaches = (coaches: Coach[]): Coach[] => {
    return coaches.filter((coach) => {
      // Specialty filter
      if (filters.specialties.length > 0) {
        const coachSpecialties = coach.specialties.split(",").map((s) => s.trim())
        const hasMatchingSpecialty = coachSpecialties.some((spec) => filters.specialties.includes(spec))
        if (!hasMatchingSpecialty) return false
      }

      // Rating filter
      if (coach.rating < filters.minRating) {
        return false
      }

      return true
    })
  }

  const getCoachesForSlot = (timeSlotId: number, date: string): Coach[] => {
    const schedules = coachSchedules.filter(
      (schedule) => schedule.timeSlot.timeSlotId === timeSlotId && schedule.scheduleDate === date,
    )

    const coaches = schedules.map((schedule) => schedule.coach)
    return filterCoaches(coaches)
  }

  const handleSlotClick = (timeSlot: TimeSlot, date: string) => {
    const coaches = getCoachesForSlot(timeSlot.timeSlotId, date)
    if (coaches.length > 0) {
      setSelectedSlot({ timeSlot, date, coaches })
    }
  }

  const handleBooking = (coachId: string) => {
    console.log("Booking coach:", coachId)
    setSelectedSlot(null)
    // API call for booking would go here
  }

  const toggleSlotSelection = (timeSlotId: number, date: string) => {
    const slotKey = `${date}-${timeSlotId}`
    const newSelected = new Set(selectedSlots)

    if (newSelected.has(slotKey)) {
      newSelected.delete(slotKey)
    } else {
      newSelected.add(slotKey)
    }

    setSelectedSlots(newSelected)
  }

  const isPastDate = (date: Date): boolean => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    const checkDate = new Date(date)
    checkDate.setHours(0, 0, 0, 0)
    return checkDate < today
  }

  if (loading && timeSlots.length === 0) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-emerald-50 to-white p-4 flex items-center justify-center">
        <div className="text-center space-y-4">
          <Loader2 className="w-8 h-8 animate-spin mx-auto text-emerald-600" />
          <p className="text-slate-600">Đang tải lịch trình...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 to-white p-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-slate-900 mb-2">Lịch Đặt Coach</h1>
          <p className="text-slate-600">Tìm và đặt lịch với các coach chuyên nghiệp</p>
          {error && (
            <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 text-yellow-600" />
              <span className="text-sm text-yellow-700">{error}</span>
            </div>
          )}
        </div>

        {/* Week Navigation */}
        <WeekNavigation
          currentWeekStart={currentWeekStart}
          onWeekChange={setCurrentWeekStart}
          selectedSlots={selectedSlots}
          onSlotSelectionChange={() => {
            // Handle slot selection management
            console.log("Selected slots:", Array.from(selectedSlots))
          }}
        />

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Filter Sidebar */}
          <div className="lg:col-span-1">
            <FilterSidebar
              filters={filters}
              onFiltersChange={setFilters}
              isOpen={isFilterOpen}
              onToggle={() => setIsFilterOpen(!isFilterOpen)}
            />
          </div>

          {/* Calendar Grid */}
          <div className="lg:col-span-3">
            <Card className="bg-white/80 backdrop-blur-sm border-emerald-100">
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Calendar className="w-5 h-5 text-emerald-600" />
                  <span>Lịch Tuần</span>
                </CardTitle>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <div className="flex items-center justify-center h-64">
                    <div className="text-center space-y-4">
                      <Loader2 className="w-8 h-8 animate-spin mx-auto text-emerald-600" />
                      <p className="text-slate-600">Đang tải dữ liệu...</p>
                    </div>
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full border-collapse">
                      {/* Table Header */}
                      <thead>
                        <tr>
                          <th className="p-3 text-left font-semibold text-slate-700 border-b border-emerald-100 min-w-[100px]">
                            Giờ
                          </th>
                          {weekDates.map((date, index) => {
                            const isToday = date.toDateString() === new Date().toDateString()
                            return (
                              <th
                                key={index}
                                className={`p-3 text-center font-semibold border-b border-emerald-100 min-w-[140px] ${isToday ? "text-emerald-600 bg-emerald-50" : "text-slate-700"
                                  }`}
                              >
                                <div className="flex flex-col">
                                  <span className="text-sm">{dayLabels[index]}</span>
                                  <span className="text-xs opacity-75">
                                    {date.getDate().toString().padStart(2, "0")}-
                                    {(date.getMonth() + 1).toString().padStart(2, "0")}
                                  </span>
                                </div>
                              </th>
                            )
                          })}
                        </tr>
                      </thead>

                      {/* Table Body */}
                      <tbody>
                        {timeSlots.map((timeSlot) => (
                          <tr key={timeSlot.timeSlotId}>
                            {/* Time Slot Label */}
                            <td className="p-3 font-medium text-slate-700 border-r border-emerald-100 bg-emerald-50/50">
                              <div>
                                <div className="text-sm font-semibold">{timeSlot.label}</div>
                                <div className="text-xs text-slate-500">
                                  {timeSlot.startTime.slice(0, 5)} - {timeSlot.endTime.slice(0, 5)}
                                </div>
                              </div>
                            </td>

                            {/* Week Day Cells */}
                            {weekDates.map((date, dayIndex) => {
                              const dateStr = date.toISOString().split("T")[0]
                              const coaches = getCoachesForSlot(timeSlot.timeSlotId, dateStr)
                              const isToday = date.toDateString() === new Date().toDateString()
                              const isPast = isPastDate(date)
                              const slotKey = `${dateStr}-${timeSlot.timeSlotId}`
                              const isSelected = selectedSlots.has(slotKey)

                              return (
                                <td
                                  key={dayIndex}
                                  className={`p-2 border-r border-b border-emerald-100 ${isToday ? "bg-emerald-50/30" : ""
                                    }`}
                                >
                                  <TimeSlotCell
                                    timeSlot={timeSlot}
                                    date={dateStr}
                                    coaches={coaches}
                                    onSlotClick={() => handleSlotClick(timeSlot, dateStr)}
                                    isSelected={isSelected}
                                    onToggleSelection={() => toggleSlotSelection(timeSlot.timeSlotId, dateStr)}
                                    isPast={isPast}
                                  />
                                </td>
                              )
                            })}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Coach Selection Modal */}
        {selectedSlot && (
          <CoachSelectionModal
            isOpen={!!selectedSlot}
            onClose={() => setSelectedSlot(null)}
            coaches={selectedSlot.coaches}
            timeSlot={selectedSlot.timeSlot}
            onBook={handleBooking}
          />
        )}
      </div>
    </div>
  )
}
