"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Calendar, Search, Filter, Edit, TrendingUp, Users, CheckCircle } from "lucide-react"

interface Plan {
    id: number
    userId: number
    userName: string
    userAvatar?: string
    planType: "gradual" | "immediate" | "custom"
    startDate: string
    endDate: string
    status: "active" | "completed" | "paused" | "failed"
    progress: number
    currentStreak: number
    targetDays: number
    dailyCigarettes: number
    originalAmount: number
    coachId?: number
    coachName?: string
}

export function PlanManagement() {
    const [plans] = useState<Plan[]>([
        {
            id: 1,
            userId: 1,
            userName: "Nguyễn Văn A",
            userAvatar: "/placeholder.svg?height=40&width=40",
            planType: "gradual",
            startDate: "2024-01-01T00:00:00Z",
            endDate: "2024-03-01T00:00:00Z",
            status: "active",
            progress: 65,
            currentStreak: 25,
            targetDays: 60,
            dailyCigarettes: 5,
            originalAmount: 20,
            coachId: 1,
            coachName: "Dr. Nguyễn Văn C",
        },
        {
            id: 2,
            userId: 2,
            userName: "Trần Thị B",
            planType: "immediate",
            startDate: "2024-01-15T00:00:00Z",
            endDate: "2024-04-15T00:00:00Z",
            status: "completed",
            progress: 100,
            currentStreak: 90,
            targetDays: 90,
            dailyCigarettes: 0,
            originalAmount: 15,
        },
    ])

    const getStatusColor = (status: string) => {
        switch (status) {
            case "active":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "completed":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "paused":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "failed":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getPlanTypeColor = (type: string) => {
        switch (type) {
            case "gradual":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            case "immediate":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "custom":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex justify-between items-center">
                        <div>
                            <CardTitle className="text-slate-900 dark:text-white">Quản Lý Kế Hoạch Cai Thuốc</CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Theo dõi và quản lý kế hoạch cai thuốc của người dùng
                            </CardDescription>
                        </div>
                        <div className="flex space-x-2">
                            <Button variant="outline" size="sm">
                                <Filter className="w-4 h-4 mr-2" />
                                Lọc
                            </Button>
                            <Button variant="outline" size="sm">
                                <Search className="w-4 h-4 mr-2" />
                                Tìm kiếm
                            </Button>
                        </div>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {plans.map((plan) => (
                            <div
                                key={plan.id}
                                className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                            >
                                <div className="flex items-start justify-between">
                                    <div className="flex items-start space-x-4 flex-1">
                                        <Avatar className="h-12 w-12">
                                            <AvatarImage src={plan.userAvatar || "/placeholder.svg"} />
                                            <AvatarFallback>{plan.userName.charAt(0)}</AvatarFallback>
                                        </Avatar>
                                        <div className="flex-1">
                                            <div className="flex items-center space-x-2 mb-2">
                                                <h4 className="font-medium text-slate-900 dark:text-white">{plan.userName}</h4>
                                                <Badge className={getStatusColor(plan.status)}>
                                                    {plan.status === "active"
                                                        ? "Đang thực hiện"
                                                        : plan.status === "completed"
                                                            ? "Hoàn thành"
                                                            : plan.status === "paused"
                                                                ? "Tạm dừng"
                                                                : "Thất bại"}
                                                </Badge>
                                                <Badge className={getPlanTypeColor(plan.planType)}>
                                                    {plan.planType === "gradual"
                                                        ? "Giảm dần"
                                                        : plan.planType === "immediate"
                                                            ? "Dừng ngay"
                                                            : "Tùy chỉnh"}
                                                </Badge>
                                            </div>

                                            <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-3">
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">{plan.progress}%</div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Tiến độ</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                        {plan.currentStreak}
                                                    </div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Ngày hiện tại</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">{plan.targetDays}</div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Mục tiêu</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                        {plan.dailyCigarettes}
                                                    </div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Điếu/ngày</div>
                                                </div>
                                                <div className="text-center p-2 bg-white dark:bg-slate-600/50 rounded">
                                                    <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                        {plan.originalAmount}
                                                    </div>
                                                    <div className="text-xs text-slate-500 dark:text-slate-400">Ban đầu</div>
                                                </div>
                                            </div>

                                            {/* Progress Bar */}
                                            <div className="mb-3">
                                                <div className="flex justify-between text-sm mb-1">
                                                    <span className="text-slate-600 dark:text-slate-400">Tiến độ hoàn thành</span>
                                                    <span className="text-slate-900 dark:text-white font-medium">{plan.progress}%</span>
                                                </div>
                                                <div className="w-full bg-slate-200 dark:bg-slate-600 rounded-full h-2">
                                                    <div
                                                        className={`h-2 rounded-full ${plan.status === "completed"
                                                                ? "bg-green-500"
                                                                : plan.status === "active"
                                                                    ? "bg-blue-500"
                                                                    : plan.status === "paused"
                                                                        ? "bg-yellow-500"
                                                                        : "bg-red-500"
                                                            }`}
                                                        style={{ width: `${plan.progress}%` }}
                                                    ></div>
                                                </div>
                                            </div>

                                            <div className="flex items-center justify-between text-sm text-slate-500 dark:text-slate-400">
                                                <div className="flex items-center space-x-4">
                                                    <span className="flex items-center">
                                                        <Calendar className="w-3 h-3 mr-1" />
                                                        {new Date(plan.startDate).toLocaleDateString("vi-VN")} -{" "}
                                                        {new Date(plan.endDate).toLocaleDateString("vi-VN")}
                                                    </span>
                                                    {plan.coachName && (
                                                        <span className="flex items-center">
                                                            <Users className="w-3 h-3 mr-1" />
                                                            Coach: {plan.coachName}
                                                        </span>
                                                    )}
                                                </div>
                                                {plan.status === "completed" && (
                                                    <span className="flex items-center text-green-600 dark:text-green-400">
                                                        <CheckCircle className="w-3 h-3 mr-1" />
                                                        Thành công
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex space-x-2">
                                        <Button size="sm" variant="ghost">
                                            <TrendingUp className="w-4 h-4" />
                                        </Button>
                                        <Button size="sm" variant="ghost">
                                            <Edit className="w-4 h-4" />
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
