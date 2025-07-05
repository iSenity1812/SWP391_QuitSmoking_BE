"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { TrendingUp, BarChart3, PieChart, Activity, Loader2 } from "lucide-react"
import { adminService } from "@/pages/admin/component/service/adminService"
import { ChartContainer, LineChart } from "@/components/ui/Chart"
import { useTheme } from "@/context/ThemeContext"

interface AnalyticsData {
    monthlyActiveUsers: number
    successRate: number
    monthlyRevenue: number
    averageSessionTime: number
    monthlyData: Array<{
        month: string
        users: number
        plans: number
        revenue: number
    }>
    successMetrics: Array<{
        metric: string
        value: number
        target: number
        color: string
    }>
}

export function AnalyticsSection() {
    const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null)
    const [loading, setLoading] = useState(true)
    const { theme } = useTheme()

    useEffect(() => {
        const fetchAnalytics = async () => {
            try {
                setLoading(true)
                const data = await adminService.getAnalyticsData()
                setAnalyticsData(data)
            } catch (error) {
                console.error("Lỗi khi tải dữ liệu phân tích:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchAnalytics()

        // Auto refresh every 2 minutes
        const interval = setInterval(fetchAnalytics, 120000)
        return () => clearInterval(interval)
    }, [])

    const chartColors = {
        primary: theme === "dark" ? "#60a5fa" : "#3b82f6",
        success: theme === "dark" ? "#34d399" : "#10b981",
        warning: theme === "dark" ? "#fbbf24" : "#f59e0b",
        purple: theme === "dark" ? "#a78bfa" : "#8b5cf6",
        red: theme === "dark" ? "#f87171" : "#ef4444",
    }

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="w-8 h-8 animate-spin" />
                <span className="ml-2 text-gray-900 dark:text-gray-100">Đang tải dữ liệu phân tích...</span>
            </div>
        )
    }

    if (!analyticsData) {
        return (
            <div className="text-center py-8">
                <p className="text-gray-900 dark:text-gray-100">Không thể tải dữ liệu phân tích</p>
            </div>
        )
    }

    const topPerformingContent = [
        {
            title: "10 Mẹo Cho Tuần Đầu Tiên",
            views: Math.floor(Math.random() * 5000) + 10000,
            engagement: Math.floor(Math.random() * 20) + 80,
        },
        {
            title: "Hiểu Về Cơn Thèm",
            views: Math.floor(Math.random() * 4000) + 8000,
            engagement: Math.floor(Math.random() * 15) + 70,
        },
        {
            title: "Lịch Trình Lợi Ích Sức Khỏe",
            views: Math.floor(Math.random() * 3000) + 7000,
            engagement: Math.floor(Math.random() * 18) + 75,
        },
        {
            title: "Đối Phó Với Cơn Thèm",
            views: Math.floor(Math.random() * 2500) + 6000,
            engagement: Math.floor(Math.random() * 12) + 65,
        },
    ]

    return (
        <div className="space-y-6">
            {/* Success Metrics */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                        <BarChart3 className="w-5 h-5 text-green-600 dark:text-green-400" />
                        <span>Chỉ Số Thành Công</span>
                    </CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Các chỉ số hiệu suất chính và mục tiêu
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {analyticsData.successMetrics.map((metric, index) => (
                            <div key={index} className="space-y-2">
                                <div className="flex justify-between items-center">
                                    <span className="text-sm font-medium text-gray-900 dark:text-gray-100">{metric.metric}</span>
                                    <div className="flex items-center space-x-2">
                                        <span className="text-sm text-gray-900 dark:text-gray-100">{metric.value}%</span>
                                        <Badge variant={metric.value >= metric.target ? "default" : "secondary"}>
                                            Mục tiêu: {metric.target}%
                                        </Badge>
                                    </div>
                                </div>
                                <Progress value={metric.value} className="h-2" />
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Monthly Growth Chart */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <TrendingUp className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            <span>Tăng Trưởng Hàng Tháng</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={analyticsData.monthlyData}
                                xAxis={[{ scaleType: "point", dataKey: "month" }]}
                                series={[
                                    { dataKey: "users", label: "Người dùng", color: chartColors.primary },
                                    { dataKey: "plans", label: "Kế hoạch", color: chartColors.success },
                                    { dataKey: "revenue", label: "Doanh thu", color: chartColors.warning },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <PieChart className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                            <span>Nội Dung Hiệu Suất Cao</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={topPerformingContent}
                                xAxis={[{ scaleType: "point", dataKey: "title" }]}
                                series={[
                                    { dataKey: "views", label: "Lượt xem", color: chartColors.purple },
                                    { dataKey: "engagement", label: "Tương tác (%)", color: chartColors.warning },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* User Behavior Analytics */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                        <Activity className="w-5 h-5 text-red-600 dark:text-red-400" />
                        <span>Thông Tin Hành Vi Người Dùng</span>
                    </CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Hiểu cách người dùng tương tác với nền tảng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="text-center p-4 rounded-lg bg-gray-50 dark:bg-gray-700/50">
                            <h3 className="font-medium mb-2 text-gray-900 dark:text-gray-100">Giờ Cao Điểm</h3>
                            <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">19-21h</p>
                            <p className="text-sm text-gray-600 dark:text-gray-400">Thời gian hoạt động nhất</p>
                        </div>
                        <div className="text-center p-4 rounded-lg bg-gray-50 dark:bg-gray-700/50">
                            <h3 className="font-medium mb-2 text-gray-900 dark:text-gray-100">Thời Gian Kế Hoạch Trung Bình</h3>
                            <p className="text-2xl font-bold text-green-600 dark:text-green-400">
                                {Math.floor(Math.random() * 20) + 35} ngày
                            </p>
                            <p className="text-sm text-gray-600 dark:text-gray-400">Từ bắt đầu đến hoàn thành</p>
                        </div>
                        <div className="text-center p-4 rounded-lg bg-gray-50 dark:bg-gray-700/50">
                            <h3 className="font-medium mb-2 text-gray-900 dark:text-gray-100">Yêu Cầu Hỗ Trợ</h3>
                            <p className="text-2xl font-bold text-yellow-600 dark:text-yellow-400">
                                {Math.floor(Math.random() * 8) + 8}%
                            </p>
                            <p className="text-sm text-gray-600 dark:text-gray-400">Của người dùng hoạt động</p>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
