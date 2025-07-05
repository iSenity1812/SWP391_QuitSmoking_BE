"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Target, Plus } from "lucide-react"

interface EmptyPlanStateProps {
    isSubscriptionActive: boolean
    onCreatePlan: () => void
}

export const EmptyPlanState: React.FC<EmptyPlanStateProps> = ({ isSubscriptionActive, onCreatePlan }) => {
    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="flex flex-col items-center justify-center min-h-[60vh] space-y-8"
        >
            <div className="text-center space-y-6">
                <div className="w-24 h-24 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center mx-auto shadow-xl">
                    <Target className="w-12 h-12 text-white" />
                </div>
                <h1 className="text-4xl lg:text-5xl font-black text-slate-800 dark:text-white">Bắt Đầu Hành Trình</h1>
                <p className="text-xl text-slate-600 dark:text-slate-300 max-w-2xl mx-auto leading-relaxed">
                    Tạo kế hoạch cai thuốc lá cá nhân hóa và thực hiện bước đầu tiên hướng tới cuộc sống khỏe mạnh, không khói
                    thuốc
                </p>
                {isSubscriptionActive && (
                    <div className="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-lg border border-emerald-200 dark:border-emerald-700">
                        <p className="text-emerald-700 dark:text-emerald-300 font-medium">
                            🎉 Bạn đang sử dụng gói Premium! Tận hưởng các tính năng cao cấp khi tạo kế hoạch.
                        </p>
                    </div>
                )}
            </div>

            <button
                onClick={onCreatePlan}
                className="px-10 py-5 rounded-xl font-bold text-lg text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 hover:scale-105 transition-all duration-300 hover:-translate-y-1 shadow-xl shadow-emerald-200/50 dark:shadow-emerald-500/25 flex items-center gap-2"
            >
                <Plus className="w-6 h-6" />
                Tạo Kế Hoạch Của Bạn
            </button>
        </motion.div>
    )
}
