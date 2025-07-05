"use client"

import { Button } from "@/components/ui/button"
import { Trophy, Award } from "lucide-react"
import type { AchievementNotification } from "../types/user-types"

interface AchievementNotificationModalProps {
    notification: AchievementNotification
    onClose: () => void
    onViewAchievements: () => void
}

export function AchievementNotificationModal({
    notification,
    onClose,
    onViewAchievements,
}: AchievementNotificationModalProps) {
    if (!notification.show || !notification.achievement) return null

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white dark:bg-slate-900 rounded-2xl p-8 max-w-md mx-4 shadow-2xl border border-slate-200 dark:border-slate-700 animate-in zoom-in-95 duration-300">
                <div className="text-center">
                    {/* Celebration Animation */}
                    <div className="relative mb-6">
                        <div className="bg-gradient-to-br from-yellow-400 to-orange-500 rounded-full p-6 inline-block animate-bounce">
                            <Trophy className="h-16 w-16 text-white" />
                        </div>
                        {/* Sparkle effects */}
                        <div className="absolute -top-2 -right-2 text-yellow-400 animate-pulse">✨</div>
                        <div className="absolute -bottom-2 -left-2 text-yellow-400 animate-pulse delay-150">⭐</div>
                        <div className="absolute top-1/2 -right-4 text-yellow-400 animate-pulse delay-300">🎉</div>
                    </div>

                    <h2 className="text-3xl font-bold text-slate-900 dark:text-white mb-2">Chúc mừng!</h2>

                    <p className="text-lg text-slate-600 dark:text-slate-400 mb-4">Bạn đã đạt được thành tựu</p>

                    <div className="bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-emerald-900/30 dark:to-emerald-800/30 rounded-xl p-4 mb-6 border border-emerald-200 dark:border-emerald-800">
                        <div className="flex items-center justify-center gap-3 mb-3">
                            <Award className="h-8 w-8 text-emerald-600 dark:text-emerald-400" />
                            <h3 className="text-xl font-bold text-emerald-700 dark:text-emerald-400">
                                {notification.achievement.name}
                            </h3>
                        </div>
                        <p className="text-emerald-600 dark:text-emerald-400 text-sm mb-3">
                            {notification.achievement.description}
                        </p>
                    </div>

                    <div className="flex gap-3">
                        <Button
                            onClick={onViewAchievements}
                            className="flex-1 bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700"
                        >
                            <Award className="h-4 w-4 mr-2" />
                            Xem thành tựu
                        </Button>
                        <Button variant="outline" onClick={onClose} className="flex-1">
                            Đóng
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    )
}
