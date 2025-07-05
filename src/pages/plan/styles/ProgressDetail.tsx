"use client"

import type React from "react"
import { Card, CardContent } from "@/components/ui/card"
import { Heart, TrendingUp, Zap, Award } from "lucide-react"
import { AnimatedSection } from "@/components/ui/AnimatedSection"

interface ProgressDetailsProps {
    smokeFreeDays: number
    moneySaved: number
    cigarettesNotSmoked: number
}

export const ProgressDetails: React.FC<ProgressDetailsProps> = ({ smokeFreeDays, moneySaved, cigarettesNotSmoked }) => {
    // Calculate health improvements based on smoke-free days
    const heartHealth = Math.min(smokeFreeDays * 0.2, 15).toFixed(1)
    const lungFunction = Math.min(smokeFreeDays * 0.3, 20).toFixed(1)
    const energyLevel = Math.min(smokeFreeDays * 0.4, 25).toFixed(1)

    // Calculate milestones achieved
    const milestonesAchieved = Math.floor(smokeFreeDays / 7) + (smokeFreeDays >= 1 ? 1 : 0)

    return (
        <div className="space-y-8">
            {/* Header */}
            <AnimatedSection animation="fadeUp" delay={100}>
                <div className="text-center">
                    <h2 className="text-3xl font-bold text-slate-800 dark:text-white">Chi Tiết Tiến Trình</h2>
                    <p className="text-slate-600 dark:text-slate-300 mt-2">Xem chi tiết về hành trình cai thuốc lá của bạn</p>
                </div>
            </AnimatedSection>

            {/* Health Metrics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {/* Heart Health */}
                <AnimatedSection animation="fadeUp" delay={200}>
                    <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border border-slate-200 dark:border-slate-700 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                        <CardContent className="p-6 text-center">
                            <div className="flex justify-center mb-4">
                                <div className="w-12 h-12 rounded-full bg-green-100 dark:bg-green-900/30 flex items-center justify-center animate-pulse">
                                    <Heart className="w-6 h-6 text-green-500 dark:text-green-400" />
                                </div>
                            </div>
                            <h3 className="font-semibold text-slate-800 dark:text-slate-200 mb-3">Sức Khỏe Tim Mạch</h3>
                            <div className="space-y-1">
                                <span className="text-2xl font-bold text-green-500 dark:text-green-400">+{heartHealth}%</span>
                                <p className="text-sm text-slate-500 dark:text-slate-400">Cải thiện</p>
                            </div>
                        </CardContent>
                    </Card>
                </AnimatedSection>

                {/* Lung Function */}
                <AnimatedSection animation="fadeUp" delay={300}>
                    <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border border-slate-200 dark:border-slate-700 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                        <CardContent className="p-6 text-center">
                            <div className="flex justify-center mb-4">
                                <div className="w-12 h-12 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center animate-pulse">
                                    <TrendingUp className="w-6 h-6 text-blue-500 dark:text-blue-400" />
                                </div>
                            </div>
                            <h3 className="font-semibold text-slate-800 dark:text-slate-200 mb-3">Chức Năng Phổi</h3>
                            <div className="space-y-1">
                                <span className="text-2xl font-bold text-blue-500 dark:text-blue-400">+{lungFunction}%</span>
                                <p className="text-sm text-slate-500 dark:text-slate-400">Phục hồi</p>
                            </div>
                        </CardContent>
                    </Card>
                </AnimatedSection>

                {/* Energy Level */}
                <AnimatedSection animation="fadeUp" delay={400}>
                    <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border border-slate-200 dark:border-slate-700 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                        <CardContent className="p-6 text-center">
                            <div className="flex justify-center mb-4">
                                <div className="w-12 h-12 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center animate-pulse">
                                    <Zap className="w-6 h-6 text-purple-500 dark:text-purple-400" />
                                </div>
                            </div>
                            <h3 className="font-semibold text-slate-800 dark:text-slate-200 mb-3">Năng Lượng</h3>
                            <div className="space-y-1">
                                <span className="text-2xl font-bold text-purple-500 dark:text-purple-400">+{energyLevel}%</span>
                                <p className="text-sm text-slate-500 dark:text-slate-400">Tăng cường</p>
                            </div>
                        </CardContent>
                    </Card>
                </AnimatedSection>

                {/* Achievements */}
                <AnimatedSection animation="fadeUp" delay={500}>
                    <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border border-slate-200 dark:border-slate-700 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                        <CardContent className="p-6 text-center">
                            <div className="flex justify-center mb-4">
                                <div className="w-12 h-12 rounded-full bg-amber-100 dark:bg-amber-900/30 flex items-center justify-center animate-bounce">
                                    <Award className="w-6 h-6 text-amber-500 dark:text-amber-400" />
                                </div>
                            </div>
                            <h3 className="font-semibold text-slate-800 dark:text-slate-200 mb-3">Thành Tựu</h3>
                            <div className="space-y-1">
                                <span className="text-2xl font-bold text-amber-500 dark:text-amber-400">{milestonesAchieved}</span>
                                <p className="text-sm text-slate-500 dark:text-slate-400">Cột mốc đạt được</p>
                            </div>
                        </CardContent>
                    </Card>
                </AnimatedSection>
            </div>

            {/* Progress Chart Section */}
            <AnimatedSection animation="fadeUp" delay={600}>
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border border-slate-200 dark:border-slate-700 shadow-lg">
                    <CardContent className="p-6">
                        <h3 className="font-semibold text-slate-800 dark:text-slate-200 mb-2">Biểu Đồ Tiến Trình</h3>
                        <p className="text-sm text-slate-500 dark:text-slate-400 mb-6">
                            Theo dõi hành trình cai thuốc theo thời gian
                        </p>

                        {/* Animated Progress Bar */}
                        <div className="relative h-4 bg-slate-100 dark:bg-slate-700 rounded-full w-full mb-8 overflow-hidden">
                            <div
                                className="absolute top-0 left-0 h-4 bg-gradient-to-r from-emerald-500 via-emerald-400 to-emerald-300 rounded-full transition-all duration-2000 ease-out"
                                style={{
                                    width: `${Math.min(smokeFreeDays * 3, 100)}%`,
                                }}
                            >
                                {/* Animated shine effect */}
                                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent animate-pulse"></div>
                            </div>

                            {/* Check mark animation */}
                            {smokeFreeDays > 0 && (
                                <div
                                    className="absolute top-1/2 transform -translate-y-1/2 w-6 h-6 bg-emerald-600 rounded-full flex items-center justify-center transition-all duration-1000"
                                    style={{ left: `${Math.max(Math.min(smokeFreeDays * 3, 100) - 3, 0)}%` }}
                                >
                                    <svg
                                        className="w-3 h-3 text-white animate-bounce"
                                        fill="none"
                                        stroke="currentColor"
                                        viewBox="0 0 24 24"
                                    >
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
                                    </svg>
                                </div>
                            )}
                        </div>

                        {/* Stats Grid */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="text-center md:text-left">
                                <h4 className="text-sm font-medium text-slate-500 dark:text-slate-400 mb-2">Điếu thuốc không hút</h4>
                                <p className="text-2xl font-bold text-slate-800 dark:text-slate-200">
                                    {cigarettesNotSmoked.toLocaleString("vi-VN")} điếu
                                </p>
                            </div>
                            <div className="text-center md:text-left">
                                <h4 className="text-sm font-medium text-slate-500 dark:text-slate-400 mb-2">Tiền tiết kiệm</h4>
                                <p className="text-2xl font-bold text-slate-800 dark:text-slate-200">
                                    {moneySaved.toLocaleString("vi-VN")} VNĐ
                                </p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </AnimatedSection>
        </div>
    )
}
