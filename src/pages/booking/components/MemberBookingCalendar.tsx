"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Calendar, Loader2, AlertCircle, CheckCircle, RefreshCcw } from "lucide-react"
import { WeekNavigation } from "./WeekNavigation"
import { FilterSidebar } from "./FilterSidebar"
import { TimeSlotCell } from "./TimeSlotCell"
import { CoachSelectionModal } from "./CoachSelectionModal"
import { BookingNoteModal } from "./BookingNoteModal"
import { toast } from "react-toastify"

// Import custom hooks
import { useBookingCalendar } from "@/hooks/useBookingCalendar"
import { useAppointmentBooking } from "@/hooks/useAppointmentBooking"
import { UpcomingAppointments } from "./UpcomingAppointments"
import { Button } from "@/components/ui/button"
import type { Coach, TimeSlot } from "@/services/timeSlotService"

export function MemberBookingCalendar() {
  const {
    currentWeekStart,
    filters,
    timeSlots,
    selectedSlots,
    loading,
    error,
    setCurrentWeekStart,
    setFilters,
    weekDates,
    getCoachesForSlot,
    getSchedulesForSlot,
    toggleSlotSelection,
    isPastDate,
    refreshSchedules,
  } = useBookingCalendar()

  const {
    isBookingModalOpen,
    isLoading: isBookingLoading,
    error: bookingError,
    selectedCoach,
    selectedTimeSlot,
    selectedDate,
    lastBookedAppointment,
    canBook,
    openBookingModal,
    closeBookingModal,
    submitBooking,
    clearError,
    clearLastBookedAppointment,
  } = useAppointmentBooking()

  const [isFilterOpen, setIsFilterOpen] = useState(false)
  const [selectedSlot, setSelectedSlot] = useState<{
    timeSlot: TimeSlot
    date: string
    coaches: Coach[]
  } | null>(null)

  const dayLabels = ["T2", "T3", "T4", "T5", "T6", "T7", "CN"]

  const handleSlotClick = (timeSlot: TimeSlot, date: string) => {
    const coaches = getCoachesForSlot(timeSlot.timeSlotId, date)
    if (coaches.length > 0) {
      setSelectedSlot({ timeSlot, date, coaches })
    }
  }

  const handleBooking = (coach: Coach, timeSlot: TimeSlot, date: string, scheduleId: number) => {
    if (!canBook) {
      toast.error('Bạn cần đăng nhập để đặt lịch tư vấn')
      return
    }

    setSelectedSlot(null) // Close coach selection modal
    openBookingModal(coach, timeSlot, date, scheduleId)
  }

  const handleBookingSubmit = async (note: string) => {
    const appointment = await submitBooking(note)
    if (appointment) {
      // Refresh schedules to reflect the booking
      refreshSchedules()

      // Show success message for a few seconds
      setTimeout(() => {
        clearLastBookedAppointment()
      }, 5000)
    }
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
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 to-white dark:from-slate-900 dark:to-slate-800 p-4 ">
      <div className="max-w-1xl mx-auto dark:bg-slate-900 dark:text-slate-200">
        {/* Header */}
        <div className="text-center mt-20 mb-8">
          <h1 className="text-2xl font-bold text-slate-900 mb-2 dark:text-white">Lịch Đặt Coach</h1>
          <p className="text-slate-600 dark:text-slate-400">Tìm và đặt lịch với các coach chuyên nghiệp</p>
          {error && (
            <div className="mt-4 p-3 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-500/30 rounded-lg flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 text-yellow-600 dark:text-yellow-400" />
              <span className="text-sm text-yellow-700 dark:text-yellow-300">{error}</span>
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

            {/* Upcoming Appointments */}
            <UpcomingAppointments />
          </div>

          {/* Calendar Grid */}
          <div className="lg:col-span-3">
            <Card className="bg-white/80 backdrop-blur-sm border-emerald-300 dark:border-slate-700 dark:bg-slate-800 shadow-sm">
              <CardHeader>
                <CardTitle className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Calendar className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
                    <span className="dark:text-white">Lịch Tuần</span>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={refreshSchedules}
                    className="text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 dark:text-emerald-400 dark:hover:bg-slate-700"
                  >
                    <RefreshCcw className="w-4 h-4" />
                    <span className="sr-only">Làm mới</span>
                  </Button>
                </CardTitle>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <div className="flex items-center justify-center h-64">
                    <div className="text-center space-y-4">
                      <Loader2 className="w-8 h-8 animate-spin mx-auto text-emerald-600 dark:text-emerald-400" />
                      <p className="text-slate-600 dark:text-slate-400">Đang tải dữ liệu...</p>
                    </div>
                  </div>
                ) : (
                  <div className="overflow-x-auto custom-scrollbar">
                    <table className="w-full border-collapse dark:border-slate-700">
                      {/* Table Header */}
                      <thead>
                        <tr className="border-b border-slate-200 dark:border-slate-700">
                          <th className="w-20 text-left font-medium text-slate-700 dark:text-slate-300 p-3">Giờ</th>
                          {weekDates.map((date, index) => (
                            <th key={date.toISOString()} className="text-center font-medium text-slate-700 dark:text-slate-300 p-3 min-w-[120px]">
                              <div className="space-y-1">
                                <div className="text-sm">{dayLabels[index]}</div>
                                <div className="text-xs text-slate-500 dark:text-slate-400">
                                  {date.getDate().toString().padStart(2, "0")}/{(date.getMonth() + 1).toString().padStart(2, "0")}
                                </div>
                              </div>
                            </th>
                          ))}
                        </tr>
                      </thead>

                      {/* Table Body */}
                      <tbody>
                        {timeSlots.map((timeSlot) => (
                          <tr key={timeSlot.timeSlotId} className="border-t border-slate-100 dark:border-slate-800">
                            <td className="p-3 text-sm font-medium text-slate-600 dark:text-slate-400 align-top">
                              <div className="sticky top-0">
                                {timeSlot.startTime.slice(0, 5)}
                              </div>
                            </td>
                            {weekDates.map((date) => {
                              const dateStr = date.toISOString().split('T')[0]
                              console.log(`🗓️ Processing date: ${dateStr} for slot ${timeSlot.timeSlotId}`)
                              const coaches = getCoachesForSlot(timeSlot.timeSlotId, dateStr)
                              const slotKey = `${dateStr}-${timeSlot.timeSlotId}`
                              const isSelected = selectedSlots.has(slotKey)
                              const isPast = isPastDate(date)

                              return (
                                <td key={dateStr} className="p-2">
                                  <TimeSlotCell
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
            schedules={getSchedulesForSlot(selectedSlot.timeSlot.timeSlotId, selectedSlot.date)}
            timeSlot={selectedSlot.timeSlot}
            date={selectedSlot.date}
            onBook={handleBooking}
          />
        )}

        {/* Booking Note Modal */}
        <BookingNoteModal
          isOpen={isBookingModalOpen}
          onClose={closeBookingModal}
          onSubmit={handleBookingSubmit}
          coach={selectedCoach}
          timeSlot={selectedTimeSlot}
          scheduleDate={selectedDate}
          isLoading={isBookingLoading}
        />

        {/* Success Message */}
        {lastBookedAppointment && (
          <div className="fixed bottom-4 right-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-500/30 rounded-lg p-4 shadow-lg max-w-sm">
            <div className="flex items-center space-x-2">
              <CheckCircle className="w-5 h-5 text-green-600 dark:text-green-400" />
              <div>
                <h4 className="font-medium text-green-800 dark:text-green-300">Đặt lịch thành công!</h4>
                <p className="text-sm text-green-600 dark:text-green-400">
                  Lịch tư vấn với {lastBookedAppointment.coachSchedule.coach.fullName} đã được đặt thành công.
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Error Message */}
        {bookingError && (
          <div className="fixed bottom-4 right-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-500/30 rounded-lg p-4 shadow-lg max-w-sm">
            <div className="flex items-center space-x-2">
              <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400" />
              <div>
                <h4 className="font-medium text-red-800 dark:text-red-300">Đặt lịch thất bại</h4>
                <p className="text-sm text-red-600 dark:text-red-400">{bookingError}</p>
                <button
                  onClick={clearError}
                  className="text-xs text-red-500 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 underline mt-1"
                >
                  Đóng
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
