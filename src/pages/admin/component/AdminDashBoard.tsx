"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { TrendingUp, Users, Calendar, Target, Clock, Star, Loader2, BarChart3 } from "lucide-react"
import { adminService, type DashboardStats, type Activity } from "@/pages/admin/component/service/adminService"
import { ChartContainer, LineChartComponent } from "@/components/ui/Chart"
import { useTheme } from "@/context/ThemeContext"

export function AdminDashboard() {
    const [stats, setStats] = useState<DashboardStats | null>(null)
    const [activities, setActivities] = useState<Activity[]>([])
    const [loading, setLoading] = useState(true)
    const { theme } = useTheme()

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                setLoading(true)
                const [statsData, activitiesData] = await Promise.all([
                    adminService.getDashboardStats(),
                    adminService.getRecentActivities(5),
                ])

                setStats(statsData)
                setActivities(activitiesData)
            } catch (error) {
                console.error("Lỗi khi tải dữ liệu dashboard:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchDashboardData()

        // Auto refresh every 30 seconds
        const interval = setInterval(fetchDashboardData, 30000)
        return () => clearInterval(interval)
    }, [])

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="w-8 h-8 animate-spin text-gray-600 dark:text-gray-300" />
                <span className="ml-2 text-gray-600 dark:text-gray-300">Đang tải dữ liệu...</span>
            </div>
        )
    }

    if (!stats) {
        return (
            <div className="text-center py-8">
                <p className="text-gray-600 dark:text-gray-300">Không thể tải dữ liệu dashboard</p>
                <Button onClick={() => window.location.reload()} className="mt-4">
                    Thử lại
                </Button>
            </div>
        )
    }

    // Generate chart data
    const weeklyGrowthData = Array.from({ length: 7 }, (_, i) => ({
        day: `Ngày ${i + 1}`,
        users: Math.floor(Math.random() * 100) + 50,
        plans: Math.floor(Math.random() * 80) + 30,
        success: Math.floor(Math.random() * 60) + 20,
    }))

    const dailyActivityData = Array.from({ length: 24 }, (_, i) => ({
        hour: `${i}h`,
        activity: Math.floor(Math.random() * 200) + 50,
    }))

    // Generate monthly data for each metric
    const monthlyUsersData = Array.from({ length: 12 }, (_, i) => ({
        month: `T${i + 1}`,
        users: Math.floor(Math.random() * 500) + 200 + i * 50, // Growing trend
    }))

    const monthlyPlansData = Array.from({ length: 12 }, (_, i) => ({
        month: `T${i + 1}`,
        plans: Math.floor(Math.random() * 200) + 80 + i * 20, // Growing trend
    }))

    const monthlySuccessData = Array.from({ length: 12 }, (_, i) => ({
        month: `T${i + 1}`,
        rate: Math.floor(Math.random() * 15) + 65 + i * 1, // Improving trend
    }))

    const monthlyRevenueData = Array.from({ length: 12 }, (_, i) => ({
        month: `T${i + 1}`,
        revenue: Math.floor(Math.random() * 2000) + 1000 + i * 300, // Growing trend
    }))

    // Theme-aware colors
    const chartColors = {
        primary: theme === "dark" ? "#60a5fa" : "#3b82f6",
        success: theme === "dark" ? "#34d399" : "#10b981",
        warning: theme === "dark" ? "#fbbf24" : "#f59e0b",
        purple: theme === "dark" ? "#a78bfa" : "#8b5cf6",
        red: theme === "dark" ? "#f87171" : "#ef4444",
    }

    return (
        <div className="space-y-6">
            {/* Monthly Metrics Charts */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Total Users Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            <span>Tổng Người Dùng Theo Tháng</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Tổng năm 2024: {monthlyUsersData.reduce((sum, item) => sum + item.users, 0).toLocaleString()} người dùng
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={monthlyUsersData}
                                xAxis={[{ scaleType: "point", dataKey: "month" }]}
                                series={[{ dataKey: "users", label: "Người dùng", color: chartColors.primary }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Active Plans Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <Target className="w-5 h-5 text-green-600 dark:text-green-400" />
                            <span>Kế Hoạch Hoạt Động Theo Tháng</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Tổng năm 2024: {monthlyPlansData.reduce((sum, item) => sum + item.plans, 0).toLocaleString()} kế hoạch
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={monthlyPlansData}
                                xAxis={[{ scaleType: "point", dataKey: "month" }]}
                                series={[{ dataKey: "plans", label: "Kế hoạch", color: chartColors.success }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Success Rate Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <TrendingUp className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                            <span>Tỷ Lệ Thành Công Theo Tháng</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Trung bình năm 2024: {Math.round(monthlySuccessData.reduce((sum, item) => sum + item.rate, 0) / 12)}%
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={monthlySuccessData}
                                xAxis={[{ scaleType: "point", dataKey: "month" }]}
                                series={[{ dataKey: "rate", label: "Tỷ lệ (%)", color: chartColors.purple }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Revenue Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <Star className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
                            <span>Doanh Thu Theo Tháng</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Tổng năm 2024: ${monthlyRevenueData.reduce((sum, item) => sum + item.revenue, 0).toLocaleString()}
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={monthlyRevenueData}
                                xAxis={[{ scaleType: "point", dataKey: "month" }]}
                                series={[{ dataKey: "revenue", label: "Doanh thu ($)", color: chartColors.warning }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* Charts Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Weekly Growth Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <BarChart3 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            <span>Tăng Trưởng Tuần</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Người dùng, kế hoạch và thành công theo ngày
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={weeklyGrowthData}
                                xAxis={[{ scaleType: "point", dataKey: "day" }]}
                                series={[
                                    { dataKey: "users", label: "Người dùng", color: chartColors.primary },
                                    { dataKey: "plans", label: "Kế hoạch", color: chartColors.success },
                                    { dataKey: "success", label: "Thành công", color: chartColors.warning },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Daily Activity Chart */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <Clock className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                            <span>Hoạt Động Theo Giờ</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Phân bố hoạt động người dùng trong 24 giờ
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChartComponent
                                dataset={dailyActivityData}
                                xAxis={[{ scaleType: "point", dataKey: "hour" }]}
                                series={[{ dataKey: "activity", label: "Hoạt động", color: chartColors.purple }]}
                                width={800}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* Recent Activity */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                        <Clock className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                        <span>Hoạt Động Gần Đây</span>
                    </CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Các hoạt động mới nhất của người dùng và hệ thống
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {activities.map((activity) => (
                            <div
                                key={activity.id}
                                className="flex items-center justify-between p-3 rounded-lg bg-gray-50 dark:bg-gray-700/50"
                            >
                                <div className="flex items-center space-x-3">
                                    <div
                                        className={`w-3 h-3 rounded-full ${activity.type === "success"
                                            ? "bg-green-500"
                                            : activity.type === "warning"
                                                ? "bg-yellow-500"
                                                : activity.type === "error"
                                                    ? "bg-red-500"
                                                    : "bg-blue-500"
                                            }`}
                                    />
                                    <div>
                                        <p className="font-medium text-gray-900 dark:text-gray-100">{activity.user}</p>
                                        <p className="text-sm text-gray-600 dark:text-gray-400">{activity.action}</p>
                                    </div>
                                </div>
                                <span className="text-sm text-gray-500 dark:text-gray-400">{activity.time}</span>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Quick Actions */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="text-gray-900 dark:text-gray-100">Thao Tác Nhanh</CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Các tác vụ quản trị thường dùng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2 text-gray-700 dark:text-gray-300 border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                        >
                            <Users className="w-6 h-6" />
                            <span>Thêm Người Dùng</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2 text-gray-700 dark:text-gray-300 border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                        >
                            <Calendar className="w-6 h-6" />
                            <span>Tạo Kế Hoạch</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2 text-gray-700 dark:text-gray-300 border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                        >
                            <Target className="w-6 h-6" />
                            <span>Xem Báo Cáo</span>
                        </Button>
                        <Button
                            variant="outline"
                            className="h-20 flex flex-col space-y-2 text-gray-700 dark:text-gray-300 border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                        >
                            <Star className="w-6 h-6" />
                            <span>Gửi Thông Báo</span>
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
