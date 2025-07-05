import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Users, Star, Info, User } from "lucide-react"
import { specialtyOptions } from "@/constants/specialties"
import type { TimeSlot, Coach } from "@/services/timeSlotService"
import type { TransformedCoachSchedule } from "@/hooks/useBookingCalendar"

interface CoachSelectionModalProps {
  isOpen: boolean
  onClose: () => void
  coaches: Coach[]
  schedules: TransformedCoachSchedule[]
  timeSlot: TimeSlot
  date: string
  onBook: (coach: Coach, timeSlot: TimeSlot, date: string, scheduleId: number) => void
}

export function CoachSelectionModal({ isOpen, onClose, coaches, schedules, timeSlot, date, onBook }: CoachSelectionModalProps) {
  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr)
    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

  const getSpecialtyLabel = (specialtyKey: string) => {
    const specialty = specialtyOptions.find((s) => s.key === specialtyKey)
    return specialty ? specialty.label : specialtyKey
  }

  // Helper function to find schedule for a coach
  const findScheduleForCoach = (coach: Coach) => {
    return schedules.find(
      schedule =>
        schedule.coach.coachId === coach.coachId &&
        schedule.timeSlot.timeSlotId === timeSlot.timeSlotId &&
        schedule.scheduleDate === date
    )
  }

  const handleBookCoach = (coach: Coach) => {
    const schedule = findScheduleForCoach(coach)
    if (schedule) {
      onBook(coach, timeSlot, date, schedule.scheduleId)
    } else {
      console.error('Schedule not found for coach:', coach.coachId)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            <Users className="w-5 h-5 text-emerald-600" />
            <span>Chọn Coach</span>
          </DialogTitle>
          <div className="text-sm text-slate-600">
            {formatDate(date)} • {timeSlot.startTime.slice(0, 5)} - {timeSlot.endTime.slice(0, 5)}
          </div>
        </DialogHeader>

        <div className="space-y-4">
          {coaches.map((coach) => (
            <div
              key={coach.coachId}
              className="border border-emerald-100 rounded-lg p-4 hover:border-emerald-200 transition-colors"
            >
              <div className="flex items-start space-x-4">
                <div className="w-16 h-16 rounded-full bg-emerald-100 flex items-center justify-center">
                  <User className="w-8 h-8 text-emerald-600" />
                </div>

                <div className="flex-1 space-y-2">
                  <div className="flex items-center justify-between">
                    <h4 className="font-semibold text-slate-900 dark:text-white">{coach.fullName}</h4>
                  </div>

                  <div className="flex items-center space-x-4 text-sm text-slate-600">
                    <div className="flex items-center space-x-1">
                      <Star className="w-4 h-4 text-yellow-400 fill-current" />
                      <span>{coach.rating.toFixed(1)}</span>
                    </div>
                    <div className="text-slate-500">@{coach.username}</div>
                  </div>

                  <div className="flex flex-wrap gap-1">
                    {coach.specialties.replace(/\[|\]/g, "").split(",").map((spec, index) => (
                      <Badge
                        key={index}
                        variant="secondary"
                        className="text-xs bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-800 dark:text-emerald-200 dark:border-emerald-700"
                      >
                        {getSpecialtyLabel(spec.trim())}
                      </Badge>
                    ))}
                  </div>

                  <div className="flex items-center justify-between pt-2">
                    <div className="text-xs text-slate-500">{coach.email}</div>

                    <div className="flex space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        className="border-emerald-200 text-emerald-700 hover:bg-emerald-50"
                      >
                        <Info className="w-4 h-4 mr-1" />
                        Hồ sơ
                      </Button>
                      <Button
                        size="sm"
                        onClick={() => handleBookCoach(coach)}
                        className="bg-emerald-500 hover:bg-emerald-600 text-white"
                      >
                        Đặt lịch
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  )
}
