import { useState, useEffect, useCallback } from 'react'
import { coachScheduleService } from '@/services/coachScheduleService'
import type { UseTimeSlotsState, TimeSlot } from '@/types/api'
import { DataTransformer } from '@/utils/dataTransformers'

/**
 * Custom hook to manage time slots data
 */
export function useTimeSlots(): UseTimeSlotsState {
  const [timeSlots, setTimeSlots] = useState<TimeSlot[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const fetchTimeSlots = useCallback(async () => {
    try {
      setIsLoading(true)
      setError(null)
      
      const response = await coachScheduleService.getTimeSlots()
      const transformedSlots = response
        .filter(slot => !slot.deleted)
        .map(DataTransformer.transformTimeSlot)
      
      setTimeSlots(transformedSlots)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch time slots'
      setError(errorMessage)
      console.error('Error fetching time slots:', err)
    } finally {
      setIsLoading(false)
    }
  }, [])

  const refetch = useCallback(async () => {
    await fetchTimeSlots()
  }, [fetchTimeSlots])

  useEffect(() => {
    fetchTimeSlots()
  }, [fetchTimeSlots])

  return {
    timeSlots,
    isLoading,
    error,
    refetch
  }
}
