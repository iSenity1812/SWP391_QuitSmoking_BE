"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Calendar, Cigarette, TrendingDown, Target, CheckCircle, AlertCircle } from "lucide-react"
import type { Plan } from "./ui/types/plan"

interface DailyEntry {
  date: string
  cigarettesSmoked: number
  targetCigarettes: number
  notes?: string
}

interface GradualPlanTrackerProps {
  plan: Plan
  onProgressUpdate?: (progress: number) => void
}

export const GradualPlanTracker: React.FC<GradualPlanTrackerProps> = ({ plan, onProgressUpdate }) => {
  const [dailyEntries, setDailyEntries] = useState<DailyEntry[]>([])
  const [todayCigarettes, setTodayCigarettes] = useState<number>(0)
  const [notes, setNotes] = useState<string>("")
  const [currentDay, setCurrentDay] = useState<number>(1)

  // Calculate current day based on plan start date
  useEffect(() => {
    const startDate = new Date(plan.startDate)
    const today = new Date()
    startDate.setHours(0, 0, 0, 0)
    today.setHours(0, 0, 0, 0)

    const diffTime = today.getTime() - startDate.getTime()
    const diffDays = Math.floor(diffTime / (24 * 60 * 60 * 1000)) + 1
    setCurrentDay(Math.max(1, diffDays))
  }, [plan.startDate])

  // Load saved entries from localStorage
  useEffect(() => {
    const savedEntries = localStorage.getItem(`gradual-tracker-${plan.id}`)
    if (savedEntries) {
      setDailyEntries(JSON.parse(savedEntries))
    }
  }, [plan.id])

  // Save entries to localStorage
  useEffect(() => {
    if (dailyEntries.length > 0) {
      localStorage.setItem(`gradual-tracker-${plan.id}`, JSON.stringify(dailyEntries))
    }
  }, [dailyEntries, plan.id])

  // Calculate target cigarettes for current day
  const getTargetForDay = (day: number): number => {
    if (plan.reductionSchedule && plan.reductionSchedule[day - 1]) {
      return plan.reductionSchedule[day - 1].cigarettesPerDay
    }
    // Fallback calculation: reduce by 1 each day
    return Math.max(0, plan.dailyCigarettes - (day - 1))
  }

  // Calculate overall progress using algorithm
  const calculateProgress = (): { percentage: number; status: string; message: string } => {
    if (dailyEntries.length === 0) {
      return { percentage: 0, status: "not-started", message: "Chưa bắt đầu theo dõi" }
    }

    let totalScore = 0
    let maxPossibleScore = 0
    let consecutiveSuccessDays = 0

    dailyEntries.forEach((entry) => {
      const dayNumber = dailyEntries.indexOf(entry) + 1
      const target = getTargetForDay(dayNumber)
      const actual = entry.cigarettesSmoked

      // Calculate daily score (0-100 points per day)
      let dailyScore = 0
      if (actual <= target) {
        // Perfect or better than target
        dailyScore = 100
        consecutiveSuccessDays++
      } else if (actual <= target + 2) {
        // Close to target (within 2 cigarettes)
        dailyScore = 70
        consecutiveSuccessDays = 0
      } else if (actual <= plan.dailyCigarettes) {
        // Still reducing but not meeting target
        const reductionFromOriginal = plan.dailyCigarettes - actual
        const maxReduction = plan.dailyCigarettes - target
        dailyScore = Math.max(30, (reductionFromOriginal / maxReduction) * 50)
        consecutiveSuccessDays = 0
      } else {
        // Smoking more than original amount
        dailyScore = 0
        consecutiveSuccessDays = 0
      }

      totalScore += dailyScore
      maxPossibleScore += 100
    })

    // Bonus for consistency
    const consistencyBonus = Math.min(20, consecutiveSuccessDays * 2)
    totalScore += consistencyBonus

    const percentage = Math.min(100, (totalScore / maxPossibleScore) * 100)

    // Determine status and message
    let status = "in-progress"
    let message = "Đang tiến hành tốt"

    if (percentage >= 90) {
      status = "excellent"
      message = "Xuất sắc! Bạn đang làm rất tốt!"
    } else if (percentage >= 75) {
      status = "good"
      message = "Tốt! Tiếp tục duy trì"
    } else if (percentage >= 50) {
      status = "fair"
      message = "Khá ổn, hãy cố gắng hơn"
    } else if (percentage >= 25) {
      status = "needs-improvement"
      message = "Cần cải thiện, đừng bỏ cuộc"
    } else {
      status = "struggling"
      message = "Gặp khó khăn, hãy tìm kiếm hỗ trợ"
    }

    // Check if plan is completed
    const targetDay = plan.dailyCigarettes + 1
    if (currentDay >= targetDay && percentage >= 80) {
      status = "completed"
      message = "Hoàn thành kế hoạch! Chúc mừng!"
    }

    return { percentage, status, message }
  }

  // Handle daily entry submission
  const handleSubmitDaily = () => {
    const today = new Date().toISOString().split("T")[0]
    const target = getTargetForDay(currentDay)

    const newEntry: DailyEntry = {
      date: today,
      cigarettesSmoked: todayCigarettes,
      targetCigarettes: target,
      notes: notes.trim() || undefined,
    }

    // Update or add entry for today
    const existingIndex = dailyEntries.findIndex((entry) => entry.date === today)
    if (existingIndex >= 0) {
      const updatedEntries = [...dailyEntries]
      updatedEntries[existingIndex] = newEntry
      setDailyEntries(updatedEntries)
    } else {
      setDailyEntries([...dailyEntries, newEntry])
    }

    // Reset form
    setTodayCigarettes(0)
    setNotes("")

    // Update progress
    const progress = calculateProgress()
    onProgressUpdate?.(progress.percentage)
  }

  // Get today's entry if exists
  const todayEntry = dailyEntries.find((entry) => entry.date === new Date().toISOString().split("T")[0])
  const targetToday = getTargetForDay(currentDay)
  const progress = calculateProgress()

  return (
    <div className="space-y-6">
      {/* Progress Overview */}
      <Card className="p-6 bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-blue-100 dark:border-slate-700">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white flex items-center gap-2">
            <TrendingDown className="w-5 h-5 text-blue-500" />
            Tiến độ tổng thể
          </h3>
          <div className="text-right">
            <div className="text-sm text-slate-600 dark:text-slate-400">
              Ngày {currentDay} / {plan.dailyCigarettes + 1}
            </div>
          </div>
        </div>

        <Progress value={progress.percentage} className="mb-4" />

        <div className="flex items-center justify-between">
          <span className="text-2xl font-bold text-slate-900 dark:text-white">
            {progress.percentage.toFixed(0)}% hoàn thành
          </span>
          <Badge
            className={`${progress.status === "excellent"
                ? "bg-green-500"
                : progress.status === "good"
                  ? "bg-blue-500"
                  : progress.status === "fair"
                    ? "bg-yellow-500"
                    : progress.status === "needs-improvement"
                      ? "bg-orange-500"
                      : progress.status === "completed"
                        ? "bg-emerald-500"
                        : "bg-red-500"
              } text-white`}
          >
            {progress.message}
          </Badge>
        </div>
      </Card>

      {/* Daily Input */}
      <Card className="p-6 bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-blue-100 dark:border-slate-700">
        <h3 className="text-lg font-semibold text-slate-900 dark:text-white mb-4 flex items-center gap-2">
          <Calendar className="w-5 h-5 text-blue-500" />
          Nhập liệu hôm nay
        </h3>

        <div className="grid gap-4">
          <div className="flex items-center justify-between p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-700">
            <div className="flex items-center gap-2">
              <Target className="w-5 h-5 text-blue-500" />
              <span className="font-medium text-blue-800 dark:text-blue-300">Mục tiêu hôm nay:</span>
            </div>
            <span className="text-xl font-bold text-blue-800 dark:text-blue-300">{targetToday} điếu</span>
          </div>

          {todayEntry ? (
            <div className="p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg border border-emerald-200 dark:border-emerald-700">
              <div className="flex items-center gap-2 mb-2">
                <CheckCircle className="w-5 h-5 text-emerald-500" />
                <span className="font-medium text-emerald-800 dark:text-emerald-300">Đã ghi nhận hôm nay:</span>
              </div>
              <div className="text-emerald-700 dark:text-emerald-300">
                <p>
                  Đã hút: <strong>{todayEntry.cigarettesSmoked} điếu</strong>
                </p>
                <p>
                  Mục tiêu: <strong>{todayEntry.targetCigarettes} điếu</strong>
                </p>
                {todayEntry.notes && <p>Ghi chú: {todayEntry.notes}</p>}
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              <div className="grid gap-2">
                <Label htmlFor="cigarettes" className="text-sm font-medium text-slate-900 dark:text-white">
                  Số điếu đã hút hôm nay
                </Label>
                <Input
                  id="cigarettes"
                  type="number"
                  min="0"
                  value={todayCigarettes}
                  onChange={(e) => setTodayCigarettes(Number(e.target.value) || 0)}
                  className="border-2 border-blue-100 dark:border-slate-600 focus:border-blue-300 dark:focus:border-blue-500"
                  placeholder="Nhập số điếu..."
                />
              </div>

              <div className="grid gap-2">
                <Label htmlFor="notes" className="text-sm font-medium text-slate-900 dark:text-white">
                  Ghi chú (tùy chọn)
                </Label>
                <Input
                  id="notes"
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  className="border-2 border-blue-100 dark:border-slate-600 focus:border-blue-300 dark:focus:border-blue-500"
                  placeholder="Cảm giác, tình huống, ghi chú..."
                />
              </div>

              <Button
                onClick={handleSubmitDaily}
                className="w-full bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white"
              >
                <Cigarette className="w-4 h-4 mr-2" />
                Ghi nhận hôm nay
              </Button>
            </div>
          )}
        </div>
      </Card>

      {/* Recent Entries */}
      {dailyEntries.length > 0 && (
        <Card className="p-6 bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-blue-100 dark:border-slate-700">
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white mb-4">Lịch sử gần đây</h3>
          <div className="space-y-3">
            {dailyEntries
              .slice(-7)
              .reverse()
              .map((entry) => {
                const isSuccess = entry.cigarettesSmoked <= entry.targetCigarettes
                return (
                  <div
                    key={entry.date}
                    className={`p-3 rounded-lg border ${isSuccess
                        ? "bg-emerald-50 dark:bg-emerald-900/20 border-emerald-200 dark:border-emerald-700"
                        : "bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-700"
                      }`}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        {isSuccess ? (
                          <CheckCircle className="w-4 h-4 text-emerald-500" />
                        ) : (
                          <AlertCircle className="w-4 h-4 text-red-500" />
                        )}
                        <span className="font-medium text-slate-900 dark:text-white">
                          {new Date(entry.date).toLocaleDateString("vi-VN")}
                        </span>
                      </div>
                      <div className="text-right">
                        <span
                          className={`font-bold ${isSuccess ? "text-emerald-700 dark:text-emerald-300" : "text-red-700 dark:text-red-300"}`}
                        >
                          {entry.cigarettesSmoked}/{entry.targetCigarettes} điếu
                        </span>
                      </div>
                    </div>
                    {entry.notes && <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">{entry.notes}</p>}
                  </div>
                )
              })}
          </div>
        </Card>
      )}
    </div>
  )
}
