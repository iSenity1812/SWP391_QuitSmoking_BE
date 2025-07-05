"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Users, Target, Heart, Calendar, BarChart3 } from "lucide-react"

export function AnalyticsSection() {
    const analyticsData = [
        {
            title: "Tỷ Lệ Thành Công Theo Tháng",
            description: "Phần trăm người dùng hoàn thành kế hoạch cai thuốc",
            data: [
                { month: "T1", success: 68 },
                { month: "T2", success: 72 },
                { month: "T3", success: 75 },
                { month: "T4", success: 78 },
                { month: "T5", success: 73 },
                { month: "T6", success: 80 },
            ],
        },
    ]

    const keyMetrics = [
        {
            title: "Tổng Người Dùng Mới",
            value: "2,847",
            change: "+12%",
            icon: Users,
            color: "text-blue-500",
        },
        {
            title: "Kế Hoạch Hoàn Thành",
            value: "1,234",
            change: "+8%",
            icon: Target,
            color: "text-green-500",
        },
        {
            title: "Tỷ Lệ Thành Công",
            value: "73%",
            change: "+5%",
            icon: Heart,
            color: "text-red-500",
        },
        {
            title: "Thời Gian TB",
            value: "45 ngày",
            change: "-3 ngày",
            icon: Calendar,
            color: "text-purple-500",
        },
    ]

    return (
        <div className="space-y-6">
            {/* Key Metrics */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {keyMetrics.map((metric, index) => (
                    <Card key={index} className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium text-slate-700 dark:text-slate-300">{metric.title}</CardTitle>
                            <metric.icon className={`h-4 w-4 ${metric.color}`} />
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold text-slate-900 dark:text-white">{metric.value}</div>
                            <p className="text-xs text-green-600 dark:text-green-400">{metric.change} so với tháng trước</p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Success Rate Chart */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                        <BarChart3 className="w-5 h-5 text-blue-500" />
                        <span>Tỷ Lệ Thành Công Theo Thời Gian</span>
                    </CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Theo dõi xu hướng thành công của người dùng qua các tháng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {analyticsData[0].data.map((item, index) => (
                            <div key={index} className="flex items-center justify-between">
                                <span className="text-sm font-medium text-slate-700 dark:text-slate-300">{item.month}</span>
                                <div className="flex items-center space-x-3 flex-1 mx-4">
                                    <div className="flex-1 bg-slate-200 dark:bg-slate-600 rounded-full h-2">
                                        <div
                                            className="bg-gradient-to-r from-blue-500 to-green-500 h-2 rounded-full"
                                            style={{ width: `${item.success}%` }}
                                        ></div>
                                    </div>
                                    <span className="text-sm font-semibold text-slate-900 dark:text-white">{item.success}%</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Detailed Analytics */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="text-slate-900 dark:text-white">Phân Tích Theo Độ Tuổi</CardTitle>
                        <CardDescription className="text-slate-600 dark:text-slate-400">
                            Tỷ lệ thành công theo nhóm tuổi
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">18-25 tuổi</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">65%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">26-35 tuổi</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">78%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">36-45 tuổi</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">82%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">46+ tuổi</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">71%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="text-slate-900 dark:text-white">Phân Tích Theo Loại Kế Hoạch</CardTitle>
                        <CardDescription className="text-slate-600 dark:text-slate-400">
                            Hiệu quả của các loại kế hoạch cai thuốc
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Giảm dần</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">75%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Dừng ngay</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">68%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Tùy chỉnh</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">81%</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Có coach</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">89%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}
