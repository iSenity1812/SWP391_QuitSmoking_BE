"use client"

import type React from "react"
import { useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import {
    Calendar,
    DollarSign,
    Heart,
    TrendingUp,
    Award,
    Clock,
    Cigarette,
    Target,
    CheckCircle,
    Sparkles,
    RotateCcw,
    Edit,
} from "lucide-react"

interface PlanData {
    id: string
    title: string
    startDate: Date
    quitDate: Date
    quitType: "gradual" | "fullstop"
    dailyCigarettes: number
    cigaretteType: string
    motivation: string
    goals: string[]
    reductionPlan?: {
        duration: number
        targetReduction: number
    }
}

interface Achievement {
    id: string
    title: string
    description: string
    icon: any
    days: number
    unlocked: boolean
    unlockedAt?: Date
}

interface ProgressDashboardProps {
    plan: PlanData
    onEditPlan: () => void
    onRestartPlan: () => void
}

export const ProgressDashboard: React.FC<ProgressDashboardProps> = ({ plan, onEditPlan, onRestartPlan }) => {
    // const [currentDate, setCurrentDate] = useState(new Date())

    // Update current date every minute
    useEffect(() => {
        const timer = setInterval(() => {
            // setCurrentDate(new Date())
        }, 60000)

        return () => clearInterval(timer)
    }, [])

    // Calculate progress metrics
    const calculateProgress = () => {
        // const startDate = new Date(plan.startDate)
        const quitDate = new Date(plan.quitDate)
        const today = new Date()

        // Days since quit date
        const timeSinceQuit = today.getTime() - quitDate.getTime()
        const daysSinceQuit = Math.max(0, Math.floor(timeSinceQuit / (1000 * 3600 * 24)))

        // Calculate savings
        const cigarettePrices: { [key: string]: number } = {
            "Vinataba (mềm)": 25000,
            "Vinataba (đóng gói cứng)": 30000,
            "Thăng Long": 23000,
            "Marlboro (đỏ/trắng)": 35000,
            "555": 40000,
            Esse: 30000,
            "Black Stone": 50000,
            "Captain Black": 60000,
            Khác: 30000,
        }

        const pricePerPack = cigarettePrices[plan.cigaretteType] || 30000
        const costPerCigarette = pricePerPack / 20
        const dailyCost = costPerCigarette * plan.dailyCigarettes
        const totalSaved = dailyCost * daysSinceQuit

        // Calculate cigarettes not smoked
        const cigarettesNotSmoked = plan.dailyCigarettes * daysSinceQuit

        // Calculate health improvements (simplified)
        const healthScore = Math.min(100, daysSinceQuit * 2) // 2 points per day, max 100

        return {
            daysSinceQuit,
            totalSaved,
            cigarettesNotSmoked,
            healthScore,
            isActive: daysSinceQuit >= 0,
        }
    }

    const progress = calculateProgress()

    // Define achievements
    const achievements: Achievement[] = [
        {
            id: "first-day",
            title: "Ngày Đầu Tiên",
            description: "Hoàn thành ngày đầu tiên không hút thuốc",
            icon: CheckCircle,
            days: 1,
            unlocked: progress.daysSinceQuit >= 1,
        },
        {
            id: "first-week",
            title: "Tuần Đầu Tiên",
            description: "Vượt qua tuần đầu tiên thành công",
            icon: Calendar,
            days: 7,
            unlocked: progress.daysSinceQuit >= 7,
        },
        {
            id: "first-month",
            title: "Tháng Đầu Tiên",
            description: "Hoàn thành tháng đầu tiên không khói thuốc",
            icon: Award,
            days: 30,
            unlocked: progress.daysSinceQuit >= 30,
        },
        {
            id: "three-months",
            title: "Ba Tháng",
            description: "Chức năng phổi được cải thiện đáng kể",
            icon: Heart,
            days: 90,
            unlocked: progress.daysSinceQuit >= 90,
        },
        {
            id: "six-months",
            title: "Sáu Tháng",
            description: "Nguy cơ bệnh tim giảm một nửa",
            icon: TrendingUp,
            days: 180,
            unlocked: progress.daysSinceQuit >= 180,
        },
        {
            id: "one-year",
            title: "Một Năm",
            description: "Nguy cơ bệnh tim chỉ còn một nửa so với người hút thuốc",
            icon: Sparkles,
            days: 365,
            unlocked: progress.daysSinceQuit >= 365,
        },
    ]

    const nextAchievement = achievements.find((a) => !a.unlocked)
    const unlockedAchievements = achievements.filter((a) => a.unlocked)

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold text-slate-800 dark:text-white">{plan.title}</h1>
                    <p className="text-slate-600 dark:text-slate-300">
                        Bắt đầu từ {new Date(plan.startDate).toLocaleDateString("vi-VN")}
                    </p>
                </div>
                <div className="flex gap-2">
                    <Button variant="outline" onClick={onEditPlan}>
                        <Edit className="w-4 h-4 mr-2" />
                        Chỉnh Sửa
                    </Button>
                    <Button variant="outline" onClick={onRestartPlan} className="text-orange-600 hover:text-orange-700">
                        <RotateCcw className="w-4 h-4 mr-2" />
                        Bắt Đầu Lại
                    </Button>
                </div>
            </div>

            {/* Main Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                <StatsCard
                    icon={Calendar}
                    title="Ngày Không Hút Thuốc"
                    value={progress.daysSinceQuit.toString()}
                    subtitle="ngày"
                    gradient="from-emerald-400 to-emerald-600"
                />
                <StatsCard
                    icon={DollarSign}
                    title="Tiền Tiết Kiệm"
                    value={progress.totalSaved.toLocaleString("vi-VN")}
                    subtitle="VNĐ"
                    gradient="from-green-400 to-green-600"
                />
                <StatsCard
                    icon={Cigarette}
                    title="Điếu Thuốc Tránh Được"
                    value={progress.cigarettesNotSmoked.toString()}
                    subtitle="điếu"
                    gradient="from-orange-400 to-orange-600"
                />
                <StatsCard
                    icon={Heart}
                    title="Điểm Sức Khỏe"
                    value={progress.healthScore.toString()}
                    subtitle="/100"
                    gradient="from-rose-400 to-rose-600"
                />
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Progress Overview */}
                <div className="lg:col-span-2 space-y-6">
                    {/* Next Achievement */}
                    {nextAchievement && (
                        <Card className="bg-gradient-to-br from-blue-50 to-blue-100 dark:from-blue-900/20 dark:to-blue-800/20 border-2 border-blue-200 dark:border-blue-700">
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2 text-blue-700 dark:text-blue-300">
                                    <Target className="w-5 h-5" />
                                    Mục Tiêu Tiếp Theo
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-blue-500 rounded-full flex items-center justify-center">
                                        <nextAchievement.icon className="w-6 h-6 text-white" />
                                    </div>
                                    <div className="flex-1">
                                        <h3 className="font-semibold text-blue-800 dark:text-blue-200">{nextAchievement.title}</h3>
                                        <p className="text-sm text-blue-600 dark:text-blue-300">{nextAchievement.description}</p>
                                        <div className="mt-2">
                                            <div className="flex justify-between text-sm text-blue-600 dark:text-blue-300 mb-1">
                                                <span>
                                                    {progress.daysSinceQuit} / {nextAchievement.days} ngày
                                                </span>
                                                <span>{Math.round((progress.daysSinceQuit / nextAchievement.days) * 100)}%</span>
                                            </div>
                                            <Progress value={(progress.daysSinceQuit / nextAchievement.days) * 100} className="h-2" />
                                        </div>
                                    </div>
                                </div>
                                {nextAchievement.days - progress.daysSinceQuit > 0 && (
                                    <div className="mt-3 p-2 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
                                        <p className="text-sm text-blue-700 dark:text-blue-300">
                                            <Clock className="w-4 h-4 inline mr-1" />
                                            Còn {nextAchievement.days - progress.daysSinceQuit} ngày nữa để đạt được thành tựu này!
                                        </p>
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    {/* Health Timeline */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Heart className="w-5 h-5 text-rose-500" />
                                Cải Thiện Sức Khỏe
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-4">
                                {[
                                    {
                                        time: "20 phút",
                                        benefit: "Nhịp tim và huyết áp trở về bình thường",
                                        achieved: progress.daysSinceQuit >= 0,
                                    },
                                    {
                                        time: "12 giờ",
                                        benefit: "Mức CO trong máu giảm xuống bình thường",
                                        achieved: progress.daysSinceQuit >= 1,
                                    },
                                    { time: "2 tuần", benefit: "Tuần hoàn máu được cải thiện", achieved: progress.daysSinceQuit >= 14 },
                                    { time: "1 tháng", benefit: "Chức năng phổi tăng cường 30%", achieved: progress.daysSinceQuit >= 30 },
                                    { time: "3 tháng", benefit: "Nguy cơ đau tim giảm đáng kể", achieved: progress.daysSinceQuit >= 90 },
                                    { time: "1 năm", benefit: "Nguy cơ bệnh tim giảm 50%", achieved: progress.daysSinceQuit >= 365 },
                                ].map((item, index) => (
                                    <div
                                        key={index}
                                        className={`flex items-center gap-3 p-3 rounded-lg ${item.achieved
                                            ? "bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-200 dark:border-emerald-700"
                                            : "bg-gray-50 dark:bg-slate-700/50"
                                            }`}
                                    >
                                        <div
                                            className={`w-8 h-8 rounded-full flex items-center justify-center ${item.achieved ? "bg-emerald-500" : "bg-gray-300 dark:bg-slate-600"
                                                }`}
                                        >
                                            {item.achieved ? (
                                                <CheckCircle className="w-4 h-4 text-white" />
                                            ) : (
                                                <Clock className="w-4 h-4 text-gray-500 dark:text-slate-400" />
                                            )}
                                        </div>
                                        <div className="flex-1">
                                            <p className="font-medium text-sm">{item.time}</p>
                                            <p className="text-sm text-slate-600 dark:text-slate-300">{item.benefit}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Sidebar */}
                <div className="space-y-6">
                    {/* Plan Details */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Chi Tiết Kế Hoạch</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                            <div>
                                <p className="text-sm font-medium text-slate-600 dark:text-slate-300">Ngày Cai Thuốc</p>
                                <p className="font-semibold">{new Date(plan.quitDate).toLocaleDateString("vi-VN")}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-slate-600 dark:text-slate-300">Phương Pháp</p>
                                <Badge variant="secondary">{plan.quitType === "gradual" ? "Giảm Dần" : "Dừng Hoàn Toàn"}</Badge>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-slate-600 dark:text-slate-300">Thói Quen Cũ</p>
                                <p className="text-sm">{plan.dailyCigarettes} điếu/ngày</p>
                                <p className="text-xs text-slate-500 dark:text-slate-400">{plan.cigaretteType}</p>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Achievements */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Award className="w-5 h-5 text-yellow-500" />
                                Thành Tựu ({unlockedAchievements.length}/{achievements.length})
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-3">
                                {achievements.map((achievement) => (
                                    <div
                                        key={achievement.id}
                                        className={`flex items-center gap-3 p-2 rounded-lg ${achievement.unlocked
                                            ? "bg-emerald-50 dark:bg-emerald-900/20"
                                            : "bg-gray-50 dark:bg-slate-700/50 opacity-60"
                                            }`}
                                    >
                                        <div
                                            className={`w-8 h-8 rounded-full flex items-center justify-center ${achievement.unlocked ? "bg-emerald-500" : "bg-gray-300 dark:bg-slate-600"
                                                }`}
                                        >
                                            <achievement.icon className="w-4 h-4 text-white" />
                                        </div>
                                        <div className="flex-1">
                                            <p className="font-medium text-sm">{achievement.title}</p>
                                            <p className="text-xs text-slate-600 dark:text-slate-300">{achievement.days} ngày</p>
                                        </div>
                                        {achievement.unlocked && <Sparkles className="w-4 h-4 text-emerald-500" />}
                                    </div>
                                ))}
                            </div>
                        </CardContent>
                    </Card>

                    {/* Motivation */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Sparkles className="w-5 h-5 text-pink-500" />
                                Động Lực
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p className="text-sm italic text-slate-600 dark:text-slate-300">"{plan.motivation}"</p>
                            <div className="mt-3">
                                <p className="text-xs font-medium text-slate-600 dark:text-slate-300 mb-2">Mục tiêu:</p>
                                <div className="flex flex-wrap gap-1">
                                    {plan.goals.map((goal, index) => (
                                        <Badge key={index} variant="secondary" className="text-xs">
                                            {goal}
                                        </Badge>
                                    ))}
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    )
}

// Stats Card Component
const StatsCard: React.FC<{
    icon: any
    title: string
    value: string
    subtitle: string
    gradient: string
}> = ({ icon: Icon, title, value, subtitle, gradient }) => {
    return (
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 hover:-translate-y-1">
            <CardContent className="p-6">
                <div className="flex items-center justify-between">
                    <div>
                        <p className="text-sm font-medium text-muted-foreground">{title}</p>
                        <div className="flex items-baseline gap-1">
                            <p className="text-2xl font-bold">{value}</p>
                            {subtitle && <p className="text-sm text-muted-foreground">{subtitle}</p>}
                        </div>
                    </div>
                    <div
                        className={`w-12 h-12 rounded-xl bg-gradient-to-br ${gradient} flex items-center justify-center shadow-lg`}
                    >
                        <Icon className="w-6 h-6 text-white" />
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}
