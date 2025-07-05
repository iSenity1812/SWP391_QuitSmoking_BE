"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, UserCheck, Activity } from "lucide-react"

export function StaffDashboard() {
    const recentActivity = [
        {
            type: "user",
            message: "Người dùng mới đăng ký: Nguyễn Văn A",
            time: "5 phút trước",
            status: "new",
        },
        {
            type: "plan",
            message: "Kế hoạch mới được tạo bởi Trần Thị B",
            time: "15 phút trước",
            status: "active",
        },
        {
            type: "coach",
            message: "Coach Dr. Nguyễn C hoàn thành buổi tư vấn",
            time: "1 giờ trước",
            status: "completed",
        },
        {
            type: "success",
            message: "Người dùng Lê Văn D đạt mốc 30 ngày",
            time: "2 giờ trước",
            status: "milestone",
        },
    ]

    return (
        <div className="space-y-6">
            {/* Recent Activity */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Hoạt Động Gần Đây</CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Các hoạt động mới nhất trong hệ thống quản lý nhân sự
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {recentActivity.map((activity, index) => (
                            <div
                                key={index}
                                className="flex items-center justify-between p-3 rounded-lg bg-slate-50 dark:bg-slate-700/50"
                            >
                                <div className="flex items-center space-x-3">
                                    <div
                                        className={`w-3 h-3 rounded-full ${activity.status === "new"
                                                ? "bg-blue-500"
                                                : activity.status === "active"
                                                    ? "bg-green-500"
                                                    : activity.status === "completed"
                                                        ? "bg-purple-500"
                                                        : "bg-yellow-500"
                                            }`}
                                    />
                                    <div>
                                        <p className="font-medium text-slate-900 dark:text-white">{activity.message}</p>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">{activity.time}</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Quick Stats */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <TrendingUp className="w-5 h-5 text-green-500" />
                            <span>Xu Hướng Người Dùng</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Đăng ký mới</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">+15%</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Hoạt động</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">+8%</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Thành công</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">+12%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <UserCheck className="w-5 h-5 text-blue-500" />
                            <span>Hiệu Suất Coach</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Buổi tư vấn</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">234</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Đánh giá TB</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">4.8/5</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Tỷ lệ thành công</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">78%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <Activity className="w-5 h-5 text-purple-500" />
                            <span>Hoạt Động Hệ Thống</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Uptime</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">99.9%</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Phản hồi TB</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">120ms</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Lỗi hệ thống</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">0.1%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}
