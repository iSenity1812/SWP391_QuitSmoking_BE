"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Users, UserPlus, Search, Filter, BarChart3, TrendingUp, Loader2 } from "lucide-react"
import { adminService, type User } from "@/pages/admin/component/service/adminService"
import { ChartContainer, LineChart } from "@/components/ui/Chart"
import { useTheme } from "@/context/ThemeContext"

export function UserManagement() {
    const [users, setUsers] = useState<User[]>([])
    const [loading, setLoading] = useState(true)
    const [currentPage, setCurrentPage] = useState(1)
    const { theme } = useTheme()

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setLoading(true)
                const userData = await adminService.getUsers(currentPage, 10)
                setUsers(userData)
            } catch (error) {
                console.error("Lỗi khi tải dữ liệu người dùng:", error)
            } finally {
                setLoading(false)
            }
        }

        fetchUsers()
    }, [currentPage])

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="w-8 h-8 animate-spin" />
                <span className="ml-2">Đang tải dữ liệu người dùng...</span>
            </div>
        )
    }

    const chartColors = {
        primary: theme === "dark" ? "#60a5fa" : "#3b82f6",
        success: theme === "dark" ? "#34d399" : "#10b981",
        warning: theme === "dark" ? "#fbbf24" : "#f59e0b",
        purple: theme === "dark" ? "#a78bfa" : "#8b5cf6",
        red: theme === "dark" ? "#f87171" : "#ef4444",
    }

    // Generate chart data
    const userRegistrationData = Array.from({ length: 7 }, (_, i) => ({
        day: `Ngày ${i + 1}`,
        registrations: Math.floor(Math.random() * 50) + 20,
        active: Math.floor(Math.random() * 40) + 15,
        premium: Math.floor(Math.random() * 15) + 5,
    }))

    const userStatusData = [
        { status: "Hoạt động", count: users.filter((u) => u.status === "active").length * 10 },
        { status: "Không hoạt động", count: users.filter((u) => u.status === "inactive").length * 5 },
    ]

    const userActivityData = Array.from({ length: 12 }, (_, i) => ({
        month: `T${i + 1}`,
        newUsers: Math.floor(Math.random() * 100) + 50,
        activeUsers: Math.floor(Math.random() * 200) + 100,
        churnRate: Math.floor(Math.random() * 10) + 5,
    }))

    return (
        <div className="space-y-6">
            {/* Charts Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* User Registration Trends */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <BarChart3 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            <span>Xu Hướng Đăng Ký</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Đăng ký và hoạt động người dùng theo ngày
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={userRegistrationData}
                                xAxis={[{ scaleType: "point", dataKey: "day" }]}
                                series={[
                                    { dataKey: "registrations", label: "Đăng ký", color: chartColors.primary },
                                    { dataKey: "active", label: "Hoạt động", color: chartColors.success },
                                    { dataKey: "premium", label: "Premium", color: chartColors.warning },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* User Status Distribution */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <Users className="w-5 h-5 text-green-600 dark:text-green-400" />
                            <span>Phân Bố Trạng Thái</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Tình trạng hoạt động của người dùng
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={userStatusData}
                                xAxis={[{ scaleType: "point", dataKey: "status" }]}
                                series={[{ dataKey: "count", label: "Số lượng", color: chartColors.success }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* Monthly User Activity */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                        <TrendingUp className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                        <span>Hoạt Động Người Dùng Hàng Tháng</span>
                    </CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Người dùng mới, hoạt động và tỷ lệ rời bỏ theo tháng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <ChartContainer className="h-[400px]">
                        <LineChart
                            dataset={userActivityData}
                            xAxis={[{ scaleType: "point", dataKey: "month" }]}
                            series={[
                                { dataKey: "newUsers", label: "Người dùng mới", color: chartColors.primary },
                                { dataKey: "activeUsers", label: "Người dùng hoạt động", color: chartColors.success },
                                { dataKey: "churnRate", label: "Tỷ lệ rời bỏ (%)", color: chartColors.red },
                            ]}
                            width={800}
                            height={400}
                        />
                    </ChartContainer>
                </CardContent>
            </Card>

            {/* User Management Actions */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between text-gray-900 dark:text-gray-100">
                        <span>Quản Lý Người Dùng</span>
                        <div className="flex space-x-2">
                            <Button size="sm" variant="outline">
                                <Search className="w-4 h-4 mr-2" />
                                Tìm kiếm
                            </Button>
                            <Button size="sm" variant="outline">
                                <Filter className="w-4 h-4 mr-2" />
                                Lọc
                            </Button>
                            <Button size="sm">
                                <UserPlus className="w-4 h-4 mr-2" />
                                Thêm Người Dùng
                            </Button>
                        </div>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {users.map((user) => (
                            <div
                                key={user.id}
                                className="flex items-center justify-between p-4 rounded-lg bg-gray-50 dark:bg-gray-700/50"
                            >
                                <div className="flex items-center space-x-4">
                                    <Avatar>
                                        <AvatarImage src={user.avatar || "/placeholder.svg"} />
                                        <AvatarFallback>{user.name.charAt(0)}</AvatarFallback>
                                    </Avatar>
                                    <div>
                                        <p className="font-medium text-gray-900 dark:text-gray-100">{user.name}</p>
                                        <p className="text-sm text-gray-600 dark:text-gray-400">{user.email}</p>
                                        <p className="text-xs text-gray-500">{user.phone}</p>
                                    </div>
                                </div>
                                <div className="flex items-center space-x-4">
                                    <Badge variant={user.status === "active" ? "default" : "secondary"}>
                                        {user.status === "active" ? "Hoạt động" : "Không hoạt động"}
                                    </Badge>
                                    <Badge variant="outline">{user.plan}</Badge>
                                    <div className="text-right text-sm">
                                        <p>Tham gia: {user.joinDate}</p>
                                        <p className="text-gray-500">Hoạt động: {user.lastActive}</p>
                                    </div>
                                    <Button size="sm" variant="outline">
                                        Chi tiết
                                    </Button>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="flex justify-center mt-6">
                        <div className="flex space-x-2">
                            <Button
                                variant="outline"
                                onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                                disabled={currentPage === 1}
                            >
                                Trước
                            </Button>
                            <Button variant="outline">{currentPage}</Button>
                            <Button variant="outline" onClick={() => setCurrentPage(currentPage + 1)}>
                                Sau
                            </Button>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
