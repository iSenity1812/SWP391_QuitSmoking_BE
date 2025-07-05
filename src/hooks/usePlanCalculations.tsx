"use client"

import { useMemo } from "react"

interface Plan {
    id: number
    title: string
    description: string
    startDate: Date
    targetDate: Date
    dailyCigarettes: number
    motivation: string
    cigaretteType: string
}

const cigarettePrices: { [key: string]: number } = {
    "Vinataba (mềm)": 25000,
    "Vinataba (đóng gói cứng)": 30000,
    "Thăng Long": 23000,
    "Marlboro (đỏ/trắng)": 35000,
    "555": 40000,
    Esse: 30000,
    "Black Stone": 50000,
    "Captain Black": 60000,
    Khác: 30000,
}

export const usePlanCalculations = (plan: Plan | null) => {
    return useMemo(() => {
        console.log("=== CALCULATING SAVINGS ===")
        console.log("Plan:", plan)

        if (!plan) {
            console.log("No plan found")
            return { days: 0, saved: 0, progress: 0 }
        }

        // Get current date and plan dates
        const now = new Date()
        const startDate = new Date(plan.startDate)
        const targetDate = new Date(plan.targetDate)

        console.log("Current date:", now.toISOString())
        console.log("Start date:", startDate.toISOString())
        console.log("Target date:", targetDate.toISOString())

        // Calculate days since start (including today if started today)
        const msPerDay = 24 * 60 * 60 * 1000
        const daysSinceStart = Math.floor((now.getTime() - startDate.getTime()) / msPerDay)
        const daysSmokeFree = Math.max(0, daysSinceStart + 1) // +1 to include the start day

        console.log("Days since start:", daysSinceStart)
        console.log("Days smoke free:", daysSmokeFree)

        // Calculate total plan duration
        const totalPlanDays = Math.max(1, Math.floor((targetDate.getTime() - startDate.getTime()) / msPerDay))
        const progress = Math.min(100, (daysSmokeFree / totalPlanDays) * 100)

        console.log("Total plan days:", totalPlanDays)
        console.log("Progress:", progress)

        // Get cigarette data
        const dailyCigarettes = Number(plan.dailyCigarettes) || 0
        const cigaretteType = plan.cigaretteType || "Khác"
        const pricePerPack = cigarettePrices[cigaretteType] || cigarettePrices["Khác"]

        console.log("Daily cigarettes:", dailyCigarettes)
        console.log("Cigarette type:", cigaretteType)
        console.log("Price per pack:", pricePerPack)

        // Calculate savings
        const costPerCigarette = pricePerPack / 20 // 20 cigarettes per pack
        const dailyCost = costPerCigarette * dailyCigarettes
        const totalSaved = dailyCost * daysSmokeFree

        console.log("Cost per cigarette:", costPerCigarette)
        console.log("Daily cost:", dailyCost)
        console.log("Total saved:", totalSaved)

        const result = {
            days: daysSmokeFree,
            saved: Math.round(totalSaved),
            progress: Math.round(progress * 10) / 10,
        }

        console.log("Final result:", result)
        console.log("=== END CALCULATION ===")

        return result
    }, [plan])
}
