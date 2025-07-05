import axiosConfig from '@/config/axiosConfig'

// API Response Types
export interface TimeSlotResponse {
  status: number
  message: string
  data: TimeSlot[]
  error: null
  errorCode: null
  timestamp: string
}

export interface TimeSlot {
  timeSlotId: number
  label: string
  startTime: string
  endTime: string
  deleted: boolean
}

export interface Coach {
  coachId: string
  username: string
  email: string
  fullName: string
  specialties: string
  rating: number
}

export interface CoachSchedule {
  scheduleId: number
  coach: Coach
  timeSlot: TimeSlot
  scheduleDate: string
  booked: boolean
}

export interface CoachScheduleResponse {
  status: number
  message: string
  data: CoachSchedule[]
  error: unknown
  errorCode: string
  timestamp: string
}

export interface CoachScheduleRequest {
  startDate: string
  endDate: string
  timeSlotIds: number[]
}

// Fallback mock data in case API fails
export const fallbackTimeSlots: TimeSlot[] = [
  { timeSlotId: 1, label: "08:00", startTime: "08:00:00", endTime: "09:00:00", deleted: false },
  { timeSlotId: 2, label: "09:00", startTime: "09:00:00", endTime: "10:00:00", deleted: false },
  { timeSlotId: 3, label: "10:00", startTime: "10:00:00", endTime: "11:00:00", deleted: false },
  { timeSlotId: 4, label: "11:00", startTime: "11:00:00", endTime: "12:00:00", deleted: false },
  { timeSlotId: 5, label: "14:00", startTime: "14:00:00", endTime: "15:00:00", deleted: false },
  { timeSlotId: 6, label: "15:00", startTime: "15:00:00", endTime: "16:00:00", deleted: false },
  { timeSlotId: 7, label: "16:00", startTime: "16:00:00", endTime: "17:00:00", deleted: false },
  { timeSlotId: 8, label: "17:00", startTime: "17:00:00", endTime: "18:00:00", deleted: false },
]

// Time slot service functions
export const timeSlotService = {
  /**
   * Fetch all available time slots
   */
  async getTimeSlots(): Promise<TimeSlot[]> {
    try {
      const response = await axiosConfig.get<TimeSlotResponse>('/timeslots')

      if (response.data.status === 200) {
        return response.data.data.filter((slot: TimeSlot) => !slot.deleted)
      } else {
        throw new Error(response.data.message || 'Failed to fetch time slots')
      }
    } catch (error) {
      console.error('Error fetching time slots:', error)
      throw error
    }
  },

  /**
   * Fetch coach schedules for specific date range and time slots
   */
  async getCoachSchedules(request: CoachScheduleRequest): Promise<CoachSchedule[]> {
    try {
      const response = await axiosConfig.post<CoachScheduleResponse>(
        '/coaches/schedules/available/by-date-and-timeslot',
        request
      )

      if (response.data.status === 200) {
        return response.data.data.filter((schedule: CoachSchedule) => !schedule.booked)
      } else {
        throw new Error(response.data.message || 'Failed to fetch coach schedules')
      }
    } catch (error) {
      console.error('Error fetching coach schedules:', error)
      throw error
    }
  }
}

// Utility functions for time formatting
export const timeUtils = {
  /**
   * Convert API time string to formatted string (already in correct format)
   */
  formatTimeFromAPI(timeString: string): string {
    return timeString
  },

  /**
   * Get time label from API string (HH:MM format)
   */
  getTimeLabelFromAPI(timeString: string): string {
    return timeString.slice(0, 5) // Get HH:MM from HH:MM:SS
  },

  /**
   * Transform API CoachSchedule to frontend format
   */
  transformCoachSchedule(apiSchedule: CoachSchedule): {
    scheduleId: number
    coach: Coach
    timeSlot: TimeSlot
    scheduleDate: string
    booked: boolean
  } {
    // Since the API already returns the correct format, just return as is
    return {
      scheduleId: apiSchedule.scheduleId,
      coach: apiSchedule.coach,
      timeSlot: apiSchedule.timeSlot,
      scheduleDate: apiSchedule.scheduleDate,
      booked: apiSchedule.booked
    }
  }
}
