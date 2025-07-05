import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight, Settings } from "lucide-react"

interface WeekNavigationProps {
  currentWeekStart: Date
  onWeekChange: (date: Date) => void
  selectedSlots: Set<string>
  onSlotSelectionChange: () => void
}

export function WeekNavigation({ currentWeekStart, onWeekChange, selectedSlots, onSlotSelectionChange }: WeekNavigationProps) {
  const formatWeekRange = (startDate: Date) => {
    const endDate = new Date(startDate)
    endDate.setDate(startDate.getDate() + 6)

    const startStr = `${startDate.getDate().toString().padStart(2, "0")}/${(startDate.getMonth() + 1).toString().padStart(2, "0")}/${startDate.getFullYear()}`
    const endStr = `${endDate.getDate().toString().padStart(2, "0")}/${(endDate.getMonth() + 1).toString().padStart(2, "0")}/${endDate.getFullYear()}`

    return `${startStr} - ${endStr}`
  }

  const goToPreviousWeek = () => {
    const prevWeek = new Date(currentWeekStart)
    prevWeek.setDate(currentWeekStart.getDate() - 7)
    onWeekChange(prevWeek)
  }

  const goToNextWeek = () => {
    const nextWeek = new Date(currentWeekStart)
    nextWeek.setDate(currentWeekStart.getDate() + 7)
    onWeekChange(nextWeek)
  }

  const goToCurrentWeek = () => {
    const today = new Date()
    const monday = new Date(today)
    monday.setDate(today.getDate() - today.getDay() + 1)
    onWeekChange(monday)
  }

  return (
    <div className="flex items-center justify-between mb-6">
      <div className="flex items-center space-x-4">
        <Button variant="outline" size="sm" onClick={goToPreviousWeek} className="dark:bg-slate-800 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-700">
          <ChevronLeft className="w-4 h-4" />
        </Button>

        <div className="text-lg font-semibold text-slate-900 dark:text-slate-200">{formatWeekRange(currentWeekStart)}</div>

        <Button variant="outline" size="sm" onClick={goToNextWeek} className="dark:bg-slate-800 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-700">
          <ChevronRight className="w-4 h-4" />
        </Button>

        <Button variant="outline" size="sm" onClick={goToCurrentWeek} className="dark:bg-slate-800 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-700">
          Tuần này
        </Button>
      </div>

      <div className="flex items-center space-x-2">
        {selectedSlots.size > 0 && (
          <Badge variant="secondary" className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900/50 dark:text-emerald-300">
            {selectedSlots.size} slot đã chọn
          </Badge>
        )}
        <Button
          variant="outline"
          size="sm"
          className="bg-gradient-to-r from-emerald-50 to-emerald-100 border-emerald-200 hover:from-emerald-100 hover:to-emerald-200 dark:from-slate-700 dark:to-slate-600 dark:border-slate-500 dark:text-slate-200 dark:hover:from-slate-600 dark:hover:to-slate-500"
          onClick={onSlotSelectionChange}
        >
          <Settings className="w-4 h-4 mr-2" />
          Quản lý lựa chọn
        </Button>
      </div>
    </div>
  )
}