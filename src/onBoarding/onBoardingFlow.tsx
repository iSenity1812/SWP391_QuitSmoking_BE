"use client"

import type React from "react"
import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { AnimatePresence } from "framer-motion"
import { IntroSlide } from "./slides/IntroSlide"
import { AuthChoiceSlide } from "./slides/AuthChoiceSlide"
import { LoginSlide } from "./slides/LoginSlide"
import { RegisterSlide } from "./slides/RegisterSlide"
import { MotivationSlide } from "./slides/MotivationSlide"
import { PlanSelectionSlide } from "./slides/PlanSelectionSlide"

// Định nghĩa các bước trong quy trình onboarding
export type OnboardingStep = "intro" | "auth-choice" | "login" | "register" | "motivation" | "plan-selection"

// Định nghĩa kiểu dữ liệu cho người dùng
export interface User {
    name: string
    email: string
}

// Định nghĩa kiểu dữ liệu cho kế hoạch
export interface Plan {
    id: string
    title: string
    subtitle: string
    duration: string
    description: string
}

export const OnboardingFlow: React.FC = () => {
    const navigate = useNavigate()
    const [currentStep, setCurrentStep] = useState<OnboardingStep>("intro")
    const [user, setUser] = useState<User | null>(null)

    // Xử lý hoàn thành phần giới thiệu
    const handleIntroComplete = () => {
        setCurrentStep("auth-choice")
    }

    // Xử lý lựa chọn phương thức xác thực
    const handleAuthChoice = (choice: "login" | "register") => {
        setCurrentStep(choice)
    }

    // Xử lý quay lại màn hình lựa chọn xác thực
    const handleBackToAuthChoice = () => {
        setCurrentStep("auth-choice")
    }

    // Xử lý đăng nhập thành công
    const handleLoginSuccess = (userData: User) => {
        setUser(userData)
        setCurrentStep("motivation")
    }

    // Xử lý đăng ký thành công
    const handleRegisterSuccess = (userData: User) => {
        setUser(userData)
        setCurrentStep("motivation")
    }

    // Xử lý chuyển đổi giữa đăng nhập và đăng ký
    const handleSwitchToLogin = () => {
        setCurrentStep("login")
    }

    const handleSwitchToRegister = () => {
        setCurrentStep("register")
    }

    // Xử lý hoàn thành phần động lực
    const handleMotivationComplete = () => {
        setCurrentStep("plan-selection")
    }

    // Xử lý quay lại màn hình động lực
    const handleBackToMotivation = () => {
        setCurrentStep("motivation")
    }

    // Xử lý chọn kế hoạch
    const handlePlanSelected = (plan: Plan) => {
        // Lưu thông tin người dùng và kế hoạch vào localStorage
        if (user) {
            localStorage.setItem("user", JSON.stringify(user))
        }
        localStorage.setItem("onboarding_completed", "true")

        // Chuyển hướng đến trang kế hoạch
        navigate("/plan")
    }

    // Xử lý quay lại trang chủ
    const handleBackToHome = () => {
        // Set onboarding as completed when skipping to home
        localStorage.setItem("onboarding_completed", "true")
        navigate("/")
    }

    // Xử lý bỏ qua onboarding (cho mục đích phát triển)
    const handleSkipOnboarding = () => {
        localStorage.setItem("onboarding_completed", "true")
        navigate("/")
    }

    // Render bước hiện tại
    const renderCurrentStep = () => {
        switch (currentStep) {
            case "intro":
                return (
                    <div className="relative">
                        <IntroSlide onComplete={handleIntroComplete} />
                        <button
                            onClick={handleSkipOnboarding}
                            className="absolute top-4 right-4 bg-slate-800/50 hover:bg-slate-700 text-white px-3 py-1 rounded-md text-xs backdrop-blur-sm"
                        >
                            Bỏ qua (Dev)
                        </button>
                    </div>
                )

            case "auth-choice":
                return (
                    <AuthChoiceSlide
                        onLoginClick={() => handleAuthChoice("login")}
                        onRegisterClick={() => handleAuthChoice("register")}
                        onBack={handleBackToHome}
                    />
                )

            case "login":
                return (
                    <LoginSlide
                        onSuccess={handleLoginSuccess}
                        onBack={handleBackToAuthChoice}
                        onSwitchToRegister={handleSwitchToRegister}
                    />
                )

            case "register":
                return (
                    <RegisterSlide
                        onSuccess={handleRegisterSuccess}
                        onBack={handleBackToAuthChoice}
                        onSwitchToLogin={handleSwitchToLogin}
                    />
                )

            case "motivation":
                return (
                    <MotivationSlide
                        onContinue={handleMotivationComplete}
                        onBack={handleBackToAuthChoice}
                    />
                )

            case "plan-selection":
                return <PlanSelectionSlide onPlanSelected={handlePlanSelected} onBack={handleBackToMotivation} />

            default:
                return <IntroSlide onComplete={handleIntroComplete} />
        }
    }

    return (
        <AnimatePresence mode="wait">
            <div className="min-h-screen">{renderCurrentStep()}</div>
        </AnimatePresence>
    )
}
