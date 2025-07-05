"use client"
import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Dialog } from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { AnimatedSection } from "@/components/ui/AnimatedSection"
import { PlanStats } from "@/pages/plan/styles/PlanStats"
import { PlanDetails } from "@/pages/plan/styles/PlanDetail"
import { PlanProgress } from "@/pages/plan/styles/PlanProgress"
import { PlanFormDialog } from "@/pages/plan/styles/PlanFormDialog"
import { ReductionSchedule } from "@/pages/plan/styles/ReductionSchedule"
import { StreakTracker } from "@/pages/plan/styles/StreakTracker"
import { StreakCalendar } from "@/pages/plan/styles/StreakCalendar"
import { StreakAchievements } from "./styles/StreakAchivements"
import { usePlanCalculations } from "@/hooks/usePlanCalculations"
import { usePlanStorage } from "@/hooks/usePlanStorage"
import { usePlanForm } from "@/hooks/usePlanForm"
import { useStreakTracking } from "@/hooks/useStreakTracking"
import { CIGARETTE_PRICES } from "@/pages/plan/styles/ui/types/cigarette"
import { Target, AlertTriangle, CalendarIcon, Plus, Sparkles, TrendingDown, Zap, Flame } from "lucide-react"

export default function PlanPage() {
    const [date, setDate] = useState<Date | undefined>(new Date())
    const [isConfirmingDelete, setIsConfirmingDelete] = useState(false)
    const [activeTab, setActiveTab] = useState("plan")

    // Custom hooks
    const { currentPlan, setCurrentPlan, userSubscription } = usePlanStorage()
    const { streakData, checkIn, resetStreak, canCheckInToday, getMotivationalMessage } = useStreakTracking(currentPlan)
    const { days: smokeFreeDays, saved: moneySaved, progress } = usePlanCalculations(currentPlan)
    const {
        isCreatingPlan,
        setIsCreatingPlan,
        newPlan,
        resetForm,
        handleCreatePlan,
        handleEditPlan,
        handleInputChange,
        handleSelectChange,
        handleDateChange,
        handleNumberChange,
        selectedPlanType,
        setSelectedPlanType,
    } = usePlanForm(setCurrentPlan)

    // Force re-render when localStorage changes
    useEffect(() => {
        const handleStorageChange = () => {
            window.location.reload()
        }

        window.addEventListener("storage", handleStorageChange)
        return () => window.removeEventListener("storage", handleStorageChange)
    }, [])

    // Calculate current day for gradual plans
    const getCurrentDay = () => {
        if (!currentPlan || currentPlan.planType !== "gradual") return 1
        const startDate = new Date(currentPlan.startDate)
        const now = new Date()
        startDate.setHours(0, 0, 0, 0)
        now.setHours(0, 0, 0, 0)
        const diffTime = now.getTime() - startDate.getTime()
        const diffDays = Math.floor(diffTime / (24 * 60 * 60 * 1000))
        return Math.max(1, diffDays + 1) // +1 because day 1 is the start date
    }

    // Subscription logic
    const isPremiumUser = userSubscription.type === "premium"
    const isSubscriptionActive =
        isPremiumUser && (!userSubscription.expiryDate || userSubscription.expiryDate > new Date())

    const handleDeletePlan = () => {
        setCurrentPlan(null)
        setIsConfirmingDelete(false)
        resetForm()
        // Also reset streak when plan is deleted
        resetStreak()
    }

    // Get plan type display info with safe fallback
    const getPlanTypeInfo = (planType?: string) => {
        switch (planType) {
            case "gradual":
                return {
                    label: "Giảm Dần",
                    icon: TrendingDown,
                    color: "bg-blue-500",
                    description: "Giảm từng tuần một cách có kế hoạch",
                }
            case "cold-turkey":
                return {
                    label: "Dứt Khoát",
                    icon: Zap,
                    color: "bg-red-500",
                    description: "Ngừng hút thuốc ngay lập tức",
                }
            default:
                return {
                    label: "Kế Hoạch",
                    icon: Target,
                    color: "bg-emerald-500",
                    description: "Kế hoạch cai thuốc",
                }
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-800 dark:to-slate-900 pt-20 overflow-x-hidden">
            <div className="container mx-auto py-4 px-4 sm:px-6 lg:px-8 max-w-7xl">
                {/* Premium Banner */}
                {isSubscriptionActive && (
                    <div className="mb-6 bg-gradient-to-r from-emerald-500 to-emerald-600 text-white p-4 rounded-lg shadow-lg">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                                <Sparkles className="w-5 h-5" />
                                <span className="font-medium">
                                    Gói Premium {userSubscription.duration} - Hết hạn:{" "}
                                    {userSubscription.expiryDate?.toLocaleDateString("vi-VN")}
                                </span>
                            </div>
                            <span className="bg-white/20 text-white px-2 py-1 rounded text-xs">PREMIUM</span>
                        </div>
                    </div>
                )}

                {/* Automatic Streak Banner - Show when plan exists */}
                {currentPlan && streakData && (
                    <div className="mb-6 bg-gradient-to-r from-orange-500 to-red-500 text-white p-4 rounded-lg shadow-lg">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-3">
                                <Flame className="w-6 h-6" />
                                <div>
                                    <span className="font-bold text-lg">{streakData.currentStreak} ngày</span>
                                    <span className="ml-2 text-orange-100">streak tự động</span>
                                </div>
                            </div>
                            <div className="text-right">
                                <div className="text-sm text-orange-100">Streak dài nhất</div>
                                <div className="font-bold">{streakData.longestStreak} ngày</div>
                            </div>
                        </div>
                        <div className="mt-2 text-orange-100 text-sm">⚡ Tự động cập nhật dựa trên ngày bắt đầu kế hoạch</div>
                    </div>
                )}

                {/* Dialogs */}
                <Dialog open={isCreatingPlan} onOpenChange={setIsCreatingPlan}>
                    <PlanFormDialog
                        newPlan={newPlan}
                        selectedPlanType={selectedPlanType}
                        setSelectedPlanType={setSelectedPlanType}
                        onInputChange={handleInputChange}
                        onSelectChange={handleSelectChange}
                        onDateChange={handleDateChange}
                        onNumberChange={handleNumberChange}
                        onSubmit={handleCreatePlan}
                        onCancel={() => {
                            setIsCreatingPlan(false)
                            resetForm()
                        }}
                        cigarettePrices={CIGARETTE_PRICES}
                        isEditing={!!currentPlan}
                        isPremium={isSubscriptionActive}
                    />
                </Dialog>

                {/* Delete Dialog */}
                <Dialog open={isConfirmingDelete} onOpenChange={setIsConfirmingDelete}>
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4">
                        <div className="bg-white dark:bg-slate-900 rounded-lg p-6 max-w-md w-full">
                            <div className="flex items-center gap-2 text-red-600 dark:text-red-400 mb-4">
                                <AlertTriangle className="w-5 h-5" />
                                <h3 className="text-lg font-semibold">Xóa Kế Hoạch</h3>
                            </div>
                            <p className="text-slate-700 dark:text-slate-300 mb-6">
                                Bạn có chắc chắn muốn xóa kế hoạch cai thuốc lá của mình không? Điều này cũng sẽ xóa toàn bộ streak tự
                                động của bạn.
                            </p>
                            <div className="flex justify-end gap-4">
                                <Button variant="outline" onClick={() => setIsConfirmingDelete(false)}>
                                    Hủy
                                </Button>
                                <Button onClick={handleDeletePlan} className="bg-red-500 hover:bg-red-600 text-white">
                                    Xóa Kế Hoạch
                                </Button>
                            </div>
                        </div>
                    </div>
                </Dialog>

                {/* Main Content */}
                <Tabs value={activeTab} onValueChange={setActiveTab} className="mb-8">
                    <TabsList className="grid grid-cols-2 w-full max-w-2xl mx-auto h-auto sm:h-12 bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border border-slate-200 dark:border-slate-700 shadow-sm">
                        <TabsTrigger
                            value="plan"
                            className="flex items-center gap-1 sm:gap-1.5 text-xs sm:text-sm font-medium px-2 sm:px-3 py-2 data-[state=active]:bg-emerald-500 data-[state=active]:text-white"
                        >
                            <Target className="w-3 h-3 sm:w-4 sm:h-4" />
                            <span className="hidden sm:inline">Kế Hoạch</span>
                            <span className="sm:hidden">Plan</span>
                        </TabsTrigger>
                        <TabsTrigger
                            value="streak"
                            className="flex items-center gap-1 sm:gap-1.5 text-xs sm:text-sm font-medium px-2 sm:px-3 py-2 data-[state=active]:bg-emerald-500 data-[state=active]:text-white"
                        >
                            <Flame className="w-3 h-3 sm:w-4 sm:h-4" />
                            <span className="hidden sm:inline">Streak</span>
                            <span className="sm:hidden">Streak</span>
                        </TabsTrigger>
                    </TabsList>

                    <TabsContent value="plan">
                        <AnimatePresence mode="wait">
                            {!currentPlan ? (
                                <motion.div
                                    key="no-plan"
                                    initial={{ opacity: 0, y: 20 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -20 }}
                                    className="flex flex-col items-center justify-center min-h-[60vh] space-y-8"
                                >
                                    <div className="text-center space-y-6">
                                        <div className="w-24 h-24 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center mx-auto shadow-xl">
                                            <Target className="w-12 h-12 text-white" />
                                        </div>
                                        <h1 className="text-2xl sm:text-3xl lg:text-4xl xl:text-5xl font-black text-slate-800 dark:text-white">
                                            Bắt Đầu Hành Trình
                                        </h1>
                                        <p className="text-base sm:text-lg lg:text-xl text-slate-600 dark:text-slate-300 max-w-2xl mx-auto leading-relaxed px-4">
                                            Chọn phương pháp cai thuốc phù hợp với bạn và bắt đầu theo dõi streak tự động hàng ngày
                                        </p>

                                        {/* Plan Type Preview */}
                                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6 max-w-4xl mx-auto px-4">
                                            <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-blue-100 dark:border-slate-700 rounded-lg p-4 sm:p-6 shadow-lg">
                                                <div className="flex items-center gap-3 mb-4">
                                                    <div className="w-12 h-12 rounded-lg bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                                                        <TrendingDown className="w-6 h-6 text-white" />
                                                    </div>
                                                    <div>
                                                        <h3 className="font-bold text-slate-800 dark:text-white">Giảm Dần</h3>
                                                        <Badge className="bg-blue-100 text-blue-800 text-xs">Khuyến nghị</Badge>
                                                    </div>
                                                </div>
                                                <p className="text-sm text-slate-600 dark:text-slate-300">
                                                    Giảm 1 điếu mỗi ngày một cách có kế hoạch và khoa học
                                                </p>
                                            </div>

                                            <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-red-100 dark:border-slate-700 rounded-lg p-4 sm:p-6 shadow-lg">
                                                <div className="flex items-center gap-3 mb-4">
                                                    <div className="w-12 h-12 rounded-lg bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center">
                                                        <Zap className="w-6 h-6 text-white" />
                                                    </div>
                                                    <div>
                                                        <h3 className="font-bold text-slate-800 dark:text-white">Dứt Khoát</h3>
                                                        <Badge className="bg-red-100 text-red-800 text-xs">Thử thách</Badge>
                                                    </div>
                                                </div>
                                                <p className="text-sm text-slate-600 dark:text-slate-300">
                                                    Ngừng hút thuốc hoàn toàn ngay từ ngày đầu tiên
                                                </p>
                                            </div>
                                        </div>

                                        {isSubscriptionActive && (
                                            <div className="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-lg border border-emerald-200 dark:border-emerald-700">
                                                <p className="text-emerald-700 dark:text-emerald-300 font-medium">
                                                    🎉 Bạn đang sử dụng gói Premium! Tận hưởng các tính năng cao cấp khi tạo kế hoạch.
                                                </p>
                                            </div>
                                        )}
                                    </div>

                                    <button
                                        onClick={() => {
                                            resetForm()
                                            setSelectedPlanType(null) // Reset plan type selection
                                            setIsCreatingPlan(true)
                                        }}
                                        className="px-6 sm:px-10 py-3 sm:py-5 rounded-xl font-bold text-base sm:text-lg text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 hover:scale-105 transition-all duration-300 hover:-translate-y-1 shadow-xl shadow-emerald-200/50 dark:shadow-emerald-500/25 flex items-center gap-2"
                                    >
                                        <Plus className="w-5 h-5 sm:w-6 sm:h-6" />
                                        <span className="hidden sm:inline">Tạo Kế Hoạch Của Bạn</span>
                                        <span className="sm:hidden">Tạo Kế Hoạch</span>
                                    </button>
                                </motion.div>
                            ) : (
                                <motion.div
                                    key="with-plan"
                                    initial={{ opacity: 0, y: 20 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -20 }}
                                    className="space-y-8"
                                >
                                    {/* Header with Plan Type Badge */}
                                    <AnimatedSection animation="fadeUp" delay={200}>
                                        <div className="text-center space-y-4">
                                            <div className="flex items-center justify-center gap-3 mb-4">
                                                <h1 className="text-4xl lg:text-5xl font-black text-slate-800 dark:text-white">
                                                    Hành Trình Cai Thuốc
                                                </h1>
                                                {currentPlan.planType && (
                                                    <Badge
                                                        className={`text-white text-sm px-3 py-1 ${getPlanTypeInfo(currentPlan.planType).color}`}
                                                    >
                                                        {getPlanTypeInfo(currentPlan.planType).label}
                                                    </Badge>
                                                )}
                                            </div>
                                            <p className="text-xl text-slate-600 dark:text-slate-300">
                                                {currentPlan.planType === "gradual"
                                                    ? "Theo dõi tiến trình giảm dần hàng ngày và ăn mừng mỗi cột mốc quan trọng"
                                                    : currentPlan.planType === "cold-turkey"
                                                        ? "Theo dõi hành trình cai thuốc dứt khoát của bạn"
                                                        : "Theo dõi tiến trình và ăn mừng mỗi cột mốc quan trọng"}
                                            </p>
                                        </div>
                                    </AnimatedSection>

                                    {/* Stats */}
                                    <AnimatedSection animation="fadeUp" delay={300}>
                                        <PlanStats days={smokeFreeDays} saved={moneySaved} progress={progress} />
                                    </AnimatedSection>

                                    <div className="grid grid-cols-1 xl:grid-cols-3 gap-4 sm:gap-6 lg:gap-8">
                                        {/* Left Column */}
                                        <div className="xl:col-span-2 space-y-4 sm:space-y-6">
                                            <AnimatedSection animation="fadeUp" delay={400}>
                                                <PlanProgress
                                                    plan={currentPlan}
                                                    smokeFreeDays={smokeFreeDays}
                                                    moneySaved={moneySaved}
                                                    progress={progress}
                                                    isSubscriptionActive={isSubscriptionActive}
                                                />
                                            </AnimatedSection>

                                            {/* Reduction Schedule for Gradual Plans */}
                                            {currentPlan.planType === "gradual" && currentPlan.reductionSchedule && (
                                                <AnimatedSection animation="fadeUp" delay={450}>
                                                    <ReductionSchedule schedule={currentPlan.reductionSchedule} currentDay={getCurrentDay()} />
                                                </AnimatedSection>
                                            )}

                                            {/* Cold Turkey Motivation for Cold Turkey Plans */}
                                            {currentPlan.planType === "cold-turkey" && (
                                                <AnimatedSection animation="fadeUp" delay={450}>
                                                    <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-red-100 dark:border-slate-700 shadow-xl rounded-lg p-6">
                                                        <div className="flex items-center gap-2 mb-4">
                                                            <Zap className="w-5 h-5 text-red-500" />
                                                            <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
                                                                Phương Pháp Dứt Khoát
                                                            </h3>
                                                        </div>
                                                        <div className="space-y-4">
                                                            <div className="bg-red-50 dark:bg-red-900/20 p-4 rounded-lg border border-red-200 dark:border-red-700">
                                                                <h4 className="font-medium text-red-800 dark:text-red-300 mb-2">
                                                                    Bạn đã chọn ngừng hút thuốc hoàn toàn!
                                                                </h4>
                                                                <p className="text-red-700 dark:text-red-300 text-sm">
                                                                    Đây là phương pháp đòi hỏi ý chí mạnh mẽ nhưng mang lại kết quả nhanh chóng. Hãy kiên
                                                                    trì và nhớ rằng mỗi ngày không hút thuốc là một chiến thắng!
                                                                </p>
                                                            </div>
                                                            <div className="bg-emerald-50 dark:bg-emerald-900/20 p-4 rounded-lg border border-emerald-200 dark:border-emerald-700">
                                                                <h4 className="font-medium text-emerald-800 dark:text-emerald-300 mb-2">
                                                                    Động lực của bạn:
                                                                </h4>
                                                                <p className="text-emerald-700 dark:text-emerald-300 text-sm italic">
                                                                    "{currentPlan.motivation}"
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </AnimatedSection>
                                            )}
                                        </div>

                                        {/* Right Column */}
                                        <div className="space-y-6">
                                            <AnimatedSection animation="fadeUp" delay={600}>
                                                <PlanDetails
                                                    plan={currentPlan}
                                                    onEdit={() => handleEditPlan(currentPlan)}
                                                    onDelete={() => setIsConfirmingDelete(true)}
                                                />
                                            </AnimatedSection>

                                            <AnimatedSection animation="fadeUp" delay={700}>
                                                <div className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 shadow-xl rounded-lg p-6">
                                                    <div className="flex items-center gap-2 mb-4">
                                                        <CalendarIcon className="w-5 h-5 text-emerald-500" />
                                                        <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Lịch</h3>
                                                    </div>
                                                    <Calendar selected={date} onSelect={setDate} className="rounded-md border-0" />
                                                </div>
                                            </AnimatedSection>
                                        </div>
                                    </div>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </TabsContent>

                    <TabsContent value="streak">
                        {currentPlan && streakData ? (
                            <div className="space-y-8">
                                <AnimatedSection animation="fadeUp" delay={200}>
                                    <div className="text-center space-y-4">
                                        <h1 className="text-4xl lg:text-5xl font-black text-slate-800 dark:text-white">Streak Tự Động</h1>
                                        <p className="text-xl text-slate-600 dark:text-slate-300">
                                            Theo dõi chuỗi ngày thành công được tính tự động
                                        </p>
                                    </div>
                                </AnimatedSection>

                                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                                    <div className="lg:col-span-2 space-y-6">
                                        <AnimatedSection animation="fadeUp" delay={300}>
                                            <StreakTracker
                                                streakData={streakData}
                                                onCheckIn={checkIn}
                                                canCheckIn={canCheckInToday()}
                                                motivationalMessage={getMotivationalMessage()}
                                            />
                                        </AnimatedSection>

                                        <AnimatedSection animation="fadeUp" delay={400}>
                                            <StreakCalendar streakData={streakData} />
                                        </AnimatedSection>
                                    </div>

                                    <div className="space-y-6">
                                        <AnimatedSection animation="fadeUp" delay={500}>
                                            <StreakAchievements achievements={streakData.achievements} />
                                        </AnimatedSection>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="text-center py-12">
                                <h3 className="text-xl font-medium mb-2 text-slate-900 dark:text-white">Chưa có kế hoạch cai thuốc</h3>
                                <p className="text-slate-600 dark:text-slate-300 mb-6">
                                    Bạn cần tạo kế hoạch cai thuốc trước khi theo dõi streak tự động
                                </p>
                                <Button onClick={() => setActiveTab("plan")}>Tạo Kế Hoạch</Button>
                            </div>
                        )}
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    )
}
