"use client"

import type React from "react"
import { useState } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import type { StreakData } from "@/pages/plan/styles/ui/types/plan"

interface StreakCalendarProps {
    streakData: StreakData
}

export const StreakCalendar: React.FC<StreakCalendarProps> = ({ streakData }) => {
    const [currentDate, setCurrentDate] = useState(new Date())

    // Get current month and year
    const currentMonth = currentDate.getMonth()
    const currentYear = currentDate.getFullYear()

    // Get days in month
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate()

    // Get first day of month (0 = Sunday, 1 = Monday, etc.)
    const firstDayOfMonth = new Date(currentYear, currentMonth, 1).getDay()

    // Create array of days
    const days = Array.from({ length: daysInMonth }, (_, i) => i + 1)

    // Create array of blank days at start of month
    const blankDays = Array.from({ length: firstDayOfMonth }, (_, i) => i)

    // Navigate to previous month
    const prevMonth = () => {
        setCurrentDate(new Date(currentYear, currentMonth - 1, 1))
    }

    // Navigate to next month
    const nextMonth = () => {
        setCurrentDate(new Date(currentYear, currentMonth + 1, 1))
    }

    // Format date to YYYY-MM-DD for comparison
    const formatDate = (date: Date) => {
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(
            2,
            "0",
        )}`
    }

    // Get streak entry for a specific day
    const getStreakEntryForDay = (day: number) => {
        const date = new Date(currentYear, currentMonth, day)
        return streakData.streakHistory.find((entry) => {
            const entryDate = new Date(entry.date)
            return formatDate(entryDate) === formatDate(date)
        })
    }

    // Get class for day based on streak status
    const getDayClass = (day: number) => {
        const entry = getStreakEntryForDay(day)
        const today = new Date()
        const isToday = today.getDate() === day && today.getMonth() === currentMonth && today.getFullYear() === currentYear

        let baseClass = "w-10 h-10 rounded-full flex items-center justify-center text-sm transition-all duration-200"

        if (isToday) {
            baseClass += " ring-2 ring-offset-2 ring-blue-500 dark:ring-blue-400"
        }

        if (!entry) {
            const date = new Date(currentYear, currentMonth, day)
            const isPast = date < new Date(new Date().setHours(0, 0, 0, 0))
            const isFuture = date > new Date(new Date().setHours(23, 59, 59, 999))

            if (isPast) {
                return `${baseClass} bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-300 hover:bg-red-200 dark:hover:bg-red-800/50`
            }
            if (isFuture) {
                return `${baseClass} bg-slate-100 dark:bg-slate-800 text-slate-400 dark:text-slate-500`
            }
            return `${baseClass} bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700`
        }

        switch (entry.status) {
            case "success":
                return `${baseClass} bg-emerald-100 dark:bg-emerald-900/30 text-emerald-800 dark:text-emerald-300 hover:bg-emerald-200 dark:hover:bg-emerald-800/50`
            case "partial":
                return `${baseClass} bg-amber-100 dark:bg-amber-900/30 text-amber-800 dark:text-amber-300 hover:bg-amber-200 dark:hover:bg-amber-800/50`
            case "failed":
                return `${baseClass} bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-300 hover:bg-red-200 dark:hover:bg-red-800/50`
            default:
                return `${baseClass} bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700`
        }
    }

    // Month names
    const monthNames = [
        "Tháng 1",
        "Tháng 2",
        "Tháng 3",
        "Tháng 4",
        "Tháng 5",
        "Tháng 6",
        "Tháng 7",
        "Tháng 8",
        "Tháng 9",
        "Tháng 10",
        "Tháng 11",
        "Tháng 12",
    ]

    // Day names
    const dayNames = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"]

    return (
        <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-blue-100 dark:border-slate-700 shadow-xl rounded-lg p-6">
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-semibold text-slate-900 dark:text-white">Lịch Streak</h3>
                <div className="flex items-center gap-2">
                    <Button variant="outline" size="icon" onClick={prevMonth} className="h-8 w-8">
                        <ChevronLeft className="h-4 w-4" />
                    </Button>
                    <span className="text-slate-700 dark:text-slate-300 font-medium">
                        {monthNames[currentMonth]} {currentYear}
                    </span>
                    <Button variant="outline" size="icon" onClick={nextMonth} className="h-8 w-8">
                        <ChevronRight className="h-4 w-4" />
                    </Button>
                </div>
            </div>

            <div className="mb-4">
                <div className="grid grid-cols-7 gap-1 mb-2">
                    {dayNames.map((day) => (
                        <div key={day} className="text-center text-xs font-medium text-slate-500 dark:text-slate-400">
                            {day}
                        </div>
                    ))}
                </div>
                <div className="grid grid-cols-7 gap-1">
                    {blankDays.map((blankDay) => (
                        <div key={`blank-${blankDay}`} className="w-10 h-10"></div>
                    ))}
                    {days.map((day) => (
                        <div key={day} className="flex items-center justify-center">
                            <button className={getDayClass(day)}>{day}</button>
                        </div>
                    ))}
                </div>
            </div>

            <div className="flex flex-wrap gap-4 justify-center mt-6">
                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded-full bg-emerald-100 dark:bg-emerald-900/30"></div>
                    <span className="text-xs text-slate-700 dark:text-slate-300">Thành công</span>
                </div>
                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded-full bg-amber-100 dark:bg-amber-900/30"></div>
                    <span className="text-xs text-slate-700 dark:text-slate-300">Một phần</span>
                </div>
                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded-full bg-red-100 dark:bg-red-900/30"></div>
                    <span className="text-xs text-slate-700 dark:text-slate-300">Thất bại</span>
                </div>
                <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded-full bg-slate-100 dark:bg-slate-800"></div>
                    <span className="text-xs text-slate-700 dark:text-slate-300">Chưa check-in</span>
                </div>
            </div>
        </div>
    )
}
