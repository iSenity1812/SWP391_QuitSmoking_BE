"use client"

import { useState, useEffect } from "react"
import type { Plan, UserSubscription } from "@/pages/plan/styles/ui/types/plan"
import { STORAGE_KEYS } from "@/pages/plan/styles/ui/types/cigarette"

export const usePlanStorage = () => {
    const [currentPlan, setCurrentPlan] = useState<Plan | null>(null)
    const [userSubscription, setUserSubscription] = useState<UserSubscription>({ type: "free" })

    // Load from localStorage
    useEffect(() => {
        // Try all possible storage keys
        const possibleKeys = [STORAGE_KEYS.PLAN, "plan", "currentPlan", "PLAN"]
        let savedPlan = null

        for (const key of possibleKeys) {
            const planData = localStorage.getItem(key)
            if (planData) {
                savedPlan = planData
                break
            }
        }

        if (savedPlan) {
            try {
                const parsedPlan = JSON.parse(savedPlan)

                // Convert date strings back to Date objects
                if (parsedPlan.startDate) {
                    parsedPlan.startDate = new Date(parsedPlan.startDate)
                }
                if (parsedPlan.targetDate) {
                    parsedPlan.targetDate = new Date(parsedPlan.targetDate)
                }

                // Ensure all required fields exist
                if (parsedPlan.id && parsedPlan.title && parsedPlan.startDate && parsedPlan.targetDate) {
                    setCurrentPlan(parsedPlan)
                }
            } catch (error) {
                console.error("Error loading plan:", error)
                // Clear corrupted data
                possibleKeys.forEach((key) => localStorage.removeItem(key))
            }
        }

        const savedSubscription = localStorage.getItem(STORAGE_KEYS.SUBSCRIPTION)
        if (savedSubscription) {
            try {
                const parsedSubscription = JSON.parse(savedSubscription)
                if (parsedSubscription.expiryDate) {
                    parsedSubscription.expiryDate = new Date(parsedSubscription.expiryDate)
                }
                setUserSubscription(parsedSubscription)
            } catch (error) {
                console.error("Error loading subscription:", error)
                localStorage.removeItem(STORAGE_KEYS.SUBSCRIPTION)
            }
        }
    }, [])

    // Save to localStorage
    useEffect(() => {
        if (currentPlan) {
            // Save to multiple keys to ensure compatibility
            localStorage.setItem(STORAGE_KEYS.PLAN, JSON.stringify(currentPlan))
            localStorage.setItem("plan", JSON.stringify(currentPlan))
            localStorage.setItem("currentPlan", JSON.stringify(currentPlan))
        } else {
            // Clear all plan keys
            localStorage.removeItem(STORAGE_KEYS.PLAN)
            localStorage.removeItem("plan")
            localStorage.removeItem("currentPlan")
        }
    }, [currentPlan])

    useEffect(() => {
        localStorage.setItem(STORAGE_KEYS.SUBSCRIPTION, JSON.stringify(userSubscription))
    }, [userSubscription])

    return {
        currentPlan,
        setCurrentPlan,
        userSubscription,
        setUserSubscription,
    }
}
