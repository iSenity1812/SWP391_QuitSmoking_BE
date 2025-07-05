import { Plus, User } from "lucide-react"
import type { Coach } from "@/services/timeSlotService"

interface TimeSlotCellProps {
  coaches: Coach[]
  onSlotClick: () => void
  isSelected: boolean
  onToggleSelection: () => void
  isPast: boolean
}

export function TimeSlotCell({
  coaches,
  onSlotClick,
  isSelected,
  onToggleSelection,
  isPast,
}: TimeSlotCellProps) {
  // Debug logging for coaches
  // console.log(`📅 TimeSlotCell rendered with ${coaches.length} coaches:`, coaches.map(c => c.fullName))

  // Determine cell state based on the reference image
  const getCellState = () => {
    if (isPast) return "past"
    if (coaches.length === 0) return "not-registered"
    return "available"
  }

  const cellState = getCellState()

  const getCellStyles = () => {
    const baseStyles = "min-h-[100px] p-3 rounded-lg transition-all duration-200 cursor-pointer border-2 flex flex-col items-center justify-center text-sm font-medium"

    switch (cellState) {
      case "past":
        return `${baseStyles} bg-slate-100 dark:bg-slate-800/50 border-slate-200 dark:border-slate-700/50 text-slate-400 dark:text-slate-500 cursor-not-allowed`
      case "not-registered":
        return `${baseStyles} bg-white dark:bg-slate-800 border-slate-200 dark:border-slate-700 text-slate-500 dark:text-slate-400 hover:border-slate-300 dark:hover:border-slate-600`
      case "available":
        if (isSelected) {
          return `${baseStyles} bg-emerald-100 dark:bg-emerald-800/50 border-emerald-400 dark:border-emerald-600 text-emerald-700 dark:text-emerald-300`
        }
        return `${baseStyles} bg-emerald-50 dark:bg-emerald-900/30 border-emerald-200 dark:border-emerald-800/50 text-emerald-600 dark:text-emerald-400 hover:border-emerald-300 dark:hover:border-emerald-700 hover:bg-emerald-100 dark:hover:bg-emerald-900/50`
      default:
        return baseStyles
    }
  }

  const handleClick = () => {
    if (isPast) return

    if (coaches.length === 0) {
      // For not registered slots, just toggle selection
      onToggleSelection()
    } else {
      // For available slots, show coach selection
      onSlotClick()
    }
  }

  const renderContent = () => {
    switch (cellState) {
      case "past":
        return (
          <div className="text-center">
            <div className="text-xs">Không thể đăng ký</div>
            <div className="text-xs text-red-500 dark:text-red-600/80 mt-1">Đã qua</div>
          </div>
        )
      case "not-registered":
        return (
          <div className="text-center">
            <div className="text-xs">Chưa đăng ký</div>
            {isSelected && (
              <div className="mt-2">
                <Plus className="w-4 h-4 mx-auto text-emerald-600 dark:text-emerald-400" />
              </div>
            )}
          </div>
        )
      case "available":
        if (coaches.length === 1) {
          return (
            <div className="text-center space-y-2">
              <div className="w-8 h-8 rounded-full bg-emerald-200 dark:bg-emerald-800/60 flex items-center justify-center mx-auto">
                <User className="w-4 h-4 text-emerald-700 dark:text-emerald-300" />
              </div>
              <div>
                <div className="text-xs font-medium">Có sẵn</div>
                <div className="text-xs">Tạo lịch hẹn</div>
              </div>
              {isSelected && <div className="text-xs text-emerald-600 dark:text-emerald-400">✓ Đã chọn</div>}
            </div>
          )
        } else {
          return (
            <div className="text-center space-y-2">
              <div className="flex -space-x-1 justify-center">
                {coaches.slice(0, 3).map((coach, index) => (
                  <div
                    key={coach.coachId}
                    className="w-6 h-6 rounded-full bg-emerald-200 dark:bg-emerald-800/60 border border-white dark:border-slate-700 flex items-center justify-center"
                    style={{ zIndex: 3 - index }}
                  >
                    <User className="w-3 h-3 text-emerald-700 dark:text-emerald-300" />
                  </div>
                ))}
                {coaches.length > 3 && (
                  <div className="w-6 h-6 rounded-full bg-emerald-300 dark:bg-emerald-700/70 border border-white dark:border-slate-700 flex items-center justify-center">
                    <span className="text-xs font-medium text-emerald-800 dark:text-emerald-200">+{coaches.length - 3}</span>
                  </div>
                )}
              </div>
              <div>
                <div className="text-xs font-medium">Có sẵn</div>
                <div className="text-xs">{coaches.length} coach</div>
              </div>
              {isSelected && <div className="text-xs text-emerald-600 dark:text-emerald-400">✓ Đã chọn</div>}
            </div>
          )
        }
      default:
        return null
    }
  }

  return (
    <div className={getCellStyles()} onClick={handleClick}>
      {renderContent()}
    </div>
  )
}
