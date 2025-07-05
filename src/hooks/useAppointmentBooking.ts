import { useState, useCallback } from 'react'
import { appointmentService } from '@/services/appointmentService'
import type { AppointmentData, CreateAppointmentRequest } from '@/services/appointmentService'
import type { Coach, TimeSlot } from '@/services/timeSlotService'
import { useAuth } from './useAuth'

interface BookingState {
  isBookingModalOpen: boolean
  isLoading: boolean
  error: string | null
  selectedCoach: Coach | null
  selectedTimeSlot: TimeSlot | null
  selectedDate: string
  coachScheduleId: number | null
}

export function useAppointmentBooking() {
  const { user, isAuthenticated } = useAuth()

  const [bookingState, setBookingState] = useState<BookingState>({
    isBookingModalOpen: false,
    isLoading: false,
    error: null,
    selectedCoach: null,
    selectedTimeSlot: null,
    selectedDate: '',
    coachScheduleId: null,
  })

  const [lastBookedAppointment, setLastBookedAppointment] = useState<AppointmentData | null>(null)

  // Open booking modal with selected coach and time slot
  const openBookingModal = useCallback((
    coach: Coach,
    timeSlot: TimeSlot,
    date: string,
    scheduleId: number
  ) => {
    if (!isAuthenticated) {
      setBookingState(prev => ({
        ...prev,
        error: 'Bạn cần đăng nhập để đặt lịch tư vấn'
      }))
      return
    }

    setBookingState(prev => ({
      ...prev,
      isBookingModalOpen: true,
      selectedCoach: coach,
      selectedTimeSlot: timeSlot,
      selectedDate: date,
      coachScheduleId: scheduleId,
      error: null,
    }))
  }, [isAuthenticated])

  // Close booking modal and reset state
  const closeBookingModal = useCallback(() => {
    setBookingState(prev => ({
      ...prev,
      isBookingModalOpen: false,
      selectedCoach: null,
      selectedTimeSlot: null,
      selectedDate: '',
      coachScheduleId: null,
      error: null,
    }))
  }, [])

  // Submit booking with note
  const submitBooking = useCallback(async (note: string): Promise<AppointmentData | null> => {
    if (!bookingState.coachScheduleId) {
      setBookingState(prev => ({
        ...prev,
        error: 'Thông tin đặt lịch không hợp lệ'
      }))
      return null
    }

    setBookingState(prev => ({
      ...prev,
      isLoading: true,
      error: null,
    }))

    try {
      const request: CreateAppointmentRequest = {
        coachScheduleId: bookingState.coachScheduleId,
        note: note.trim(),
      }

      console.log('🔄 Submitting booking request:', request)
      const appointmentData = await appointmentService.createAppointment(request)

      console.log('✅ Booking successful:', appointmentData)

      // Store the booked appointment for success display
      setLastBookedAppointment(appointmentData)

      // Close modal and reset state
      closeBookingModal()

      return appointmentData
    } catch (error) {
      console.error('❌ Booking failed:', error)

      const errorMessage = error instanceof Error ? error.message : 'Đã xảy ra lỗi khi đặt lịch'
      setBookingState(prev => ({
        ...prev,
        error: errorMessage,
      }))

      return null
    } finally {
      setBookingState(prev => ({
        ...prev,
        isLoading: false,
      }))
    }
  }, [bookingState.coachScheduleId, closeBookingModal])

  // Clear error
  const clearError = useCallback(() => {
    setBookingState(prev => ({
      ...prev,
      error: null,
    }))
  }, [])

  // Clear last booked appointment (after showing success message)
  const clearLastBookedAppointment = useCallback(() => {
    setLastBookedAppointment(null)
  }, [])

  // Check if user can book appointments
  const canBook = isAuthenticated && user

  return {
    // State
    isBookingModalOpen: bookingState.isBookingModalOpen,
    isLoading: bookingState.isLoading,
    error: bookingState.error,
    selectedCoach: bookingState.selectedCoach,
    selectedTimeSlot: bookingState.selectedTimeSlot,
    selectedDate: bookingState.selectedDate,
    lastBookedAppointment,
    canBook,

    // Actions
    openBookingModal,
    closeBookingModal,
    submitBooking,
    clearError,
    clearLastBookedAppointment,
  }
}
