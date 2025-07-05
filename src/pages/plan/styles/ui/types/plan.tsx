export interface Plan {
    id: number
    title: string
    description: string
    startDate: Date
    targetDate: Date
    dailyCigarettes: number
    motivation: string
    cigaretteType: string
    planType: "gradual" | "cold-turkey" // Added planType field
    reductionSchedule?: ReductionStep[] // For gradual plans
}

export interface PlanFormData {
    title: string
    description: string
    startDate: Date
    targetDate: Date
    dailyCigarettes: number
    motivation: string
    cigaretteType: string
    planType: "gradual" | "cold-turkey" // Added planType field
}

export interface ReductionStep {
    week: number // Will represent day number for daily reduction
    cigarettesPerDay: number
    description: string
}

export interface UserSubscription {
    type: "free" | "premium"
    duration?: string
    expiryDate?: Date
}

export interface PlanCalculations {
    days: number
    saved: number
    progress: number
}

// Streak interfaces
export interface StreakData {
    currentStreak: number
    longestStreak: number
    totalDays: number
    lastCheckIn: Date | null
    streakHistory: StreakEntry[]
    achievements: StreakAchievement[]
}

export interface StreakEntry {
    date: Date
    status: "success" | "failed" | "partial"
    cigarettesSmoked?: number
    notes?: string
}

export interface StreakAchievement {
    id: string
    title: string
    description: string
    icon: string
    daysRequired: number
    unlockedAt?: Date
    isUnlocked: boolean
}
