import { useState, useEffect, useCallback } from 'react'
import { timeSlotService, fallbackTimeSlots } from '@/services/timeSlotService'
import type { TimeSlot, Coach } from '@/services/timeSlotService'
import type { FilterState } from '@/pages/booking/components/FilterSidebar'

export interface TransformedCoachSchedule {
  scheduleId: number
  coach: Coach
  timeSlot: TimeSlot
  scheduleDate: string
  booked: boolean
}

export function useBookingCalendar() {
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

  const [timeSlots, setTimeSlots] = useState<TimeSlot[]>([])
  const [coachSchedules, setCoachSchedules] = useState<TransformedCoachSchedule[]>([])
  const [selectedSlots, setSelectedSlots] = useState<Set<string>>(new Set())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Fetch time slots with error handling and fallback
  useEffect(() => {
    const fetchTimeSlots = async () => {
      try {
        setError(null)
        // console.log('📋 Fetching time slots...')
        const slots = await timeSlotService.getTimeSlots()
        // console.log('Time slots received:', slots.length)
        // console.log('Time slots data:', JSON.stringify(slots, null, 2))
        setTimeSlots(slots)
      } catch (error) {
        console.error('Error fetching time slots:', error)
        setError('Không thể tải time slots từ server, sử dụng dữ liệu mẫu')
        // Use fallback data
        console.log('Using fallback time slots:', fallbackTimeSlots.length)
        setTimeSlots(fallbackTimeSlots)
      }
    }

    fetchTimeSlots()
  }, [])

  // Function to fetch coach schedules (can be called manually for refresh)
  const fetchCoachSchedules = useCallback(async () => {
    if (timeSlots.length === 0) return

    setLoading(true)
    try {
      setError(null)
      const startDate = currentWeekStart.toISOString().split('T')[0]
      const endDate = new Date(currentWeekStart)
      endDate.setDate(currentWeekStart.getDate() + 6)
      const endDateStr = endDate.toISOString().split('T')[0]

      const schedules = await timeSlotService.getCoachSchedules({
        startDate,
        endDate: endDateStr,
        timeSlotIds: timeSlots.map(slot => slot.timeSlotId),
      })

      // Transform the schedules to frontend format (API already returns correct format)
      setCoachSchedules(schedules)

      // Debug: Log raw API response data for troubleshooting
      console.log('🔄 API Response Debug:')
      console.log('Total schedules received:', schedules.length)
      console.log('Date range requested:', startDate, 'to', endDateStr)
      console.log('TimeSlot IDs requested:', timeSlots.map(slot => slot.timeSlotId))

      // Log first few schedules to see the format
      console.log('Sample schedules (first 5):')
      schedules.slice(0, 5).forEach((schedule, index) => {
        console.log(`Schedule ${index + 1}:`, {
          scheduleId: schedule.scheduleId,
          coachName: schedule.coach.fullName,
          timeSlotId: schedule.timeSlot.timeSlotId,
          scheduleDate: schedule.scheduleDate,
          booked: schedule.booked
        })
      })

      // Group schedules by date and timeSlot for easier debugging
      const groupedByDate = schedules.reduce((acc, schedule) => {
        const key = `${schedule.scheduleDate}-${schedule.timeSlot.timeSlotId}`
        if (!acc[key]) acc[key] = []
        acc[key].push(schedule.coach.fullName)
        return acc
      }, {} as Record<string, string[]>)

      console.log('Schedules grouped by date-timeSlot:', groupedByDate)

      // Debug: Log all unique coaches and their specialties
      const uniqueCoaches = Array.from(new Map(schedules.map(s => [s.coach.coachId, s.coach])).values())
      console.log('All available coaches:', uniqueCoaches.map(c => ({
        name: c.fullName,
        specialties: c.specialties,
        rating: c.rating
      })))
    } catch (error) {
      console.error('Error fetching coach schedules:', error)
      setError('Không thể tải lịch trình coach từ server')
      // Keep existing schedules or set empty array
      setCoachSchedules([])
    } finally {
      setLoading(false)
    }
  }, [currentWeekStart, timeSlots])

  // Fetch coach schedules when week changes
  useEffect(() => {
    fetchCoachSchedules()
  }, [fetchCoachSchedules])

  const getWeekDates = (startDate: Date) => {
    const dates = []
    for (let i = 0; i < 7; i++) {
      const date = new Date(startDate)
      date.setDate(startDate.getDate() + i)
      dates.push(date)
    }
    return dates
  }

  const filterCoaches = (coaches: Coach[]): Coach[] => {
    console.log('=== FILTER COACHES START ===')
    console.log('Total coaches to filter:', coaches.length)
    console.log('Current filters:', filters)

    // If no filters are applied, show all coaches
    if (filters.specialties.length === 0 && filters.minRating === 0) {
      console.log('No filters applied, returning all coaches')
      return coaches
    }

    const filteredCoaches = coaches.filter((coach) => {
      console.log(`\n--- Filtering coach: ${coach.fullName} ---`)
      console.log('Raw specialties string:', coach.specialties)
      console.log('Coach rating:', coach.rating)

      // Specialty filter
      if (filters.specialties.length > 0) {
        // Handle API response format where specialties might be "[]" string or actual comma-separated values
        let coachSpecialties: string[] = []

        try {
          // Try to parse as JSON first (in case it's "[]" or '["BEHAVIORAL_THERAPY"]')
          if (coach.specialties.startsWith('[') && coach.specialties.endsWith(']')) {
            const parsed = JSON.parse(coach.specialties)
            console.log('Raw parsed result:', parsed, typeof parsed)

            // The API returns "[BEHAVIORAL_THERAPY]" as string, which when JSON.parsed becomes ["BEHAVIORAL_THERAPY"]
            coachSpecialties = parsed.map((spec: string) => {
              const cleaned = spec.toString().trim()
              console.log('Processing specialty:', spec, '->', cleaned)
              return cleaned
            }).filter((s: string) => s.length > 0)
          } else {
            // Fallback to split by comma
            coachSpecialties = coach.specialties.split(',').map(s => s.trim()).filter(s => s.length > 0)
          }
        } catch (error) {
          console.log('JSON parsing failed:', error)
          // If JSON parsing fails, split by comma
          coachSpecialties = coach.specialties.split(',').map(s => s.trim()).filter(s => s.length > 0)
        }

        console.log('Final parsed specialties:', coachSpecialties)
        console.log('Filter specialties:', filters.specialties)

        // If coach has no specialties, don't show when filtering by specialty
        if (coachSpecialties.length === 0) {
          // console.log('❌ Coach has no specialties, filtering out')
          return false
        }

        const hasMatchingSpecialty = coachSpecialties.some(spec => {
          const matches = filters.specialties.includes(spec)
          // console.log(`Checking specialty "${spec}" against filters:`, matches)
          return matches
        })

        console.log('Has matching specialty:', hasMatchingSpecialty)
        if (!hasMatchingSpecialty) {
          // console.log('❌ No matching specialty, filtering out')
          return false
        }
      }

      // Rating filter
      if (coach.rating < filters.minRating) {
        // console.log('❌ Rating too low, filtering out')
        return false
      }

      // console.log('✅ Coach passed all filters')
      return true
    })

    console.log('=== FILTER COACHES END ===')
    console.log('Filtered coaches count:', filteredCoaches.length)
    console.log('Filtered coaches:', filteredCoaches.map(c => c.fullName))
    return filteredCoaches
  }

  const getCoachesForSlot = (timeSlotId: number, date: string): Coach[] => {
    console.log(`\n🔍 Getting coaches for slot ${timeSlotId} on date ${date}`)

    // Debug: Log all available schedules to see what we have
    console.log('=== DEBUG: All coach schedules ===')
    console.log('Total schedules:', coachSchedules.length)

    // Group schedules by date and timeSlot for better debugging
    const schedulesByDate = coachSchedules.reduce((acc, schedule) => {
      const key = `${schedule.scheduleDate}-slot${schedule.timeSlot.timeSlotId}`
      if (!acc[key]) acc[key] = []
      acc[key].push(schedule.coach.fullName)
      return acc
    }, {} as Record<string, string[]>)

    console.log('Schedules by date-slot:', schedulesByDate)

    // Check if we have any schedules for the requested date
    const schedulesForDate = coachSchedules.filter(s => s.scheduleDate === date)
    console.log(`Schedules available for date ${date}:`, schedulesForDate.length)

    if (schedulesForDate.length > 0) {
      console.log('Available time slots for this date:',
        [...new Set(schedulesForDate.map(s => s.timeSlot.timeSlotId))].sort()
      )
    }

    // Check if we have any schedules for the requested timeSlot (any date)
    const schedulesForSlot = coachSchedules.filter(s => s.timeSlot.timeSlotId === timeSlotId)
    console.log(`Schedules available for slot ${timeSlotId} (any date):`, schedulesForSlot.length)

    if (schedulesForSlot.length > 0) {
      console.log('Available dates for this slot:',
        [...new Set(schedulesForSlot.map(s => s.scheduleDate))].sort()
      )
    }

    const schedules = coachSchedules.filter(
      schedule => schedule.timeSlot.timeSlotId === timeSlotId && schedule.scheduleDate === date
    )

    console.log(`Found ${schedules.length} schedules for exact match (slot ${timeSlotId} + date ${date})`)

    const coaches = schedules.map(schedule => schedule.coach)
    console.log('Raw coaches for slot:', coaches.map(c => ({
      name: c.fullName,
      specialties: c.specialties,
      rating: c.rating
    })))

    const filteredCoaches = filterCoaches(coaches)
    // console.log(`Returning ${filteredCoaches.length} filtered coaches`)

    return filteredCoaches
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

  const getSchedulesForSlot = (timeSlotId: number, date: string): TransformedCoachSchedule[] => {
    return coachSchedules.filter(
      schedule => schedule.timeSlot.timeSlotId === timeSlotId && schedule.scheduleDate === date
    )
  }

  const refreshSchedules = useCallback(() => {
    fetchCoachSchedules()
  }, [fetchCoachSchedules])

  return {
    // State
    currentWeekStart,
    filters,
    timeSlots,
    coachSchedules,
    selectedSlots,
    loading,
    error,

    // Actions
    setCurrentWeekStart,
    setFilters,
    setSelectedSlots,
    refreshSchedules,

    // Computed values
    weekDates: getWeekDates(currentWeekStart),

    // Helper functions
    getCoachesForSlot,
    getSchedulesForSlot,
    toggleSlotSelection,
    isPastDate,
  }
}
