"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Clock, Sparkles } from "lucide-react"

interface MilestoneItemProps {
    milestone: {
        days: number
        title: string
        description: string
        icon: React.ComponentType<{ className?: string }>
    }
    smokeFreeDays: number
    index: number
}

export const MilestoneItem: React.FC<MilestoneItemProps> = ({ milestone, smokeFreeDays, index }) => {
    const isAchieved = smokeFreeDays >= milestone.days
    const isNext = smokeFreeDays < milestone.days && index === 0

    return (
        <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.1 }}
            className={`flex items-center gap-4 p-4 rounded-xl transition-all duration-300 ${isAchieved
                ? "bg-emerald-50 dark:bg-emerald-900/20 border-2 border-emerald-200 dark:border-emerald-700"
                : isNext
                    ? "bg-blue-50 dark:bg-blue-900/20 border-2 border-blue-200 dark:border-blue-700"
                    : "bg-gray-50 dark:bg-slate-700/50 border-2 border-gray-200 dark:border-slate-600"
                }`}
        >
            <div
                className={`w-12 h-12 rounded-full flex items-center justify-center ${isAchieved
                    ? "bg-emerald-500 text-white"
                    : isNext
                        ? "bg-blue-500 text-white"
                        : "bg-gray-300 dark:bg-slate-600 text-gray-600 dark:text-slate-400"
                    }`}
            >
                <milestone.icon className="w-6 h-6" />
            </div>
            <div className="flex-1">
                <div className="flex items-center gap-2">
                    <h4 className="font-semibold">{milestone.title}</h4>
                    {isAchieved && <Sparkles className="w-4 h-4 text-emerald-500" />}
                    {isNext && <Clock className="w-4 h-4 text-blue-500" />}
                </div>
                <p className="text-sm text-muted-foreground">{milestone.description}</p>
            </div>
            <div className="text-right">
                <div className="text-sm font-medium">{milestone.days} ngày</div>
                {isNext && (
                    <div className="text-xs text-blue-600 dark:text-blue-400">Còn {milestone.days - smokeFreeDays} ngày</div>
                )}
            </div>
        </motion.div>
    )
}
