"use client"

import React, { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { toast } from "react-toastify"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  ChevronLeft,
  ChevronRight,
  Plus,
  Edit,
  Trash2,
  CheckCircle,
  Clock,
  X,
  Calendar,
  Settings,
  CheckCircle2,
  Loader2,
  AlertCircle
} from "lucide-react"

// Import custom hooks and types
import { useTimeSlots } from "@/hooks/useTimeSlots"
import { useWeeklySchedule, useCurrentCoachId } from "@/hooks/useWeeklySchedule"
import { useAppointmentStatus } from "@/hooks/useAppointmentStatus"
import { AppointmentStatus, type AppointmentStatusType } from "@/services/appointmentServices"
import type { TimeSlot, WeeklyScheduleSlot, WeeklyScheduleResponse } from "@/types/api"
import { DataTransformer } from "@/utils/dataTransformers"

// Status Colors & Icons  
const statusColors = {
  confirmed: 'bg-green-100 border-green-300 text-green-800 dark:bg-green-900/20 dark:border-green-700 dark:text-green-300 cursor-pointer',
  scheduled: 'bg-blue-100 border-blue-300 text-blue-800 dark:bg-blue-900/20 dark:border-blue-700 dark:text-blue-300 cursor-pointer',
  cancelled: 'bg-red-100 border-red-300 text-red-800 dark:bg-red-900/20 dark:border-red-700 dark:text-red-300 cursor-pointer',
  completed: 'bg-cyan-100 border-cyan-300 text-cyan-800 dark:bg-cyan-900/20 dark:border-cyan-700 dark:text-cyan-300 cursor-pointer',
  missed: 'bg-orange-100 border-orange-300 text-orange-800 dark:bg-orange-900/20 dark:border-orange-700 dark:text-orange-300 cursor-pointer',
  available: 'bg-emerald-50 border-emerald-200 text-emerald-600 dark:bg-emerald-900/10 dark:border-emerald-600 dark:text-emerald-400 cursor-pointer hover:bg-emerald-100 hover:border-emerald-300',
  unavailable: 'bg-slate-100 border-slate-200 text-slate-500 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-400 cursor-pointer'
}

const statusIcons = {
  confirmed: <CheckCircle className="w-4 h-4 text-green-600" />,
  scheduled: <Clock className="w-4 h-4 text-blue-600" />,
  cancelled: <X className="w-4 h-4 text-red-600" />,
  completed: <CheckCircle2 className="w-4 h-4 text-emerald-600" />,
  missed: <AlertCircle className="w-4 h-4 text-orange-600" />,
  available: <Plus className="w-4 h-4 text-emerald-600" />,
  unavailable: null
}

// Week Navigation Component
interface WeekNavigationProps {
  currentWeekStart: Date
  onWeekChange: (date: Date) => void
  setIsRegistrationOpen: (open: boolean) => void
}

function WeekNavigation({ currentWeekStart, onWeekChange, setIsRegistrationOpen }: WeekNavigationProps) {

  const formatWeekRange = (startDate: Date) => {
    const endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 6);

    const startStr = startDate.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
    const endStr = endDate.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });

    return `${startStr} - ${endStr}`;
  };

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
    const startOfWeek = DataTransformer.getWeekStart(today) // Sử dụng T2-CN format
    onWeekChange(startOfWeek)
  }

  return (
    <div className="flex items-center justify-between mb-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="sm" onClick={goToPreviousWeek}>
          <ChevronLeft className="w-4 h-4" />
        </Button>

        <div className="text-lg font-semibold text-slate-900 dark:text-white">
          {formatWeekRange(currentWeekStart)}
        </div>

        <Button variant="outline" size="sm" onClick={goToNextWeek}>
          <ChevronRight className="w-4 h-4" />
        </Button>

        <Button variant="outline" size="sm" onClick={goToCurrentWeek}>
          Tuần này
        </Button>
      </div>      <div className="flex items-center space-x-2">
        <Button
          variant="outline"
          size="sm"
          className="bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-emerald-900/20 dark:to-emerald-800/20 border-emerald-200 dark:border-emerald-700 hover:from-emerald-100 hover:to-emerald-200 dark:hover:from-emerald-900/30 dark:hover:to-emerald-800/30"
          onClick={() => setIsRegistrationOpen(true)}
        >
          <Settings className="w-4 h-4 mr-2" />
          Đăng ký Slots
        </Button>
      </div>
    </div>
  )
}

