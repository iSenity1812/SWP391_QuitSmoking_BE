"use client"

import { useState, useEffect } from "react"
import type { Plan, StreakData, StreakEntry, StreakAchievement } from "@/pages/plan/styles/ui/types/plan"

// Default achievements
const DEFAULT_ACHIEVEMENTS: StreakAchievement[] = [
    {
        id: "streak-1",
        title: "Ngày Đầu Tiên",
        description: "Hoàn thành ngày đầu tiên không hút thuốc",
        icon: "star",
        daysRequired: 1,
        isUnlocked: false,
    },
    {
        id: "streak-3",
        title: "Ba Ngày Đầu",
        description: "Vượt qua 3 ngày đầu tiên khó khăn nhất",
        icon: "trophy",
        daysRequired: 3,
        isUnlocked: false,
    },
    {
        id: "streak-7",
        title: "Một Tuần",
        description: "Hoàn thành một tuần không hút thuốc",
        icon: "award",
        daysRequired: 7,
        isUnlocked: false,
    },
    {
        id: "streak-14",
        title: "Hai Tuần",
        description: "Hai tuần liên tiếp không hút thuốc",
        icon: "medal",
        daysRequired: 14,
        isUnlocked: false,
    },
    {
        id: "streak-30",
        title: "Một Tháng",
        description: "Một tháng không hút thuốc - thói quen mới đang hình thành!",
        icon: "crown",
        daysRequired: 30,
        isUnlocked: false,
    },
    {
        id: "streak-90",
        title: "Ba Tháng",
        description: "Ba tháng không hút thuốc - bạn đã thay đổi lối sống!",
        icon: "diamond",
        daysRequired: 90,
        isUnlocked: false,
    },
    {
        id: "streak-365",
        title: "Một Năm",
        description: "Một năm không hút thuốc - bạn đã chiến thắng!",
        icon: "flame",
        daysRequired: 365,
        isUnlocked: false,
    },
]

// Helper to get today's date with time set to midnight
const getTodayDate = () => {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return today
}

// Helper to calculate days between two dates
const getDaysBetween = (startDate: Date, endDate: Date) => {
    const start = new Date(startDate)
    const end = new Date(endDate)
    start.setHours(0, 0, 0, 0)
    end.setHours(0, 0, 0, 0)

    const diffTime = end.getTime() - start.getTime()
    return Math.floor(diffTime / (1000 * 60 * 60 * 24))
}

// Helper to generate automatic streak entries
const generateStreakEntries = (startDate: Date, currentStreak: number): StreakEntry[] => {
    const entries: StreakEntry[] = []
    const start = new Date(startDate)
    start.setHours(0, 0, 0, 0)

    for (let i = 0; i < currentStreak; i++) {
        const entryDate = new Date(start)
        entryDate.setDate(entryDate.getDate() + i)

        entries.push({
            date: entryDate,
            status: "success",
            notes: "Tự động tính toán dựa trên ngày bắt đầu kế hoạch",
        })
    }

    return entries
}

