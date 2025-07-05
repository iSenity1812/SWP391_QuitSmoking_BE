"use client"

import type React from "react"
import { useState } from "react"
import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { ChevronLeft, ChevronRight, Zap, Dumbbell, Sparkles, Rocket, Crown, Wind } from 'lucide-react'
import type { Plan } from "../onBoardingFlow"

interface PlanSelectionSlideProps {
    onPlanSelected: (plan: Plan) => void
    onBack: () => void
}

export const PlanSelectionSlide: React.FC<PlanSelectionSlideProps> = ({ onPlanSelected, onBack }) => {
    const [selectedPlan, setSelectedPlan] = useState<Plan | null>(null)

    const plans: Plan[] = [
        {
            id: "quick-sprint",
            title: "14 NGÀY",
            subtitle: "NHANH CHÓNG",
            duration: "2 tuần",
            description: "Hoàn hảo cho người hút thuốc nhẹ sẵn sàng thay đổi nhanh",
        },

        {
            id: "full-reset",
            title: "30 NGÀY",
            subtitle: "KHỞI ĐỘNG LẠI",
            duration: "1 tháng",
            description: "Phổ biến nhất. Một tháng để thay đổi cuộc sống",
        },

        {
            id: "complete-change",
            title: "3 THÁNG",
            subtitle: "THAY ĐỔI HOÀN TOÀN",
            duration: "12 tuần",
            description: "Hỗ trợ tối đa cho thành công lâu dài",
        },
    ]

    const handlePlanSelect = (plan: Plan) => {
        setSelectedPlan(plan)
    }

    const handleContinue = () => {
        if (selectedPlan) {
            onPlanSelected(selectedPlan)
        }
    }

    const getGradient = (index: number) => {
        const gradients = [
            "from-yellow-400 to-orange-500",
            "from-purple-400 to-pink-500",
            "from-emerald-400 to-blue-500",
        ]
        return gradients[index % gradients.length]
    }

    const getIcon = (index: number) => {
        const icons = [Zap, Dumbbell, Sparkles, Rocket, Crown]
        const Icon = icons[index % icons.length]
        return <Icon className="w-6 h-6 text-white" />
    }

    return (
        <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -50 }}
            transition={{ duration: 0.5 }}
            className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 relative overflow-hidden"
        >
            {/* Background decorative elements */}
            <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-emerald-300/20 dark:from-emerald-500/10 dark:to-emerald-600/5 rounded-full blur-3xl animate-pulse"></div>
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-emerald-100/40 to-emerald-200/30 dark:from-emerald-600/10 dark:to-emerald-500/5 rounded-full blur-3xl animate-pulse delay-1000"></div>

            <div className="relative z-10 p-4">
                <div className="max-w-md mx-auto">
                    {/* Header */}
                    <div className="text-center mb-8 pt-8">
                        <div className="flex items-center justify-center gap-3 text-2xl font-black text-slate-800 dark:text-white mb-4">
                            <Wind className="h-8 w-8 text-emerald-500" />
                            <span className="text-2xl font-bold">QuitTogether</span>
                        </div>
                        <h1 className="text-2xl font-bold text-slate-800 dark:text-white mb-4">
                            Hành trình của mỗi người đều khác nhau. Chọn kế hoạch phù hợp với bạn.
                        </h1>
                    </div>

                    {/* Plans */}
                    <div className="space-y-4 mb-8">
                        {plans.map((plan, index) => (
                            <motion.div
                                key={plan.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: index * 0.1 }}
                            >
                                <Card
                                    className={`cursor-pointer transition-all duration-300 hover:scale-105 bg-white/95 dark:bg-slate-800/95 backdrop-blur-xl border shadow-lg shadow-emerald-100/50 dark:shadow-slate-900/50 ${selectedPlan?.id === plan.id
                                        ? "ring-2 ring-emerald-500 bg-emerald-50 dark:bg-emerald-900/20 border-emerald-200 dark:border-emerald-700"
                                        : "border-emerald-200 dark:border-slate-700 hover:shadow-xl"
                                        }`}
                                    onClick={() => handlePlanSelect(plan)}
                                >
                                    <CardContent className="p-6">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-4">
                                                <div
                                                    className={`w-12 h-12 rounded-xl bg-gradient-to-br ${getGradient(index)} flex items-center justify-center shadow-lg`}
                                                >
                                                    {getIcon(index)}
                                                </div>
                                                <div className="flex-1">
                                                    <div className="flex items-center gap-2 mb-1">
                                                        <h3 className="font-bold text-slate-800 dark:text-white">
                                                            {plan.title} - {plan.subtitle}
                                                        </h3>
                                                        {index === 1 && (
                                                            <span className="bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300 text-xs px-2 py-1 rounded-full font-medium">
                                                                Phổ biến
                                                            </span>
                                                        )}
                                                    </div>
                                                    <p className="text-sm text-slate-600 dark:text-slate-300">{plan.description}</p>
                                                </div>
                                            </div>
                                            <ChevronRight className="w-5 h-5 text-slate-400" />
                                        </div>
                                    </CardContent>
                                </Card>
                            </motion.div>
                        ))}
                    </div>

                    {/* Action Buttons */}
                    <div className="space-y-4">
                        <Button
                            onClick={handleContinue}
                            disabled={!selectedPlan}
                            className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 text-white font-bold py-4 text-lg rounded-xl disabled:opacity-50 hover:scale-105 transition-all duration-300 hover:-translate-y-0.5 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25"
                        >
                            Tiếp Tục Với Kế Hoạch Đã Chọn
                        </Button>

                        <Button
                            variant="ghost"
                            onClick={onBack}
                            className="w-full flex items-center justify-center gap-2 text-slate-600 dark:text-slate-300 hover:text-emerald-600 dark:hover:text-emerald-400"
                        >
                            <ChevronLeft className="w-4 h-4" />
                            Quay Lại
                        </Button>
                    </div>
                </div>
            </div>
        </motion.div>
    )
}