// Render the Registration Dialog separately
const RegistrationDialog = ({
  isOpen,
  onClose,
  onSuccess,
  timeSlots,
  registerMultiDateSlots,
  currentWeek,
  scheduleData
}: {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
  timeSlots: TimeSlot[]
  registerMultiDateSlots: (slotsByDate: Record<string, number[]>) => Promise<void>
  currentWeek: Date
  scheduleData: WeeklyScheduleResponse | null
}) => {  // Helper function to check if a slot can be registered
  const canRegisterSlot = (date: string, timeSlot: TimeSlot): boolean => {
    const now = new Date()

    // Parse the date properly - date string should be in YYYY-MM-DD format
    const slotDate = new Date(date + 'T00:00:00')
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    // Get slot date at midnight for comparison
    const slotDateOnly = new Date(slotDate)
    slotDateOnly.setHours(0, 0, 0, 0)

    console.log('=== SLOT REGISTRATION CHECK ===')
    console.log('Now:', now.toLocaleString('vi-VN'))
    console.log('Now ISO:', now.toISOString())
    console.log('Slot date input:', date)
    console.log('Slot date parsed:', slotDate.toISOString())
    console.log('Slot time:', timeSlot.startTime)
    console.log('Today midnight:', today.toLocaleString('vi-VN'))
    console.log('Today ISO:', today.toISOString())
    console.log('Slot date midnight:', slotDateOnly.toLocaleString('vi-VN'))
    console.log('Slot date midnight ISO:', slotDateOnly.toISOString())

    // Case 1: Slot in the past - CANNOT register
    if (slotDateOnly < today) {
      console.log('❌ Slot in past')
      return false
    }

    // Case 2: Slot in the future - CAN register
    if (slotDateOnly > today) {
      console.log('✅ Slot in future')
      return true
    }

    // Case 3: Slot is today - check if current time < (slot start time - 1 hour)
    if (slotDateOnly.getTime() === today.getTime()) {
      // Parse slot start time (format: "HH:MM:SS" or "HH:MM")
      const [hours, minutes] = timeSlot.startTime.split(':').map(Number)

      // Create slot start time for today
      const slotStartTime = new Date()
      slotStartTime.setHours(hours, minutes, 0, 0)

      // Calculate 1 hour before slot start time
      const oneHourBefore = new Date(slotStartTime.getTime() - 60 * 60 * 1000)

      console.log('Slot start time:', slotStartTime.toLocaleString('vi-VN'))
      console.log('One hour before:', oneHourBefore.toLocaleString('vi-VN'))
      console.log('Can register (now < oneHourBefore):', now < oneHourBefore)

      // Can register if current time is before the 1-hour threshold
      return now < oneHourBefore
    }

    console.log('❌ Default false case')
    return false
  }
  // Multi-date selection state: Map<date, slotIds[]>
  const [selectedSlotsByDate, setSelectedSlotsByDate] = useState<Record<string, number[]>>({})
  const [activeDate, setActiveDate] = useState<string>('') // Currently viewing date
  const [isLoading, setIsLoading] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)

  // Generate dates for the current week
  const weekDates = React.useMemo(() => {
    const dates = []
    for (let i = 0; i < 7; i++) {
      const date = new Date(currentWeek)
      date.setDate(currentWeek.getDate() + i)
      dates.push(date)
    }
    return dates
  }, [currentWeek])  // Reset states when dialog opens/closes
  React.useEffect(() => {
    if (!isOpen) {
      setSelectedSlotsByDate({})
      setActiveDate('')
      setIsLoading(false)
      setShowSuccess(false)
    } else {
      // Set default active date to first available day
      const now = new Date()
      now.setHours(0, 0, 0, 0)
      const firstAvailableDate = weekDates.find(d => d >= now)

      if (firstAvailableDate) {
        setActiveDate(firstAvailableDate.toISOString().split('T')[0])
      } else {
        setActiveDate(weekDates[0].toISOString().split('T')[0])
      }
    }
  }, [isOpen, weekDates])  // Helper functions for multi-date slot management
  const getSelectedSlotsForDate = (date: string): number[] => {
    return selectedSlotsByDate[date] || []
  }

  const toggleSlotForDate = (date: string, slotId: number) => {
    // Get slot data to check current status
    const slotData = scheduleData?.registeredSlots.find(
      s => s.date === date && s.timeSlotId === slotId
    )

    const hasAppointments = slotData?.appointments && slotData.appointments.length > 0

    // Don't allow toggling if slot has appointments
    if (hasAppointments) return

    setSelectedSlotsByDate(prev => {
      const currentSlots = prev[date] || []
      const newSlots = currentSlots.includes(slotId)
        ? currentSlots.filter(id => id !== slotId)
        : [...currentSlots, slotId]

      return {
        ...prev,
        [date]: newSlots
      }
    })
  }

  const handleSlotToggle = (slotId: number) => {
    if (!activeDate) return
    toggleSlotForDate(activeDate, slotId)
  }  // Simple click handler for slot selection
  const handleSlotClick = (slotId: number) => {
    if (!activeDate) return

    // Get slot data to check if it has appointments
    const slotData = scheduleData?.registeredSlots.find(
      s => s.date === activeDate && s.timeSlotId === slotId
    )

    const hasAppointments = slotData?.appointments && slotData.appointments.length > 0

    // Don't allow selection if slot has appointments
    if (hasAppointments) return

    handleSlotToggle(slotId)
  }// Multi-date registration logic
  const handleRegister = async () => {
    const totalSlots = Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0)

    if (totalSlots === 0) {
      toast.warn("Vui lòng chọn ít nhất một time slot", {
        position: "top-right",
        autoClose: 3000,
      })
      return
    }

    setIsLoading(true)

    try {
      // Use the new multi-date registration function
      await registerMultiDateSlots(selectedSlotsByDate)

      setIsLoading(false)
      setShowSuccess(true)

      const dateCount = Object.keys(selectedSlotsByDate).filter(date => selectedSlotsByDate[date].length > 0).length
      toast.success(`Đã đăng ký thành công ${totalSlots} time slot${totalSlots > 1 ? 's' : ''} cho ${dateCount} ngày!`, {
        position: "top-right",
        autoClose: 3000,
      })

      // Auto close after success
      setTimeout(() => {
        setShowSuccess(false)
        onSuccess()
        onClose()
      }, 1500)

    } catch (error) {
      console.error("Error registering slots:", error)
      setIsLoading(false)

      const errorMessage = error instanceof Error ? error.message : "Có lỗi xảy ra khi đăng ký slots"
      toast.error(errorMessage, {
        position: "top-right",
        autoClose: 5000,
      })
    }
  }

  if (!isOpen) return null

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-lg">
        <DialogHeader className="relative">
          <DialogTitle>Quản lý Time Slots</DialogTitle>
          <Button
            variant="ghost"
            size="sm"
            onClick={onClose}
            className="absolute right-0 top-0 text-slate-400 hover:text-slate-600"
          >
            {/* <X className="w-4 h-4" /> */}
          </Button>
        </DialogHeader>        {showSuccess ? (
          <div className="text-center space-y-4 py-8">
            <div className="mx-auto w-20 h-20 bg-gradient-to-br from-green-400 to-green-600 rounded-full flex items-center justify-center animate-bounce">
              <CheckCircle2 className="w-10 h-10 text-white" />
            </div>
            <div className="space-y-2">
              <h3 className="text-xl font-bold text-slate-900 dark:text-white">
                Đăng ký thành công! 🎉
              </h3>
              <p className="text-slate-600 dark:text-slate-400">
                {(() => {
                  const totalSlots = Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0)
                  const dateCount = Object.keys(selectedSlotsByDate).filter(date => selectedSlotsByDate[date].length > 0).length
                  return `Bạn đã đăng ký ${totalSlots} time slot${totalSlots > 1 ? 's' : ''} cho ${dateCount} ngày`
                })()}
              </p>
            </div>
          </div>
        ) : isLoading ? (
          <div className="text-center space-y-4 py-8">
            <Loader2 className="w-16 h-16 text-emerald-500 animate-spin mx-auto" />
            <div className="space-y-2">
              <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
                Đang đăng ký slots...
              </h3>
              <p className="text-slate-600 dark:text-slate-400">
                Vui lòng đợi một chút
              </p>
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Header */}
            <div className="text-center space-y-4">
              <div className="mx-auto w-16 h-16 bg-gradient-to-br from-emerald-100 to-emerald-200 dark:from-emerald-900/30 dark:to-emerald-800/20 rounded-full flex items-center justify-center">
                <Calendar className="w-8 h-8 text-emerald-600 dark:text-emerald-400" />
              </div>
              <div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white">
                  Đăng ký Time Slots 🗓️
                </h3>
                <p className="text-sm text-slate-600 dark:text-slate-400 mt-2">
                  Chọn các khung giờ bạn muốn mở để nhận lịch hẹn từ thành viên
                </p>
              </div>            </div>            {/* Date Selection with Multi-select */}
            <div className="space-y-3">
              <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
                Chọn ngày đăng ký (có thể chọn nhiều ngày)
              </label>
              <div className="grid grid-cols-7 gap-2">
                {weekDates.map((date, index) => {
                  const dateStr = date.toISOString().split('T')[0]
                  const isActive = activeDate === dateStr
                  const hasSelections = (selectedSlotsByDate[dateStr] || []).length > 0
                  const isToday = date.toDateString() === new Date().toDateString()
                  const isPast = date < new Date(new Date().setHours(0, 0, 0, 0))

                  return (
                    <button
                      key={index}
                      type="button"
                      onClick={() => setActiveDate(dateStr)}
                      disabled={isPast}
                      className={`
                        p-2 rounded-lg text-xs font-medium border-2 transition-all duration-200 relative
                        ${isActive
                          ? 'bg-blue-500 text-white border-blue-500'
                          : isPast
                            ? 'bg-slate-100 text-slate-400 border-slate-200 cursor-not-allowed'
                            : 'bg-white hover:bg-blue-50 text-slate-700 border-slate-200 hover:border-blue-300'
                        }
                        ${isToday && !isPast ? 'ring-2 ring-blue-200' : ''}
                      `}
                    >                      <div className="flex flex-col items-center">
                        <span className="text-[10px] opacity-75">
                          {['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'][index]}
                        </span>
                        <span className="font-bold">{date.getDate()}</span>
                      </div>
                      {hasSelections && (
                        <div className="absolute -top-1 -right-1 w-4 h-4 bg-green-500 rounded-full flex items-center justify-center">
                          <span className="text-[10px] text-white font-bold">
                            {selectedSlotsByDate[dateStr].length}
                          </span>
                        </div>
                      )}
                    </button>
                  )
                })}
              </div>
              <div className="text-center">
                {!activeDate ? (
                  <p className="text-xs text-amber-600">⚠️ Vui lòng chọn ngày để xem/chỉnh sửa slots</p>
                ) : (
                  <p className="text-xs text-blue-600">
                    📅 Đang xem: {new Date(activeDate).toLocaleDateString('vi-VN')}
                    {(() => {
                      const activeSlots = selectedSlotsByDate[activeDate] || []
                      return activeSlots.length > 0 ? ` - ${activeSlots.length} slot đã chọn` : ''
                    })()}
                  </p>
                )}
              </div>
            </div>            {/* Slots Selection for Active Date */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-700 dark:text-slate-300">
                  Chọn khung giờ {activeDate ? `(${getSelectedSlotsForDate(activeDate).length}/8)` : ''}
                </span>
                <div className="flex space-x-2">                  <Button
                  type="button"
                  variant="outline"
                  size="sm" onClick={() => {
                    if (activeDate) {
                      const availableSlots = timeSlots.filter(slot => {
                        const slotData = scheduleData?.registeredSlots.find(
                          s => s.date === activeDate && s.timeSlotId === slot.timeSlotId
                        )
                        const hasAppointments = slotData?.appointments && slotData.appointments.length > 0

                        return canRegisterSlot(activeDate, slot) && !hasAppointments
                      }).map(slot => slot.timeSlotId)

                      setSelectedSlotsByDate(prev => ({
                        ...prev,
                        [activeDate]: availableSlots
                      }))
                    }
                  }}
                  disabled={!activeDate}
                >
                  Chọn tất cả
                </Button>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      if (activeDate) {
                        setSelectedSlotsByDate(prev => ({
                          ...prev,
                          [activeDate]: []
                        }))
                      }
                    }}
                    disabled={!activeDate}
                  >
                    Bỏ chọn
                  </Button>
                </div>
              </div>              <div className="grid grid-cols-2 gap-3 max-h-60 overflow-y-auto select-none">
                <p className="col-span-2 text-xs text-slate-500 text-center mb-2">
                  💡 Tip: Click để chọn/bỏ chọn từng slot
                </p>                {timeSlots.map((slot) => {
                  const currentDateSlots = activeDate ? getSelectedSlotsForDate(activeDate) : []
                  const isSelected = currentDateSlots.includes(slot.timeSlotId)

                  // Check if slot can be registered based on time rules
                  const canRegister = activeDate ? canRegisterSlot(activeDate, slot) : false

                  // Get slot data to check current status (same as TimeSlotCell logic)
                  const slotData = activeDate ? scheduleData?.registeredSlots.find(
                    s => s.date === activeDate && s.timeSlotId === slot.timeSlotId
                  ) : undefined

                  const getSlotStatus = () => {
                    if (!slotData) return 'unavailable' // Not registered yet
                    if (slotData.primaryAppointment) return slotData.primaryAppointment.status
                    return 'available' // Registered but no appointments
                  }

                  const status = getSlotStatus()
                  const hasAppointments = slotData?.appointments && slotData.appointments.length > 0

                  // Only disable if: can't register due to time rules, or has appointments
                  const isDisabled = !canRegister || hasAppointments || !activeDate

                  return (<button
                    key={slot.timeSlotId}
                    onClick={() => !isDisabled && handleSlotClick(slot.timeSlotId)}
                    disabled={isDisabled}
                    className={`p-3 rounded-xl border-2 transition-all duration-200 ${isDisabled
                      ? 'border-slate-200 bg-slate-100 text-slate-400 cursor-not-allowed'
                      : isSelected
                        ? 'border-emerald-300 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-300 hover:scale-105'
                        : status === 'available'
                          ? 'border-amber-200 bg-amber-50 dark:bg-amber-900/20 text-amber-700 dark:text-amber-300 hover:border-amber-300'
                          : 'border-slate-200 dark:border-slate-700 hover:border-emerald-200 hover:scale-105'
                      } ${!isDisabled ? 'cursor-pointer' : ''}`}
                  ><div className="flex items-center justify-between">
                      <div className="text-left">
                        <div className={`font-semibold text-sm ${!canRegister ? 'line-through' : ''}`}>
                          {slot.label}
                        </div>
                        <div className="text-xs opacity-75">
                          {slot.startTime.slice(0, 5)} - {slot.endTime.slice(0, 5)}
                        </div>                        {!canRegister && (
                          <div className="text-xs text-red-500 mt-1">
                            {(() => {
                              if (!activeDate) return ''
                              const slotDate = new Date(activeDate)
                              const today = new Date()
                              today.setHours(0, 0, 0, 0)
                              slotDate.setHours(0, 0, 0, 0)

                              if (slotDate < today) return 'Đã qua'
                              if (slotDate.getTime() === today.getTime()) return 'Quá hạn 1h'
                              return 'Không thể đăng ký'
                            })()}
                          </div>
                        )}
                        {status === 'available' && canRegister && (
                          <div className="text-xs text-amber-600 mt-1">Đã đăng ký - có thể hủy</div>
                        )}
                        {hasAppointments && (
                          <div className="text-xs text-blue-600 mt-1">
                            Có {slotData?.appointments.length} lịch hẹn
                          </div>
                        )}
                      </div>
                      <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${isDisabled
                        ? 'border-slate-300 bg-slate-200'
                        : isSelected
                          ? 'border-emerald-500 bg-emerald-500'
                          : status === 'available'
                            ? 'border-amber-500 bg-amber-100'
                            : 'border-slate-300'
                        }`}>
                        {isSelected && !isDisabled && <CheckCircle className="w-3 h-3 text-white" />}
                        {status === 'available' && !isSelected && <CheckCircle className="w-3 h-3 text-amber-600" />}
                        {isDisabled && <X className="w-3 h-3 text-slate-400" />}
                      </div>
                    </div>
                  </button>
                  )
                })}
              </div>
            </div>            {/* Multi-date Summary */}
            <div className="text-center p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
              <p className="text-sm text-slate-600 dark:text-slate-400">
                {(() => {
                  const totalSlots = Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0)
                  const selectedDates = Object.keys(selectedSlotsByDate).filter(date => selectedSlotsByDate[date].length > 0)

                  if (totalSlots === 0) {
                    return '🤔 Chưa chọn time slot nào'
                  }

                  if (selectedDates.length === 1) {
                    return `✅ Sẽ đăng ký ${totalSlots} slot cho ngày ${new Date(selectedDates[0]).toLocaleDateString('vi-VN')}`
                  }

                  return `✅ Sẽ đăng ký ${totalSlots} slot cho ${selectedDates.length} ngày khác nhau`
                })()}
              </p>
              {Object.keys(selectedSlotsByDate).filter(date => selectedSlotsByDate[date].length > 0).length > 0 && (
                <div className="mt-2 text-xs text-slate-500">                  {Object.entries(selectedSlotsByDate)
                  .filter(([, slots]) => slots.length > 0)
                  .map(([date, slots]) => (
                    <div key={date}>
                      {new Date(date).toLocaleDateString('vi-VN')}: {slots.length} slot{slots.length > 1 ? 's' : ''}
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-3 pt-4 border-t border-slate-200 dark:border-slate-700">
              <Button
                variant="outline"
                onClick={onClose}
                className="flex-1"
              >
                Hủy
              </Button>
              <Button
                onClick={handleRegister}
                disabled={Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0) === 0}
                className={`flex-1 ${Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0) > 0
                  ? 'bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700'
                  : ''
                  }`}
              >
                <Plus className="w-4 h-4 mr-2" />
                Đăng ký {(() => {
                  const total = Object.values(selectedSlotsByDate).reduce((sum, slots) => sum + slots.length, 0)
                  return total > 0 ? `(${total})` : ''
                })()}
              </Button>
            </div>
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
}



// Time Slot Cell Component - Simple Click Only
interface TimeSlotCellProps {
  date: string
  timeSlot: TimeSlot
  slotData?: WeeklyScheduleSlot
  isToday: boolean
  onClick: () => void
}

function TimeSlotCell({
  date,
  timeSlot,
  slotData,
  isToday,
  onClick
}: TimeSlotCellProps) {
  const getSlotStatus = (): keyof typeof statusColors => {
    if (!slotData) return 'unavailable'
    if (slotData.primaryAppointment) return slotData.primaryAppointment.status
    return 'available'
  }

  // Check if slot can be registered
  const canRegisterSlot = (): boolean => {
    const now = new Date()
    const slotDate = new Date(date)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    slotDate.setHours(0, 0, 0, 0)

    if (slotDate < today) return false
    if (slotDate > today) return true

    if (slotDate.getTime() === today.getTime()) {
      const [hours, minutes] = timeSlot.startTime.split(':').map(Number)
      const slotStartTime = new Date()
      slotStartTime.setHours(hours, minutes, 0, 0)
      const oneHourBefore = new Date(slotStartTime.getTime() - 60 * 60 * 1000)
      return now < oneHourBefore
    }
    return false
  }
  const status = getSlotStatus()
  const primaryAppointment = slotData?.primaryAppointment
  const canRegister = canRegisterSlot()

  // Handle cell click - only show slot detail modal
  const handleCellClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    onClick()
  }

  return (
    <div
      onClick={handleCellClick}
      className={`
        min-h-[110px] p-2 border-2 rounded-lg transition-all duration-200 
        ${statusColors[status]}
        ${isToday ? 'ring-2 ring-blue-400 ring-opacity-50' : ''}
        ${status !== 'unavailable' ? 'hover:shadow-md' : ''}
        cursor-pointer select-none
      `}
    >
      <div className="flex flex-col h-full">
        {/* Status Icon */}
        <div className="flex justify-center mb-1">
          {statusIcons[status]}
        </div>

        {/* Content */}
        {primaryAppointment ? (
          <div className="text-center">
            <div className="font-medium text-xs truncate">
              {primaryAppointment.clientName}
            </div>
            <div className="text-xs opacity-75 mt-1">
              {status === 'confirmed' && 'Đã xác nhận'}
              {status === 'scheduled' && 'Chờ xác nhận'}
              {status === 'cancelled' && 'Đã hủy'}
              {status === 'completed' && 'Đã hoàn thành'}
              {status === 'missed' && 'Đã bỏ lỡ'}
            </div>

            {/* Show appointment count if multiple */}
            {slotData?.appointments && slotData.appointments.length > 1 && (
              <div className="text-xs opacity-60 mt-1">
                +{slotData.appointments.length - 1} khác
              </div>
            )}
          </div>) : status === 'available' ? (
            <div className="text-center">
              <div className="text-xs font-medium">Có sẵn</div>
              <div className="text-xs opacity-75 mt-1">Tạo lịch hẹn</div>
            </div>
          ) : (
          <div className="text-center">
            <div className="text-xs opacity-75">
              {canRegister ? 'Chưa đăng ký' : 'Không thể đăng ký'}
            </div>
            {!canRegister && status === 'unavailable' && (
              <div className="text-xs text-red-500 mt-1">
                {new Date(date) < new Date(new Date().setHours(0, 0, 0, 0))
                  ? 'Đã qua'
                  : 'Quá hạn 1h'}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

// Main Weekly Schedule Table Component
export function WeeklyScheduleTable() {// Get current coach ID from auth context
  const coachId = useCurrentCoachId()
  const [currentWeekStart, setCurrentWeekStart] = useState<Date>(() => {
    const today = new Date()
    return DataTransformer.getWeekStart(today) // Sử dụng T2-CN format
  })  // Use custom hooks for API data
  const { timeSlots, isLoading: timeSlotsLoading, error: timeSlotsError } = useTimeSlots()
  const {
    scheduleData,
    isLoading: scheduleLoading, error: scheduleError,
    refetch,
    registerMultiDateSlots,
    unregisterSlot
  } = useWeeklySchedule(null, currentWeekStart) // null để lấy lịch của coach hiện tại  // Appointment status hook with success callback to refresh UI
  const { updateAppointmentStatus, isUpdating } = useAppointmentStatus(() => {
    // Force refresh the weekly schedule data when appointment status is updated
    refetch()

    // Auto-close modal after successful update (with small delay)
    setTimeout(() => {
      setSelectedSlot(null)
    }, 1500)
  })

  const [selectedSlot, setSelectedSlot] = useState<{
    date: string
    timeSlot: TimeSlot
    slotData?: WeeklyScheduleSlot
  } | null>(null)
  const [isRegistrationOpen, setIsRegistrationOpen] = useState(false)
  const [isSingleSlotRegistering, setIsSingleSlotRegistering] = useState(false)
  const [isCancellingSlot, setIsCancellingSlot] = useState(false)

  // Single slot registration function
  const handleSingleSlotRegister = async (timeSlotId: number, date: string) => {
    // Check if slot can be registered
    const timeSlot = timeSlots.find(slot => slot.timeSlotId === timeSlotId)
    if (!timeSlot) {
      toast.error("Không tìm thấy thông tin slot", {
        position: "top-right",
        autoClose: 3000,
      })
      return
    }

    // Helper function to check registration eligibility (same as in RegistrationDialog)
    const canRegisterSlot = (slotDate: string, slot: TimeSlot): boolean => {
      const now = new Date()
      const targetDate = new Date(slotDate)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      targetDate.setHours(0, 0, 0, 0)

      if (targetDate < today) return false
      if (targetDate > today) return true

      if (targetDate.getTime() === today.getTime()) {
        const [hours, minutes] = slot.startTime.split(':').map(Number)
        const slotStartTime = new Date()
        slotStartTime.setHours(hours, minutes, 0, 0)
        const oneHourBefore = new Date(slotStartTime.getTime() - 60 * 60 * 1000)
        return now < oneHourBefore
      }
      return false
    }

    if (!canRegisterSlot(date, timeSlot)) {
      toast.error("Không thể đăng ký slot này. Slot phải được đăng ký trước 1 tiếng so với giờ bắt đầu.", {
        position: "top-right",
        autoClose: 5000,
      })
      return
    }

    setIsSingleSlotRegistering(true)
    try {
      // Use the same API as multi-date but with single slot
      await registerMultiDateSlots({ [date]: [timeSlotId] })

      toast.success("Đăng ký slot thành công!", {
        position: "top-right",
        autoClose: 3000,
      })

      refetch()
      setSelectedSlot(null)
    } catch (error) {
      console.error("Error registering single slot:", error)
      const errorMessage = error instanceof Error ? error.message : "Có lỗi xảy ra khi đăng ký slot"
      toast.error(errorMessage, {
        position: "top-right",
        autoClose: 5000,
      })
    } finally {
      setIsSingleSlotRegistering(false)
    }
  }
  const handleRegistrationSuccess = () => {
    // Refresh weekly schedule data
    refetch()
  }

  // Helper function to check if slot can be cancelled
  const canCancelSlot = (date: string, timeSlot: TimeSlot): { canCancel: boolean; reason?: string } => {
    const now = new Date();
    const slotDate = new Date(date + 'T00:00:00');
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    slotDate.setHours(0, 0, 0, 0);

    // Case 1: Slot in the past - CANNOT cancel
    if (slotDate < today) {
      return { canCancel: false, reason: 'Slot đã qua, không thể hủy' };
    }

    // Case 2: Slot in the future - CAN cancel
    if (slotDate > today) {
      return { canCancel: true };
    }

    // Case 3: Slot is today - check if current time < slot start time
    if (slotDate.getTime() === today.getTime()) {
      const [hours, minutes] = timeSlot.startTime.split(':').map(Number);
      const slotStartTime = new Date();
      slotStartTime.setHours(hours, minutes, 0, 0);

      if (now >= slotStartTime) {
        return { canCancel: false, reason: 'Slot đã bắt đầu, không thể hủy' };
      }

      return { canCancel: true };
    }

    return { canCancel: false, reason: 'Không thể hủy slot này' };
  };

  // Helper function to check appointment status actions
  const canUpdateAppointmentStatus = (appointment: { status: string }, newStatus: AppointmentStatusType): { canUpdate: boolean; reason?: string } => {
    const currentStatus = appointment.status.toUpperCase();
    const now = new Date();

    // Get appointment time from selectedSlot context
    if (!selectedSlot) {
      return { canUpdate: false, reason: 'Không có thông tin slot' };
    }

    // Parse appointment date and time
    const appointmentDate = new Date(selectedSlot.date + 'T00:00:00');
    const [hours, minutes] = selectedSlot.timeSlot.startTime.split(':').map(Number);
    const appointmentDateTime = new Date(appointmentDate);
    appointmentDateTime.setHours(hours, minutes, 0, 0);

    // Final states cannot be changed
    if (['CANCELLED', 'COMPLETED', 'MISSED'].includes(currentStatus)) {
      return { canUpdate: false, reason: 'Trạng thái cuối cùng không thể thay đổi' };
    }

    // Can only mark as COMPLETED/MISSED after appointment time has passed
    if (['COMPLETED', 'MISSED'].includes(newStatus) && appointmentDateTime > now) {
      return { canUpdate: false, reason: 'Chỉ có thể hoàn thành/bỏ lỡ sau khi đã qua giờ hẹn' };
    }

    // Can cancel anytime before appointment starts
    if (newStatus === 'CANCELLED' && appointmentDateTime > now) {
      return { canUpdate: true };
    }

    // Can complete if appointment time has passed and status is CONFIRMED
    if (newStatus === 'COMPLETED' && currentStatus === 'CONFIRMED' && appointmentDateTime <= now) {
      return { canUpdate: true };
    }

    // Can mark as MISSED if appointment time has passed and status is CONFIRMED
    if (newStatus === 'MISSED' && currentStatus === 'CONFIRMED' && appointmentDateTime <= now) {
      return { canUpdate: true };
    }

    return { canUpdate: false, reason: 'Không thể thực hiện hành động này' };
  };// Generate week dates (Sunday to Saturday)
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
  const today = new Date().toDateString()
  // Day labels từ T2-CN (Monday to Sunday)
  const dayLabels = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN']

  const handleSlotClick = (date: string, timeSlot: TimeSlot, slotData?: WeeklyScheduleSlot) => {
    setSelectedSlot({ date, timeSlot, slotData })
  }

  const handleWeekChange = (newWeekStart: Date) => {
    setCurrentWeekStart(newWeekStart)
    // Data will be automatically refetched by the hook
  }

  // Cancel slot registration function with time validation
  const handleSlotCancellation = async (scheduleId: number) => {
    if (!selectedSlot) return;

    const { canCancel, reason } = canCancelSlot(selectedSlot.date, selectedSlot.timeSlot);

    if (!canCancel) {
      toast.error(reason || "Không thể hủy slot này", {
        position: "top-right",
        autoClose: 5000,
      });
      return;
    }

    setIsCancellingSlot(true);
    try {
      await unregisterSlot(scheduleId); // Gọi hàm hủy đăng ký từ hook

      toast.success("Hủy đăng ký slot thành công!", {
        position: "top-right",
        autoClose: 3000,
      });

      refetch(); // Tải lại dữ liệu lịch trình sau khi hủy
      setSelectedSlot(null); // Đóng dialog chi tiết slot
    } catch (error) {
      console.error("Error cancelling slot:", error);
      const errorMessage = error instanceof Error ? error.message : "Có lỗi xảy ra khi hủy đăng ký slot";
      toast.error(errorMessage, {
        position: "top-right",
        autoClose: 5000,
      });
    } finally {
      setIsCancellingSlot(false);
    }
  };

  // Loading state
  if (timeSlotsLoading || scheduleLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center space-y-4">
          <Loader2 className="w-8 h-8 animate-spin mx-auto text-emerald-600" />
          <p className="text-slate-600 dark:text-slate-400">
            Đang tải lịch trình...
          </p>
        </div>
      </div>
    )
  }

  // Error state
  if (timeSlotsError || scheduleError) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center space-y-4">
          <AlertCircle className="w-8 h-8 mx-auto text-red-500" />
          <div>
            <h3 className="font-semibold text-slate-900 dark:text-white">
              Có lỗi xảy ra
            </h3>
            <p className="text-slate-600 dark:text-slate-400 mt-1">
              {timeSlotsError || scheduleError}
            </p>
            <Button
              variant="outline"
              size="sm"
              className="mt-3"
              onClick={() => {
                refetch()
                // Also refresh time slots if needed
              }}
            >
              Thử lại
            </Button>
          </div>
        </div>
      </div>
    )
  }
  // No coach ID
  if (!coachId) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center space-y-4">
          <AlertCircle className="w-8 h-8 mx-auto text-amber-500" />
          <div>
            <h3 className="font-semibold text-slate-900 dark:text-white">
              Chỉ dành cho Coach
            </h3>
            <p className="text-slate-600 dark:text-slate-400 mt-1">
              Bạn cần có quyền Coach để truy cập trang này
            </p>
          </div>        </div>
      </div>)
  }

  return (
    <div className="space-y-6">      {/* Week Navigation */}
      <WeekNavigation
        currentWeekStart={currentWeekStart}
        onWeekChange={handleWeekChange}
        setIsRegistrationOpen={setIsRegistrationOpen}
      />

      {/* Weekly Schedule Table */}
      <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="w-5 h-5" />
            <span>Lịch Tuần</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              {/* Table Header */}
              <thead>
                <tr>
                  <th className="p-3 text-left font-semibold text-slate-700 dark:text-slate-300 border-b">
                    Giờ
                  </th>
                  {weekDates.map((date, index) => {
                    const isToday = date.toDateString() === today
                    return (
                      <th
                        key={index}
                        className={`p-3 text-center font-semibold border-b min-w-[120px] ${isToday
                          ? 'text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20'
                          : 'text-slate-700 dark:text-slate-300'
                          }`}
                      >
                        <div className="flex flex-col">
                          <span className="text-sm">{dayLabels[index]}</span>
                          <span className="text-xs opacity-75">
                            {date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' })}
                          </span>
                        </div>
                      </th>
                    )
                  })}
                </tr>
              </thead>

              {/* Table Body */}              <tbody>
                {timeSlots.map((timeSlot) => (
                  <tr key={timeSlot.timeSlotId}>
                    {/* Time Slot Label */}
                    <td className="p-3 font-medium text-slate-700 dark:text-slate-300 border-r">
                      <div>
                        <div className="text-sm">{timeSlot.label}</div>
                        <div className="text-xs opacity-75">
                          {timeSlot.startTime.slice(0, 5)} - {timeSlot.endTime.slice(0, 5)}
                        </div>
                      </div>
                    </td>                    {/* Week Day Cells */}
                    {weekDates.map((date, dayIndex) => {
                      // Fix: Use local date format to avoid timezone issues
                      const year = date.getFullYear()
                      const month = String(date.getMonth() + 1).padStart(2, '0')
                      const day = String(date.getDate()).padStart(2, '0')
                      const dateStr = `${year}-${month}-${day}`

                      const isToday = date.toDateString() === today
                      const slotData = scheduleData?.registeredSlots.find(
                        slot => slot.date === dateStr && slot.timeSlotId === timeSlot.timeSlotId
                      )

                      return (
                        <td key={dayIndex} className="p-2 border-r border-b">                          <TimeSlotCell
                          date={dateStr}
                          timeSlot={timeSlot}
                          slotData={slotData}
                          isToday={isToday}
                          onClick={() => handleSlotClick(dateStr, timeSlot, slotData)}
                        />
                        </td>
                      )
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      {/* Slot Details Dialog */}
      {selectedSlot && (
        <Dialog open={!!selectedSlot} onOpenChange={() => setSelectedSlot(null)}>
          <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>
                Chi tiết Slot - {selectedSlot.timeSlot.label}
              </DialogTitle>
            </DialogHeader>
            <div className="space-y-4">              <div className="text-sm text-slate-600 dark:text-slate-400">
              <strong>Ngày:</strong> {selectedSlot.date.split('-').reverse().join('/')}
            </div>
              <div className="text-sm text-slate-600 dark:text-slate-400">
                <strong>Thời gian:</strong> {selectedSlot.timeSlot.startTime.slice(0, 5)} - {selectedSlot.timeSlot.endTime.slice(0, 5)}
              </div>

              {selectedSlot.slotData?.appointments && selectedSlot.slotData.appointments.length > 0 ? (
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <h4 className="font-medium">Danh sách lịch hẹn ({selectedSlot.slotData.appointments.length})</h4>
                    <Badge variant="secondary" className="text-xs">
                      {selectedSlot.slotData.appointments.length} cuộc hẹn
                    </Badge>
                  </div>

                  <div className="space-y-3 max-h-96 overflow-y-auto">
                    {selectedSlot.slotData.appointments.map((appointment, index) => (
                      <div
                        key={appointment.appointmentId}
                        className={`p-4 rounded-lg border-2 ${index === 0 ? 'bg-emerald-50 dark:bg-emerald-900/20 border-emerald-200 dark:border-emerald-700' :
                          'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700'
                          }`}
                      >
                        {index === 0 && (
                          <div className="text-xs text-emerald-600 dark:text-emerald-400 font-medium mb-2">
                            📌 Ưu tiên cao nhất
                          </div>
                        )}

                        <div className="space-y-2 text-sm">
                          <div className="flex items-center justify-between">
                            <span><strong>Thành viên:</strong> {appointment.clientName}</span>
                            <Badge className={`${statusColors[appointment.status as keyof typeof statusColors]} text-xs`}>
                              {appointment.status === 'confirmed' && 'Đã xác nhận'}
                              {appointment.status === 'scheduled' && 'Chờ xác nhận'}
                              {appointment.status === 'cancelled' && 'Đã hủy'}
                              {appointment.status === 'completed' && 'Đã hoàn thành'}
                              {appointment.status === 'missed' && 'Đã bỏ lỡ'}
                            </Badge>
                          </div>
                          <div><strong>Phương thức:</strong> Online</div>
                          {appointment.notes && (
                            <div><strong>Ghi chú:</strong> {appointment.notes}</div>
                          )}
                          <div className="text-xs text-slate-500 dark:text-slate-400">
                            <strong>ID:</strong> {appointment.appointmentId}
                          </div>
                        </div>                        {/* Action buttons for each appointment */}
                        <div className="flex space-x-2 mt-3">
                          {/* Sửa button - disabled for now */}
                          <Button size="sm" variant="outline" className="text-xs" disabled>
                            <Edit className="w-3 h-3 mr-1" />
                            Sửa
                          </Button>

                          {/* Complete button - only for CONFIRMED status and past appointments */}
                          {(() => {
                            const { canUpdate, reason } = canUpdateAppointmentStatus(appointment, AppointmentStatus.COMPLETED);
                            return (
                              <Button
                                size="sm"
                                variant="outline"
                                className="text-xs"
                                disabled={!canUpdate || isUpdating}
                                title={!canUpdate ? reason : undefined}
                                onClick={() => {
                                  updateAppointmentStatus({
                                    appointmentId: appointment.appointmentId,
                                    newStatus: AppointmentStatus.COMPLETED
                                  });
                                }}
                              >                                {isUpdating ? (
                                <Loader2 className="w-3 h-3 mr-1 animate-spin" />
                              ) : (
                                <CheckCircle2 className="w-3 h-3 mr-1" />
                              )}
                                {isUpdating ? 'Đang xử lý...' : 'Hoàn thành'}
                              </Button>
                            );
                          })()}

                          {/* Cancel button - only if not already cancelled */}
                          {appointment.status !== 'cancelled' && (() => {
                            const { canUpdate, reason } = canUpdateAppointmentStatus(appointment, AppointmentStatus.CANCELLED);
                            return (
                              <Button
                                size="sm"
                                variant="outline"
                                className="text-xs"
                                disabled={!canUpdate || isUpdating}
                                title={!canUpdate ? reason : undefined}
                                onClick={() => {
                                  updateAppointmentStatus({
                                    appointmentId: appointment.appointmentId,
                                    newStatus: AppointmentStatus.CANCELLED
                                  });
                                }}
                              >                                {isUpdating ? (
                                <Loader2 className="w-3 h-3 mr-1 animate-spin" />
                              ) : (
                                <X className="w-3 h-3 mr-1" />
                              )}
                                {isUpdating ? 'Đang xử lý...' : 'Hủy'}
                              </Button>
                            );
                          })()}
                        </div>
                      </div>
                    ))}
                  </div>                  {/* Action buttons removed - only appointment-level actions are available */}
                </div>
              ) : selectedSlot.slotData ? (
                <div className="space-y-3">                  <div className="p-3 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
                  <p className="text-sm text-emerald-700 dark:text-emerald-300">
                    Slot này đã được đăng ký và sẵn sàng nhận lịch hẹn từ thành viên.
                  </p>
                </div>

                  {/* Add slot cancellation button for registered slots without appointments */}
                  <div className="flex space-x-2">
                    {(() => {
                      const { canCancel, reason } = canCancelSlot(selectedSlot.date, selectedSlot.timeSlot);
                      return (
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => {
                            if (selectedSlot.slotData?.coachScheduleId) {
                              handleSlotCancellation(selectedSlot.slotData.coachScheduleId);
                            } else {
                              toast.error("Không tìm thấy ID lịch trình để hủy.", { position: "top-right" });
                            }
                          }}
                          disabled={isCancellingSlot || !canCancel}
                          title={!canCancel ? reason : undefined}
                        >
                          {isCancellingSlot ? (
                            <>
                              <Loader2 className="w-4 h-4 mr-1 animate-spin" />
                              Đang hủy...
                            </>
                          ) : (
                            <>
                              <Trash2 className="w-4 h-4 mr-1" />
                              Hủy đăng ký slot
                            </>
                          )}
                        </Button>
                      );
                    })()}
                    {(() => {
                      const { canCancel, reason } = canCancelSlot(selectedSlot.date, selectedSlot.timeSlot);
                      return !canCancel ? (
                        <div className="text-xs text-red-500 mt-2">
                          ⚠️ {reason}
                        </div>
                      ) : null;
                    })()}
                  </div>
                </div>) : (
                <div className="space-y-3">
                  <div className="p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                    <p className="text-sm text-slate-600 dark:text-slate-400">
                      Slot này chưa được đăng ký. Đăng ký để có thể nhận lịch hẹn từ thành viên.
                    </p>
                  </div>

                  <Button
                    size="sm"
                    className="w-full bg-emerald-500 hover:bg-emerald-600 text-white"
                    onClick={() => handleSingleSlotRegister(selectedSlot.timeSlot.timeSlotId, selectedSlot.date)}
                    disabled={isSingleSlotRegistering}
                  >
                    {isSingleSlotRegistering ? (
                      <>
                        <Loader2 className="w-4 h-4 mr-1 animate-spin" />
                        Đang đăng ký...
                      </>
                    ) : (
                      <>
                        <Plus className="w-4 h-4 mr-1" />
                        Đăng ký Slot
                      </>
                    )}
                  </Button>
                </div>
              )}
            </div>
          </DialogContent>
        </Dialog>
      )}      {/* Registration Dialog */}
      <RegistrationDialog
        isOpen={isRegistrationOpen}
        onClose={() => setIsRegistrationOpen(false)}
        onSuccess={handleRegistrationSuccess} timeSlots={timeSlots}
        registerMultiDateSlots={registerMultiDateSlots}
        currentWeek={currentWeekStart}
        scheduleData={scheduleData}
      />
    </div>
  )
}
