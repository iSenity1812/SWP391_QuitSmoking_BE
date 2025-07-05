import axiosInstance from '@/config/axiosConfig'
import type { ApiResponse } from '@/types/api'

// Appointment Status Constants
export const AppointmentStatus = {
  CANCELLED: 'CANCELLED',
  CONFIRMED: 'CONFIRMED',
  COMPLETED: 'COMPLETED',
  MISSED: 'MISSED'
} as const

export type AppointmentStatusType = typeof AppointmentStatus[keyof typeof AppointmentStatus]

// Response Types
export interface AppointmentMember {
  userId: string
  username: string
  email: string
}

export interface AppointmentCoach {
  coachId: string
  username: string
  email: string
  fullName: string
  rating: number
}

export interface AppointmentTimeSlot {
  timeSlotId: number
  label: string
  startTime: string
  endTime: string
  deleted: boolean
}

export interface AppointmentCoachSchedule {
  scheduleId: number
  coach: AppointmentCoach
  timeSlot: AppointmentTimeSlot
  scheduleDate: string
  booked: boolean
}

export interface AppointmentResponseDTO {
  appointmentId: number
  member: AppointmentMember
  coachSchedule: AppointmentCoachSchedule
  status: AppointmentStatusType
  note: string
  bookingTime: string
}

// API Functions
export const appointmentService = {
  // Update appointment status
  updateAppointmentStatus: async (
    appointmentId: number,
    newStatus: AppointmentStatusType
  ): Promise<AppointmentResponseDTO> => {
    const response = await axiosInstance.put<ApiResponse<AppointmentResponseDTO>>(
      `/appointments/${appointmentId}/status?newStatus=${newStatus}`
    )

    if (response.data.status !== 200) {
      throw new Error(response.data.message || 'Failed to update appointment status')
    }

    return response.data.data
  }
}
