"use client"

import * as React from "react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from "lucide-react"

interface CalendarProps {
    selected?: Date
    onSelect?: (date: Date) => void
    className?: string
}

export function Calendar({ selected, onSelect, className }: CalendarProps) {
    const [currentDate, setCurrentDate] = React.useState(new Date())
    const [selectedDate, setSelectedDate] = React.useState<Date | undefined>(selected)

    React.useEffect(() => {
        setSelectedDate(selected)
    }, [selected])

    const daysInMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate()

    const firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1).getDay()

    const handlePrevMonth = () => {
        setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1))
    }

    const handleNextMonth = () => {
        setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1))
    }

    const handleDateClick = (day: number) => {
        const newDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), day)
        setSelectedDate(newDate)
        onSelect?.(newDate)
    }

    const weekDays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"]

    return (
        <div
            className={cn(
                "p-4 bg-white dark:bg-slate-800 rounded-lg border-2 border-emerald-100 dark:border-slate-700 shadow-sm",
                className,
            )}
        >
            {/* Header with month/year and navigation */}
            <div className="flex items-center justify-between mb-4">
                <Button
                    variant="outline"
                    size="sm"
                    onClick={handlePrevMonth}
                    className="h-8 w-8 p-0 border-emerald-200 dark:border-slate-600 hover:bg-emerald-50 dark:hover:bg-slate-700"
                >
                    <ChevronLeft className="h-4 w-4 text-slate-700 dark:text-slate-300" />
                </Button>
                <h2 className="font-semibold text-lg text-slate-800 dark:text-white">
                    {currentDate.toLocaleString("vi-VN", { month: "long", year: "numeric" })}
                </h2>
                <Button
                    variant="outline"
                    size="sm"
                    onClick={handleNextMonth}
                    className="h-8 w-8 p-0 border-emerald-200 dark:border-slate-600 hover:bg-emerald-50 dark:hover:bg-slate-700"
                >
                    <ChevronRight className="h-4 w-4 text-slate-700 dark:text-slate-300" />
                </Button>
            </div>

            {/* Calendar grid */}
            <div className="grid grid-cols-7 gap-1">
                {/* Week day headers */}
                {weekDays.map((day) => (
                    <div
                        key={day}
                        className="h-8 flex items-center justify-center text-sm font-medium text-slate-600 dark:text-slate-400 border-b border-emerald-100 dark:border-slate-600 mb-1"
                    >
                        {day}
                    </div>
                ))}

                {/* Empty cells for days before month starts */}
                {Array.from({ length: firstDayOfMonth }).map((_, index) => (
                    <div key={`empty-${index}`} className="h-8" />
                ))}

                {/* Calendar days */}
                {Array.from({ length: daysInMonth }).map((_, index) => {
                    const day = index + 1
                    const date = new Date(currentDate.getFullYear(), currentDate.getMonth(), day)
                    const isSelected = selectedDate?.toDateString() === date.toDateString()
                    const isToday = new Date().toDateString() === date.toDateString()

                    return (
                        <Button
                            key={day}
                            variant="ghost"
                            size="sm"
                            className={cn(
                                "h-8 w-8 p-0 font-normal text-sm transition-all duration-200",
                                // Default state
                                "text-slate-700 dark:text-slate-300 hover:bg-emerald-50 dark:hover:bg-slate-700",
                                // Today's date
                                isToday &&
                                !isSelected &&
                                "bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300 font-semibold",
                                // Selected date
                                isSelected &&
                                "bg-emerald-500 text-white hover:bg-emerald-600 dark:bg-emerald-600 dark:hover:bg-emerald-700 font-semibold shadow-md",
                            )}
                            onClick={() => handleDateClick(day)}
                        >
                            {day}
                        </Button>
                    )
                })}
            </div>
        </div>
    )
}
