"use client"

import type React from "react"
import { useNavigate } from "react-router-dom"
import { PlanSelectionSlide } from "../../onBoarding/slides/PlanSelectionSlide"
import type { Plan } from "../../onBoarding/onBoardingFlow"

export const PlanSelectionDirectPage: React.FC = () => {
    const navigate = useNavigate()

    const handlePlanSelected = (plan: Plan) => {
        console.log("Plan selected:", plan) // Debug log

        // Tạo plan object với format chính xác như PlanPage expects
        const newPlan = {
            id: `plan_${Date.now()}`,
            title: plan.title,
            description: plan.description,
            startDate: new Date(),
            targetDate: new Date(Date.now() + getDaysFromPlan(plan) * 24 * 60 * 60 * 1000),
            dailyCigarettes: 10,
            motivation: `Tôi muốn bỏ hút thuốc trong ${plan.duration} để có cuộc sống khỏe mạnh hơn`,
            cigaretteType: "Thuốc lá thông thường",
            cigarettePrice: 25000, // Add price for calculations
            duration: getDaysFromPlan(plan),
        }

        console.log("Created plan:", newPlan) // Debug log

        // Lưu vào localStorage với key đúng
        localStorage.setItem("plan", JSON.stringify(newPlan))
        localStorage.setItem("selectedPlan", JSON.stringify(plan))
        localStorage.setItem("onboarding_completed", "true")

        console.log("Saved to localStorage") // Debug log

        // Chuyển hướng đến trang kế hoạch
        navigate("/plan")
    }

    const getDaysFromPlan = (plan: Plan): number => {
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
