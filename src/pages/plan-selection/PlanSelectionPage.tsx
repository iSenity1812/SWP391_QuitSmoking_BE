"use client"

import type React from "react"
import { useNavigate } from "react-router-dom"
import { PlanSelectionSlide } from "../../onBoarding/slides/PlanSelectionSlide"
import type { Plan } from "../../onBoarding/onBoardingFlow"

export const PlanSelectionDirectPage: React.FC = () => {
    const navigate = useNavigate()

    const handlePlanSelected = (plan: Plan) => {
        const planDays = getDaysFromPlan(plan)
        const startDate = new Date()
        const targetDate = new Date(startDate.getTime() + planDays * 24 * 60 * 60 * 1000)

        // Tạo plan object với format chính xác như PlanPage expects
        const newPlan = {
            id: `plan_${Date.now()}`,
            title: `Kế hoạch ${plan.title}`,
            description: plan.description,
            startDate: startDate,
            targetDate: targetDate,
            dailyCigarettes: 10,
            motivation: `Tôi muốn bỏ hút thuốc trong ${planDays} ngày để có cuộc sống khỏe mạnh hơn`,
            cigaretteType: "Thuốc lá thông thường",
            cigarettePrice: 25000,
            duration: planDays,
        }

        // Clear any existing plans first
        localStorage.removeItem("plan")
        localStorage.removeItem("currentPlan")
        localStorage.removeItem("PLAN")

        // Save the new plan
        localStorage.setItem("plan", JSON.stringify(newPlan))
        localStorage.setItem("currentPlan", JSON.stringify(newPlan))
        localStorage.setItem("selectedPlan", JSON.stringify(plan))
        localStorage.setItem("onboarding_completed", "true")

        // Force a page reload to ensure the plan loads
        window.location.href = "/plan"
    }

    const getDaysFromPlan = (plan: Plan): number => {
        // Map dựa trên title để đảm bảo chính xác
        if (plan.title.includes("14 NGÀY")) return 14
        if (plan.title.includes("21 NGÀY")) return 21
        if (plan.title.includes("30 NGÀY")) return 30
        if (plan.title.includes("2 THÁNG")) return 60
        if (plan.title.includes("3 THÁNG")) return 90

        // Fallback dựa trên ID
        switch (plan.id) {
            case "quick-sprint":
                return 14
            case "habit-breaker":
                return 21
            case "full-reset":
                return 30
            case "steady-journey":
                return 60
            case "complete-change":
                return 90
            default:
                return 30
        }
    }

    const handleBack = () => {
        navigate("/")
    }

    return <PlanSelectionSlide onPlanSelected={handlePlanSelected} onBack={handleBack} />
}
