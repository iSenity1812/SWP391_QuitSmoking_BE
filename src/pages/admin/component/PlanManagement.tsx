"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Target, Calendar, TrendingUp, Users, CheckCircle, Clock, AlertCircle } from "lucide-react"

export function PlanManagement() {
    const planStats = [
        { label: "Kế Hoạch Đang Hoạt Động", value: 1234, icon: Target, color: "text-green-600" },
        { label: "Kế Hoạch Hoàn Thành", value: 567, icon: CheckCircle, color: "text-blue-600" },
        { label: "Kế Hoạch Quá Hạn", value: 89, icon: AlertCircle, color: "text-red-600" },
        { label: "Tháng Này", value: 234, icon: Calendar, color: "text-purple-600" },
    ]

    const recentPlans = [
        {
            id: 1,
            user: "Nguyễn Văn An",
            planName: "Thử Thách 30 Ngày Cai Thuốc",
            startDate: "01/01/2024",
            targetDate: "31/01/2024",
            progress: 75,
            status: "active",
            daysLeft: 8,
        },
        {
            id: 2,
            user: "Trần Thị Bình",
            planName: "Kế Hoạch Giảm Dần",
            startDate: "15/01/2024",
            targetDate: "15/03/2024",
            progress: 45,
            status: "active",
            daysLeft: 32,
        },
        {
            id: 3,
            user: "Lê Văn Cường",
            planName: "Phương Pháp Cai Ngay",
            startDate: "01/12/2023",
            targetDate: "01/01/2024",
            progress: 100,
            status: "completed",
            daysLeft: 0,
        },
        {
            id: 4,
            user: "Phạm Thị Dung",
            planName: "Chiến Binh Cuối Tuần",
            startDate: "20/01/2024",
            targetDate: "20/02/2024",
            progress: 20,
            status: "at-risk",
            daysLeft: 15,
        },
    ]

    return (
        <div className="space-y-6">
            {/* Plan Statistics */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {planStats.map((stat, index) => (
                    <Card key={index} className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                        <CardContent className="p-6">
                            <div className="flex items-center justify-between">
                                <div>
                                    <p className="text-sm text-gray-600 dark:text-gray-400">{stat.label}</p>
                                    <p className="text-2xl font-bold">{stat.value.toLocaleString()}</p>
                                </div>
                                <stat.icon className={`w-8 h-8 ${stat.color}`} />
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Plan Success Metrics */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2">
                            <TrendingUp className="w-5 h-5 text-green-600" />
                            <span>Tỷ Lệ Thành Công Theo Loại Kế Hoạch</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div>
                            <div className="flex justify-between text-sm mb-2">
                                <span>Phương Pháp Cai Ngay</span>
                                <span>85%</span>
                            </div>
                            <Progress value={85} className="h-2" />
                        </div>
                        <div>
                            <div className="flex justify-between text-sm mb-2">
                                <span>Giảm Dần</span>
                                <span>72%</span>
                            </div>
                            <Progress value={72} className="h-2" />
                        </div>
                        <div>
                            <div className="flex justify-between text-sm mb-2">
                                <span>Thử Thách 30 Ngày</span>
                                <span>68%</span>
                            </div>
                            <Progress value={68} className="h-2" />
                        </div>
                        <div>
                            <div className="flex justify-between text-sm mb-2">
                                <span>Chiến Binh Cuối Tuần</span>
                                <span>45%</span>
                            </div>
                            <Progress value={45} className="h-2" />
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2">
                            <Users className="w-5 h-5 text-blue-600" />
                            <span>Phân Bố Kế Hoạch</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="flex justify-between items-center">
                            <span className="text-sm">Thử Thách 30 Ngày</span>
                            <div className="flex items-center space-x-2">
                                <Badge variant="outline">456 người dùng</Badge>
                                <span className="text-sm text-gray-500">37%</span>
                            </div>
                        </div>
                        <div className="flex justify-between items-center">
                            <span className="text-sm">Giảm Dần</span>
                            <div className="flex items-center space-x-2">
                                <Badge variant="outline">334 người dùng</Badge>
                                <span className="text-sm text-gray-500">27%</span>
                            </div>
                        </div>
                        <div className="flex justify-between items-center">
                            <span className="text-sm">Cai Ngay</span>
                            <div className="flex items-center space-x-2">
                                <Badge variant="outline">278 người dùng</Badge>
                                <span className="text-sm text-gray-500">23%</span>
                            </div>
                        </div>
                        <div className="flex justify-between items-center">
                            <span className="text-sm">Chiến Binh Cuối Tuần</span>
                            <div className="flex items-center space-x-2">
                                <Badge variant="outline">166 người dùng</Badge>
                                <span className="text-sm text-gray-500">13%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Recent Plans */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                        <Calendar className="w-5 h-5 text-purple-600" />
                        <span>Kế Hoạch Gần Đây</span>
                    </CardTitle>
                    <CardDescription>Giám sát các kế hoạch cai thuốc đang hoạt động và gần đây</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {recentPlans.map((plan) => (
                            <div key={plan.id} className="p-4 rounded-lg border bg-gray-50 dark:bg-gray-700/50">
                                <div className="flex items-center justify-between mb-3">
                                    <div>
                                        <h3 className="font-medium">{plan.planName}</h3>
                                        <p className="text-sm text-gray-600 dark:text-gray-400">bởi {plan.user}</p>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                        <Badge
                                            variant={
                                                plan.status === "completed"
                                                    ? "default"
                                                    : plan.status === "at-risk"
                                                        ? "destructive"
                                                        : "secondary"
                                            }
                                        >
                                            {plan.status === "completed"
                                                ? "Hoàn thành"
                                                : plan.status === "at-risk"
                                                    ? "Có Rủi Ro"
                                                    : "Đang hoạt động"}
                                        </Badge>
                                        {plan.daysLeft > 0 && (
                                            <Badge variant="outline">
                                                <Clock className="w-3 h-3 mr-1" />
                                                {plan.daysLeft} ngày còn lại
                                            </Badge>
                                        )}
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <div className="flex justify-between text-sm">
                                        <span>Tiến độ</span>
                                        <span>{plan.progress}%</span>
                                    </div>
                                    <Progress value={plan.progress} className="h-2" />
                                    <div className="flex justify-between text-xs text-gray-500">
                                        <span>Bắt đầu: {plan.startDate}</span>
                                        <span>Mục tiêu: {plan.targetDate}</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Quick Actions */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle>Thao Tác Quản Lý Kế Hoạch</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <Button variant="outline" className="h-16 flex flex-col space-y-1">
                            <Target className="w-5 h-5" />
                            <span className="text-sm">Tạo Mẫu Kế Hoạch</span>
                        </Button>
                        <Button variant="outline" className="h-16 flex flex-col space-y-1">
                            <TrendingUp className="w-5 h-5" />
                            <span className="text-sm">Xem Phân Tích</span>
                        </Button>
                        <Button variant="outline" className="h-16 flex flex-col space-y-1">
                            <AlertCircle className="w-5 h-5" />
                            <span className="text-sm">Kế Hoạch Có Rủi Ro</span>
                        </Button>
                        <Button variant="outline" className="h-16 flex flex-col space-y-1">
                            <CheckCircle className="w-5 h-5" />
                            <span className="text-sm">Câu Chuyện Thành Công</span>
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
