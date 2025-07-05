import { AxiosError } from 'axios'
import type { 
  ApiError, 
  TimeSlotResponse, 
  WeeklyScheduleApiResponse, 
  WeeklyScheduleResponse,
  WeeklyScheduleApiSlot 
} from '@/types/api'

/**
 * Handle and format API errors consistently
 */
export function handleApiError(error: unknown): string {
  if (error instanceof AxiosError) {
    // Handle Axios-specific errors
    const apiError = error.response?.data as ApiError
    return apiError?.message || error.message || 'Network error occurred'
  }
  
  if (error instanceof Error) {
    return error.message
  }
  
  return 'An unexpected error occurred'
}

/**
 * Transform backend API data to frontend format
 */
export class DataTransformer {
  /**
   * Transform time slot data from API to frontend format
   */
  static transformTimeSlot(apiTimeSlot: TimeSlotResponse) {
    return {
      timeSlotId: apiTimeSlot.timeSlotId,
      label: apiTimeSlot.label,
      startTime: apiTimeSlot.startTime,
      endTime: apiTimeSlot.endTime,
      deleted: apiTimeSlot.deleted || false
    }
  }
  /**
   * Get status priority for sorting appointments
   */
  static getStatusPriority(status: string): number {
    const priorities = {
      'COMPLETED': 1,
      'MISSED': 1,
      'CONFIRMED': 2,
      'SCHEDULED': 3,
      'CANCELLED': 4
    }
    return priorities[status.toUpperCase() as keyof typeof priorities] || 5
  }

  /**
   * Transform weekly schedule data from API to frontend format
   */
  static transformWeeklySchedule(apiSchedule: WeeklyScheduleApiResponse): WeeklyScheduleResponse {
    return {
      weekStart: apiSchedule.weekStartDate,
      weekEnd: apiSchedule.weekEndDate,
      registeredSlots: apiSchedule.registeredSlots?.map((slot: WeeklyScheduleApiSlot) => {
        // Transform all appointments
        const appointments = slot.appointmentDetails?.map(appointment => ({
          appointmentId: appointment.appointmentId,
          clientName: appointment.clientName,
          clientId: parseInt(appointment.clientId),
          status: appointment.status.toLowerCase() as 'confirmed' | 'scheduled' | 'cancelled' | 'completed' | 'missed',
          notes: appointment.notes,
          method: 'phone' as const, // Default since API doesn't provide this
          createdAt: new Date().toISOString() // Default since API doesn't provide this
        })) || []

        // Sort appointments by priority (completed/missed > confirmed > cancelled)
        const sortedAppointments = appointments.sort((a, b) => 
          this.getStatusPriority(a.status) - this.getStatusPriority(b.status)
        )

        // Get primary appointment (highest priority) for cell display
        const primaryAppointment = sortedAppointments.length > 0 ? sortedAppointments[0] : undefined

        return {
          coachScheduleId: slot.coachScheduleId,
          date: slot.date,
          timeSlotId: slot.timeSlotId,
          isAvailable: slot.available,
          appointments: sortedAppointments,
          primaryAppointment
        }
      }) || []
    }
  }

  /**
   * Format date string for API requests (YYYY-MM-DD)
   */
  static formatDateForApi(date: Date): string {
    return date.toISOString().split('T')[0]
  }
  /**
   * Get start of week date (Monday) - T2-CN format
   */
  static getWeekStart(date: Date): Date {
    const weekStart = new Date(date)
    const day = weekStart.getDay() // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    
    // Calculate days to subtract to get to Monday
    // If day = 0 (Sunday), subtract 6 days to get previous Monday
    // If day = 1 (Monday), subtract 0 days
    // If day = 2 (Tuesday), subtract 1 day, etc.
    const diff = day === 0 ? 6 : day - 1
    
    weekStart.setDate(weekStart.getDate() - diff)
    weekStart.setHours(0, 0, 0, 0)
    return weekStart
  }

  /**
   * Format week range string for display
   */
  static formatWeekRange(weekStart: Date): string {
    const weekEnd = new Date(weekStart)
    weekEnd.setDate(weekStart.getDate() + 6)
    
    const startStr = weekStart.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit'
    })
    
    const endStr = weekEnd.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    })
    
    return `${startStr} - ${endStr}`
  }
}
