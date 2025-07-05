"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Trophy, Star, Award, Medal, Crown, Diamond, Flame } from "lucide-react"
import type { StreakAchievement } from "@/pages/plan/styles/ui/types/plan"

interface StreakAchievementsProps {
    achievements: StreakAchievement[]
}

export const StreakAchievements: React.FC<StreakAchievementsProps> = ({ achievements }) => {
    // Get icon component based on icon name
    const getIcon = (iconName: string) => {
        switch (iconName) {
            case "trophy":
                return <Trophy className="w-5 h-5" />
            case "star":
                return <Star className="w-5 h-5" />
            case "award":
                return <Award className="w-5 h-5" />
            case "medal":
                return <Medal className="w-5 h-5" />
            case "crown":
                return <Crown className="w-5 h-5" />
            case "diamond":
                return <Diamond className="w-5 h-5" />
            case "flame":
                return <Flame className="w-5 h-5" />
            default:
                return <Award className="w-5 h-5" />
        }
    }

    // Sort achievements by days required
    const sortedAchievements = [...achievements].sort((a, b) => a.daysRequired - b.daysRequired)

    // Count unlocked achievements
    const unlockedCount = achievements.filter((a) => a.isUnlocked).length

    return (
        <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-purple-100 dark:border-slate-700 shadow-xl rounded-lg p-6">
            <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-semibold text-slate-900 dark:text-white">Thành Tựu</h3>
                <div className="bg-purple-100 dark:bg-purple-900/30 text-purple-800 dark:text-purple-300 text-xs font-medium px-2.5 py-1 rounded-full">
                    {unlockedCount}/{achievements.length}
                </div>
            </div>

            <div className="space-y-4">
                {sortedAchievements.map((achievement) => (
                    <div
                        key={achievement.id}
                        className={`relative rounded-lg p-4 border ${achievement.isUnlocked
                                ? "bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 border-purple-200 dark:border-purple-800"
                                : "bg-slate-50 dark:bg-slate-800/50 border-slate-200 dark:border-slate-700"
                            }`}
                    >
                        <div className="flex items-start gap-3">
                            <div
                                className={`w-10 h-10 rounded-full flex items-center justify-center ${achievement.isUnlocked
                                        ? "bg-gradient-to-r from-purple-500 to-indigo-600 text-white"
                                        : "bg-slate-200 dark:bg-slate-700 text-slate-500 dark:text-slate-400"
                                    }`}
                            >
                                {getIcon(achievement.icon)}
                            </div>
                            <div className="flex-1">
                                <h4
                                    className={`font-medium ${achievement.isUnlocked
                                            ? "text-purple-800 dark:text-purple-300"
                                            : "text-slate-500 dark:text-slate-400"
                                        }`}
                                >
                                    {achievement.title}
                                </h4>
                                <p
                                    className={`text-sm ${achievement.isUnlocked
                                            ? "text-purple-700 dark:text-purple-400"
                                            : "text-slate-500 dark:text-slate-400"
                                        }`}
                                >
                                    {achievement.description}
                                </p>
                                {achievement.isUnlocked && achievement.unlockedAt && (
                                    <p className="text-xs text-purple-600 dark:text-purple-400 mt-1">
                                        Đạt được vào: {new Date(achievement.unlockedAt).toLocaleDateString("vi-VN")}
                                    </p>
                                )}
                            </div>
                            <div
                                className={`text-sm font-bold ${achievement.isUnlocked ? "text-purple-700 dark:text-purple-300" : "text-slate-500 dark:text-slate-400"
                                    }`}
                            >
                                {achievement.daysRequired} ngày
                            </div>
                        </div>

                        {achievement.isUnlocked && (
                            <motion.div
                                className="absolute -top-1 -right-1 w-6 h-6 bg-yellow-400 rounded-full flex items-center justify-center"
                                initial={{ scale: 0 }}
                                animate={{ scale: 1 }}
                                transition={{ type: "spring", stiffness: 500, damping: 15 }}
                            >
                                <Trophy className="w-3 h-3 text-white" />
                            </motion.div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    )
}
