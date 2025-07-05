import { useState, useEffect, useCallback } from 'react'
import { coachScheduleService } from '@/services/coachScheduleService'
import type { UseWeeklyScheduleState, WeeklyScheduleResponse } from '@/types/api'
import { DataTransformer } from '@/utils/dataTransformers'
import { useAuth } from '@/hooks/useAuth'

/**
 * Custom hook to manage weekly schedule data
 */
export function useWeeklySchedule(requestedCoachId: string | null, currentWeek: Date): UseWeeklyScheduleState & {
  registerSlots: (timeSlotIds: number[], scheduleDate: string) => Promise<void>
  registerMultiDateSlots: (slotsByDate: Record<string, number[]>) => Promise<void>
  unregisterSlot: (scheduleId: number) => Promise<void>
  updateAppointmentStatus: (appointmentId: number, status: 'confirmed' | 'cancelled' | 'completed') => Promise<void>
} {
  const { user } = useAuth()
  const [scheduleData, setScheduleData] = useState<WeeklyScheduleResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const fetchWeeklySchedule = useCallback(async () => {
    // Nếu không có user hoặc user không phải coach thì không fetch
    if (!user || user.role !== 'COACH') {
      setIsLoading(false)
      return
    }

    try {
      setIsLoading(true)
      setError(null)
      
      // 🔧 FIX: Gửi ngày giữa tuần thay vì start date
      // currentWeek là start of week (Sunday), ta cần gửi 1 ngày trong tuần đó
      const middleOfWeek = new Date(currentWeek)
      middleOfWeek.setDate(currentWeek.getDate() + 3) // Thêm 3 ngày = Wednesday
      const dateInWeek = DataTransformer.formatDateForApi(middleOfWeek)
      
      // 🐛 DEBUG: Log request info
      console.log('=== WEEKLY SCHEDULE REQUEST DEBUG ===')
      console.log('Current user:', user.username, '| Role:', user.role, '| ID:', user.userId)
      console.log('Requested Coach ID:', requestedCoachId)
      console.log('Current Week Start (UI):', currentWeek)
      console.log('Middle of Week (API param):', middleOfWeek)
      console.log('DateInWeek param:', dateInWeek)
      console.log('API URL will be:', requestedCoachId ? `/coaches/schedules/weekly/${requestedCoachId}` : `/coaches/schedules/weekly`)
      
      const apiResponse = await coachScheduleService.getWeeklySchedule(requestedCoachId, dateInWeek)
      
      // 🐛 DEBUG: Log response
      console.log('API Response:', apiResponse)
      
      const transformedData = DataTransformer.transformWeeklySchedule(apiResponse)
      
      console.log('Transformed Data:', transformedData)
      console.log('==========================================')
      
      setScheduleData(transformedData)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch weekly schedule'
      setError(errorMessage)
      console.error('Error fetching weekly schedule:', err)
    } finally {
      setIsLoading(false)
    }
  }, [user, requestedCoachId, currentWeek])
  const refetch = useCallback(async () => {
    await fetchWeeklySchedule()
  }, [fetchWeeklySchedule])
  // Updated multi-date registration function  
  const registerSlots = useCallback(async (timeSlotIds: number[], scheduleDate: string) => {
    if (!user || user.role !== 'COACH') throw new Error('User must be a coach to register slots')

    // Build request array with each slot and the provided date
    const slots = timeSlotIds.map(timeSlotId => ({
      timeSlotId,
      scheduleDate
    }))

    await coachScheduleService.registerTimeSlots(slots)
    // Refetch data after successful registration
    await refetch()
  }, [user, refetch])

  // New bulk multi-date registration function
  const registerMultiDateSlots = useCallback(async (slotsByDate: Record<string, number[]>) => {
    if (!user || user.role !== 'COACH') throw new Error('User must be a coach to register slots')

    // Build request array for all dates and slots
    const allSlots = Object.entries(slotsByDate).flatMap(([date, slotIds]) =>
      slotIds.map(timeSlotId => ({
        timeSlotId,
        scheduleDate: date
      }))
    )

    if (allSlots.length === 0) return

    await coachScheduleService.registerTimeSlots(allSlots)
    // Refetch data after successful registration
    await refetch()
  }, [user, refetch])
  const unregisterSlot = useCallback(async (scheduleId: number) => {
    await coachScheduleService.unregisterSlot(scheduleId)
    // Refetch data after successful unregistration
    await refetch()
  }, [refetch])

  const updateAppointmentStatus = useCallback(async (
    appointmentId: number, 
    status: 'confirmed' | 'cancelled' | 'completed'
  ) => {
    await coachScheduleService.updateAppointmentStatus(appointmentId, status)
    // Refetch data after successful status update
    await refetch()
  }, [refetch])
  useEffect(() => {
    fetchWeeklySchedule()
  }, [fetchWeeklySchedule])

  return {
    scheduleData,
    isLoading,
    error,
    refetch,
    registerSlots,
    registerMultiDateSlots,
    unregisterSlot,
    updateAppointmentStatus
  }
}

/**
 * Hook to get current coach ID from auth context
 */
export function useCurrentCoachId(): string | null {
  const { user } = useAuth()
  
  // Chỉ trả về ID nếu user là coach
  if (user && user.role === 'COACH') {
    return user.userId  // ← Sử dụng userId (theo interface đầu tiên)
  }
  
  return null
}