export const useStreakTracking = (plan: Plan | null) => {
    const [streakData, setStreakData] = useState<StreakData | null>(null)

    // Calculate automatic streak based on plan start date
    const calculateAutomaticStreak = (plan: Plan) => {
        const today = getTodayDate()
        const startDate = new Date(plan.startDate)
        startDate.setHours(0, 0, 0, 0)

        // If plan hasn't started yet
        if (startDate > today) {
            return {
                currentStreak: 0,
                hasStarted: false,
            }
        }

        // Calculate days since plan started
        const daysSinceStart = getDaysBetween(startDate, today)

        // For automatic tracking, current streak equals days since start + 1 (including today)
        const currentStreak = daysSinceStart + 1

        return {
            currentStreak: Math.max(0, currentStreak),
            hasStarted: true,
        }
    }

    // Initialize and update streak data automatically
    useEffect(() => {
        if (plan) {
            const { currentStreak, hasStarted } = calculateAutomaticStreak(plan)

            // Try to load existing streak data for achievements
            const savedStreakData = localStorage.getItem("streakData")
            let existingAchievements = DEFAULT_ACHIEVEMENTS

            if (savedStreakData) {
                try {
                    const parsedData = JSON.parse(savedStreakData)
                    if (parsedData.achievements) {
                        existingAchievements = parsedData.achievements.map((achievement: {
                            isUnlocked: boolean;
                            unlockedAt?: string;
                            daysRequired: number;
                        }) => ({
                            ...achievement,
                            unlockedAt: achievement.unlockedAt ? new Date(achievement.unlockedAt) : undefined,
                        }))
                    }
                } catch (error) {
                    console.error("Error parsing saved streak data:", error)
                }
            }

            // Check for newly unlocked achievements
            const updatedAchievements = existingAchievements.map((achievement) => {
                if (!achievement.isUnlocked && currentStreak >= achievement.daysRequired) {
                    return {
                        ...achievement,
                        isUnlocked: true,
                        unlockedAt: new Date(),
                    }
                }
                return achievement
            })

            // Generate automatic streak history
            const streakHistory = hasStarted ? generateStreakEntries(new Date(plan.startDate), currentStreak) : []

            const newStreakData: StreakData = {
                currentStreak,
                longestStreak: Math.max(
                    currentStreak,
                    existingAchievements.reduce(
                        (max, achievement) => (achievement.isUnlocked ? Math.max(max, achievement.daysRequired) : max),
                        0,
                    ),
                ),
                totalDays: currentStreak,
                lastCheckIn: hasStarted ? getTodayDate() : null,
                streakHistory,
                achievements: updatedAchievements,
            }

            setStreakData(newStreakData)
        } else {
            setStreakData(null)
        }
    }, [plan])

    // Save streak data to localStorage whenever it changes
    useEffect(() => {
        if (streakData) {
            localStorage.setItem("streakData", JSON.stringify(streakData))
        }
    }, [streakData])

    // Manual check-in (optional - for user interaction)
    const checkIn = (status: "success" | "failed" | "partial", cigarettesSmoked?: number, notes?: string) => {
        if (!streakData || !plan) return

        // For automatic system, we can still allow manual notes/status updates
        const today = getTodayDate()

        // Find today's entry in history and update it
        const updatedHistory = streakData.streakHistory.map((entry) => {
            if (entry.date.getTime() === today.getTime()) {
                return {
                    ...entry,
                    status,
                    cigarettesSmoked,
                    notes: notes || entry.notes,
                }
            }
            return entry
        })

        setStreakData({
            ...streakData,
            streakHistory: updatedHistory,
        })
    }

    // Reset streak (when plan is deleted)
    const resetStreak = () => {
        localStorage.removeItem("streakData")
        setStreakData(null)
    }

    // Get streak status
    const getStreakStatus = () => {
        if (!streakData || !plan) return "inactive"

        const today = getTodayDate()
        const startDate = new Date(plan.startDate)
        startDate.setHours(0, 0, 0, 0)

        if (startDate > today) return "not-started"
        if (streakData.currentStreak > 0) return "active"

        return "active"
    }

    // Check if user can check in today (always true for automatic system)
    const canCheckInToday = () => {
        if (!streakData || !plan) return false

        const today = getTodayDate()
        const startDate = new Date(plan.startDate)
        startDate.setHours(0, 0, 0, 0)

        return startDate <= today
    }

    // Get motivational message based on streak
    const getMotivationalMessage = () => {
        if (!streakData || !plan) return "Bắt đầu hành trình cai thuốc của bạn!"

        const today = getTodayDate()
        const startDate = new Date(plan.startDate)
        startDate.setHours(0, 0, 0, 0)

        if (startDate > today) {
            const daysUntilStart = getDaysBetween(today, startDate)
            return `Kế hoạch của bạn sẽ bắt đầu trong ${daysUntilStart} ngày. Hãy chuẩn bị tinh thần!`
        }

        const streak = streakData.currentStreak

        // Based on streak length
        if (streak === 0) {
            return "Hôm nay là ngày đầu tiên! Bạn có thể làm được!"
        } else if (streak === 1) {
            return "Ngày đầu tiên đã hoàn thành! Bạn đã bắt đầu tuyệt vời!"
        } else if (streak < 3) {
            return "Những ngày đầu rất quan trọng. Bạn đang làm rất tốt!"
        } else if (streak < 7) {
            return "Tuyệt vời! Bạn đang tiến gần đến cột mốc 1 tuần!"
        } else if (streak < 14) {
            return "Một tuần đã qua! Cơ thể bạn đang dần hồi phục!"
        } else if (streak < 30) {
            return "Hai tuần! Bạn đang trên đường tạo thói quen mới!"
        } else if (streak < 90) {
            return "Một tháng không hút thuốc! Bạn thật phi thường!"
        } else if (streak < 365) {
            return "Ba tháng! Bạn đã thay đổi lối sống của mình!"
        } else {
            return "Một năm không hút thuốc! Bạn đã chiến thắng hoàn toàn!"
        }
    }

    // Get days until next milestone
    const getDaysUntilNextMilestone = () => {
        if (!streakData) return null

        const nextMilestone = DEFAULT_ACHIEVEMENTS.find(
            (achievement) => !achievement.isUnlocked && achievement.daysRequired > streakData.currentStreak,
        )

        if (nextMilestone) {
            return nextMilestone.daysRequired - streakData.currentStreak
        }

        return null
    }

    return {
        streakData,
        checkIn,
        resetStreak,
        getStreakStatus,
        canCheckInToday,
        getMotivationalMessage,
        getDaysUntilNextMilestone,
    }
}
