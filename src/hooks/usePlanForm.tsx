"use client"

import type React from "react"

import { useState, useEffect } from "react"
import type { Plan, PlanFormData, ReductionStep } from "@/pages/plan/styles/ui/types/plan"

export const usePlanForm = (setCurrentPlan: (plan: Plan | null) => void) => {
    const [isCreatingPlan, setIsCreatingPlan] = useState(false)
    const [selectedPlanType, setSelectedPlanType] = useState<"gradual" | "cold-turkey" | null>(null)
    const [newPlan, setNewPlan] = useState<PlanFormData>({
        title: "",
        description: "",
        startDate: new Date(),
        targetDate: new Date(new Date().setDate(new Date().getDate() + 30)),
        dailyCigarettes: 20,
        motivation: "",
        cigaretteType: "Marlboro",
        planType: "gradual", // Default plan type
    })

    useEffect(() => {
        if (selectedPlanType) {
            setNewPlan((prev) => ({ ...prev, planType: selectedPlanType }))
        }
    }, [selectedPlanType])

    const resetForm = () => {
        setNewPlan({
            title: "",
            description: "",
            startDate: new Date(),
            targetDate: new Date(new Date().setDate(new Date().getDate() + 30)),
            dailyCigarettes: 20,
            motivation: "",
            cigaretteType: "Marlboro",
            planType: "gradual", // Default plan type
        })
        setSelectedPlanType(null)
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target
        setNewPlan((prev) => ({ ...prev, [name]: value }))
    }

    const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target
        setNewPlan((prev) => ({ ...prev, [name]: value }))
    }

    const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setNewPlan((prev) => ({ ...prev, [name]: new Date(value) }))
    }

    const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setNewPlan((prev) => ({ ...prev, [name]: Number.parseInt(value) || 0 }))
    }

    // Generate reduction schedule for gradual plans
    const generateReductionSchedule = (dailyCigarettes: number): ReductionStep[] => {
        const schedule: ReductionStep[] = []
        for (let i = 0; i <= dailyCigarettes; i++) {
            const cigarettesPerDay = dailyCigarettes - i
            schedule.push({
                week: i + 1, // This will represent day number for daily reduction
                cigarettesPerDay,
                description: cigarettesPerDay === 0 ? "Hoàn thành! Không hút thuốc." : `${cigarettesPerDay} điếu mỗi ngày`,
            })
        }
        return schedule
    }

    const handleCreatePlan = () => {
        if (!newPlan.title || !newPlan.description || !newPlan.motivation) {
            alert("Vui lòng điền đầy đủ thông tin bắt buộc!")
            return
        }

        if (!newPlan.planType) {
            alert("Vui lòng chọn phương pháp cai thuốc!")
            return
        }

        // Create plan with the selected type
        const plan: Plan = {
            id: Date.now(),
            ...newPlan,
            // Only generate reduction schedule for gradual plans
            ...(newPlan.planType === "gradual" && { reductionSchedule: generateReductionSchedule(newPlan.dailyCigarettes) }),
        }

        setCurrentPlan(plan)
        setIsCreatingPlan(false)
        resetForm()
    }

    const handleEditPlan = (plan: Plan) => {
        setNewPlan({
            title: plan.title,
            description: plan.description,
            startDate: new Date(plan.startDate),
            targetDate: new Date(plan.targetDate),
            dailyCigarettes: plan.dailyCigarettes,
            motivation: plan.motivation,
            cigaretteType: plan.cigaretteType,
            planType: plan.planType,
        })
        setSelectedPlanType(plan.planType)
        setIsCreatingPlan(true)
    }

    return {
        isCreatingPlan,
        setIsCreatingPlan,
        newPlan,
        resetForm,
        handleCreatePlan,
        handleEditPlan,
        handleInputChange,
        handleSelectChange,
        handleDateChange,
        handleNumberChange,
        selectedPlanType,
        setSelectedPlanType,
    }
}
