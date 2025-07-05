import axios from '@/config/axiosConfig'

// Types for appointment API
export interface CreateAppointmentRequest {
  coachScheduleId: number
  note: string
}

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
  specialties: string
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

export interface AppointmentData {
  appointmentId: number
  member: AppointmentMember
  coachSchedule: AppointmentCoachSchedule
  status: 'CONFIRMED' | 'MISSED' | 'CANCELLED' | 'COMPLETED'
  note: string
  bookingTime: string
}

export interface CreateAppointmentResponse {
  status: number
  message: string
  data: AppointmentData
  error: object
  errorCode: string
  timestamp: string
}

// Helper function to check if error is axios error
function isAxiosError(error: unknown): error is { response?: { data?: { message?: string } }; request?: unknown; message?: string } {
  return typeof error === 'object' && error !== null && ('response' in error || 'request' in error)
}

// Service class for appointment operations
class AppointmentService {
  private baseURL = '/appointments'

  /**
   * Create a new appointment
   */
  async createAppointment(request: CreateAppointmentRequest): Promise<AppointmentData> {
    try {
      console.log('🔄 Creating appointment:', request)

      const response = await axios.post<CreateAppointmentResponse>(this.baseURL, request)

      console.log('✅ Appointment created successfully:', response.data)

      if (response.data.status === 200 && response.data.data) {
        return response.data.data
      } else {
        throw new Error(response.data.message || 'Failed to create appointment')
      }
    } catch (error: unknown) {
      console.error('❌ Error creating appointment:', error)

      // Handle different types of errors
      if (isAxiosError(error) && error.response) {
        // Server responded with error status
        const errorMessage = error.response.data?.message || 'Server error occurred'
        throw new Error(errorMessage)
      } else if (isAxiosError(error) && error.request) {
        // Request was made but no response received
        throw new Error('Không thể kết nối đến server. Vui lòng thử lại.')
      } else {
        // Something else happened
        const message = isAxiosError(error) && error.message ? error.message : 'Đã xảy ra lỗi không xác định'
        throw new Error(message)
      }
    }
  }

  /**
   * Get user's appointments (for future use)
   */
  async getUserAppointments(): Promise<AppointmentData[]> {
    try {
      const response = await axios.get<{ data: AppointmentData[] }>(`${this.baseURL}/user`)
      return response.data.data || []
    } catch (error) {
      console.error('Error fetching user appointments:', error)
      throw error
    }
  }

  /**
   * Cancel an appointment (for future use)
   */
  async cancelAppointment(appointmentId: number): Promise<void> {
    try {
      await axios.delete(`${this.baseURL}/${appointmentId}`)
    } catch (error) {
      console.error('Error cancelling appointment:', error)
      throw error
    }
  }

  /**
   * Update the status of an appointment
   */
  async updateAppointmentStatus(appointmentId: number, status: 'CONFIRMED' | 'MISSED' | 'CANCELLED' | 'COMPLETED'): Promise<AppointmentData> {
    try {
      console.log(`🔄 Updating appointment ${appointmentId} to status ${status}`)

      const response = await axios.put<CreateAppointmentResponse>(`${this.baseURL}/${appointmentId}/status`, null, {
        params: { newStatus: status },
      })

      console.log('✅ Appointment status updated successfully:', response.data)

      if (response.data.status === 200 && response.data.data) {
        return response.data.data
      } else {
        throw new Error(response.data.message || 'Failed to update appointment status')
      }
    } catch (error: unknown) {
      console.error('❌ Error updating appointment status:', error)

      // Handle different types of errors
      if (isAxiosError(error) && error.response) {
        // Server responded with error status
        const errorMessage = error.response.data?.message || 'Server error occurred'
        throw new Error(errorMessage)
      } else if (isAxiosError(error) && error.request) {
        // Request was made but no response received
        throw new Error('Không thể kết nối đến server. Vui lòng thử lại.')
      } else {
        // Something else happened
        const message = isAxiosError(error) && error.message ? error.message : 'Đã xảy ra lỗi không xác định'
        throw new Error(message)
      }
    }
  }
}

// Export singleton instance
export const appointmentService = new AppointmentService()
export default appointmentService
