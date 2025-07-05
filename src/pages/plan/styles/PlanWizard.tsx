"use client"

import * as React from "react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"

interface CalendarProps {
    mode?: "single" | "multiple" | "range"
    selected?: Date
    onSelect?: (date: Date | undefined) => void
    disabled?: (date: Date) => boolean
    className?: string
}

export function Calendar({ selected, onSelect, disabled, className }: CalendarProps) {
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

        // Check if date is disabled
        if (disabled && disabled(newDate)) {
            return
        }

        setSelectedDate(newDate)
        onSelect?.(newDate)
    }

    const isDateDisabled = (date: Date) => {
        return disabled ? disabled(date) : false
    }

    const weekDays = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"]

    return (
        <div className={cn("p-3", className)}>
            <div className="flex items-center justify-between mb-4">
                <Button variant="outline" size="sm" onClick={handlePrevMonth}>
                    &lt;
                </Button>
                <span className="font-medium">{currentDate.toLocaleString("vi-VN", { month: "long", year: "numeric" })}</span>
                <Button variant="outline" size="sm" onClick={handleNextMonth}>
                    &gt;
                </Button>
            </div>
            <div className="grid grid-cols-7 gap-1 text-center">
                {weekDays.map((day) => (
                    <div key={day} className="text-sm font-medium text-muted-foreground">
                        {day}
                    </div>
                ))}
                {Array.from({ length: firstDayOfMonth }).map((_, index) => (
                    <div key={`empty-${index}`} />
                ))}
                {Array.from({ length: daysInMonth }).map((_, index) => {
                    const day = index + 1
                    const date = new Date(currentDate.getFullYear(), currentDate.getMonth(), day)
                    const isSelected = selectedDate?.toDateString() === date.toDateString()
                    const isToday = new Date().toDateString() === date.toDateString()
                    const isDisabled = isDateDisabled(date)

                    return (
                        <Button
                            key={day}
                            variant={isSelected ? "default" : "ghost"}
                            size="sm"
                            disabled={isDisabled}
                            className={cn(
                                "h-9 w-9 p-0 font-normal",
                                isToday && !isSelected && "bg-accent text-accent-foreground",
                                isDisabled && "opacity-50 cursor-not-allowed",
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