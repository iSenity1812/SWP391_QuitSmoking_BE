"use client"

import type React from "react"
import { useState } from "react"
import { motion } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Flame, CheckCircle, Calendar, Award, Target, Clock } from "lucide-react"
import type { StreakData } from "@/pages/plan/styles/ui/types/plan"

interface StreakTrackerProps {
    streakData: StreakData
    onCheckIn: (status: "success" | "failed" | "partial", cigarettesSmoked?: number, notes?: string) => void
    canCheckIn: boolean
    motivationalMessage: string
    getDaysUntilNextMilestone?: () => number | null
}

export const StreakTracker: React.FC<StreakTrackerProps> = ({
    streakData,
    onCheckIn,
    canCheckIn,
    motivationalMessage,
    getDaysUntilNextMilestone,
}) => {
    const [notes, setNotes] = useState<string>("")
    const [isAddingNote, setIsAddingNote] = useState(false)

    const handleAddNote = () => {
        if (notes.trim()) {
            onCheckIn("success", 0, notes)
            setNotes("")
            setIsAddingNote(false)
        }
    }

    const daysUntilNextMilestone = getDaysUntilNextMilestone?.() || null

    return (
        <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-orange-100 dark:border-slate-700 shadow-xl rounded-lg p-6">
            <div className="flex items-center gap-2 mb-6">
                <Flame className="w-6 h-6 text-orange-500" />
                <h3 className="text-xl font-semibold text-slate-900 dark:text-white">Streak Tự Động</h3>
                <div className="ml-auto bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 px-3 py-1 rounded-full text-sm font-medium">
                    Tự động theo dõi
                </div>
            </div>

            <div className="flex flex-col md:flex-row items-center justify-between gap-6 mb-8">
                <div className="flex flex-col items-center">
                    <div className="text-6xl md:text-8xl font-black text-orange-500 mb-2 flex items-center">
                        {streakData.currentStreak}
                        <motion.div
                            animate={{ scale: [1, 1.2, 1] }}
                            transition={{ repeat: Number.POSITIVE_INFINITY, duration: 1.5 }}
                            className="ml-2"
                        >
                            <Flame className="w-10 h-10 md:w-12 md:h-12 text-orange-500" />
                        </motion.div>
                    </div>
                    <div className="text-slate-600 dark:text-slate-300 text-lg">ngày liên tiếp</div>
                    {streakData.currentStreak > 0 && (
                        <div className="text-emerald-600 dark:text-emerald-400 text-sm mt-1">Tự động cập nhật hàng ngày</div>
                    )}
                </div>

                <div className="flex flex-col items-center md:items-start gap-2">
                    <div className="flex items-center gap-2">
                        <Award className="w-5 h-5 text-purple-500" />
                        <span className="text-slate-700 dark:text-slate-200">Streak dài nhất:</span>
                        <span className="font-bold text-purple-600 dark:text-purple-400">{streakData.longestStreak} ngày</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <Calendar className="w-5 h-5 text-blue-500" />
                        <span className="text-slate-700 dark:text-slate-200">Tổng số ngày:</span>
                        <span className="font-bold text-blue-600 dark:text-blue-400">{streakData.totalDays} ngày</span>
                    </div>
                    <div className="flex items-center gap-2">
                        <CheckCircle className="w-5 h-5 text-emerald-500" />
                        <span className="text-slate-700 dark:text-slate-200">Cập nhật lần cuối:</span>
                        <span className="font-bold text-emerald-600 dark:text-emerald-400">
                            {streakData.lastCheckIn ? new Date(streakData.lastCheckIn).toLocaleDateString("vi-VN") : "Chưa bắt đầu"}
                        </span>
                    </div>
                    {daysUntilNextMilestone && (
                        <div className="flex items-center gap-2">
                            <Target className="w-5 h-5 text-amber-500" />
                            <span className="text-slate-700 dark:text-slate-200">Cột mốc tiếp theo:</span>
                            <span className="font-bold text-amber-600 dark:text-amber-400">{daysUntilNextMilestone} ngày nữa</span>
                        </div>
                    )}
                </div>
            </div>

            <div className="bg-orange-50 dark:bg-orange-900/20 p-4 rounded-lg border border-orange-200 dark:border-orange-800 mb-6">
                <p className="text-orange-800 dark:text-orange-300 text-center font-medium">{motivationalMessage}</p>
            </div>

            {/* Automatic Tracking Info */}
            <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg border border-blue-200 dark:border-blue-800 mb-6">
                <div className="flex items-center gap-2 mb-2">
                    <Clock className="w-5 h-5 text-blue-600" />
                    <h4 className="font-medium text-blue-800 dark:text-blue-300">Theo Dõi Tự Động</h4>
                </div>
                <p className="text-blue-700 dark:text-blue-300 text-sm">
                    Streak của bạn được tính tự động dựa trên ngày bắt đầu kế hoạch. Mỗi ngày trôi qua kể từ khi bạn bắt đầu cai
                    thuốc sẽ được cộng vào streak.
                </p>
            </div>

            {/* Optional Note Adding */}
            {canCheckIn && (
                <div className="space-y-4">
                    {!isAddingNote ? (
                        <Button
                            onClick={() => setIsAddingNote(true)}
                            variant="outline"
                            className="w-full border-emerald-300 text-emerald-700 hover:bg-emerald-50 dark:border-emerald-600 dark:text-emerald-400 dark:hover:bg-emerald-900/20"
                        >
                            Thêm Ghi Chú Cho Hôm Nay
                        </Button>
                    ) : (
                        <motion.div
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: "auto" }}
                            exit={{ opacity: 0, height: 0 }}
                            className="space-y-4 bg-white dark:bg-slate-900 p-4 rounded-lg border border-slate-200 dark:border-slate-700"
                        >
                            <div className="space-y-2">
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
                                    Ghi chú cho ngày hôm nay:
                                </label>
                                <Textarea
                                    value={notes}
                                    onChange={(e) => setNotes(e.target.value)}
                                    placeholder="Cảm nhận, thành tựu, hoặc thử thách của bạn hôm nay..."
                                    className="min-h-[100px]"
                                />
                            </div>

                            <div className="flex gap-2">
                                <Button onClick={handleAddNote} className="flex-1">
                                    Lưu Ghi Chú
                                </Button>
                                <Button
                                    variant="outline"
                                    onClick={() => {
                                        setIsAddingNote(false)
                                        setNotes("")
                                    }}
                                    className="flex-1"
                                >
                                    Hủy
                                </Button>
                            </div>
                        </motion.div>
                    )}
                </div>
            )}

            {/* Progress to Next Milestone */}
            {daysUntilNextMilestone && (
                <div className="mt-6 bg-gradient-to-r from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 p-4 rounded-lg border border-purple-200 dark:border-purple-800">
                    <div className="flex items-center justify-between mb-2">
                        <span className="text-purple-700 dark:text-purple-300 font-medium">Tiến độ đến cột mốc tiếp theo</span>
                        <span className="text-purple-600 dark:text-purple-400 text-sm">{daysUntilNextMilestone} ngày nữa</span>
                    </div>
                    <div className="w-full bg-purple-200 dark:bg-purple-800 rounded-full h-2">
                        <div
                            className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all duration-500"
                            style={{
                                width: `${Math.max(10, (streakData.currentStreak / (streakData.currentStreak + daysUntilNextMilestone)) * 100)}%`,
                            }}
                        />
                    </div>
                </div>
            )}
        </div>
    )
}
