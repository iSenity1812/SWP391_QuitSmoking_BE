"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { MessageSquare, FileText, Trophy, Flag, TrendingUp, Users, Star } from "lucide-react"

export function ContentDashboard() {
    const stats = [
        {
            title: "Đánh Giá Chờ Duyệt",
            value: "23",
            change: "+5 hôm nay",
            icon: MessageSquare,
            color: "text-blue-500",
        },
        {
            title: "Nội Dung Mới",
            value: "156",
            change: "+12 tuần này",
            icon: FileText,
            color: "text-green-500",
        },
        {
            title: "Thành Tựu Hoạt Động",
            value: "89",
            change: "3 mới thêm",
            icon: Trophy,
            color: "text-yellow-500",
        },
        {
            title: "Báo Cáo Chờ Xử Lý",
            value: "7",
            change: "Cần xem xét",
            icon: Flag,
            color: "text-red-500",
        },
    ]

    const recentActivity = [
        {
            type: "review",
            message: "Đánh giá mới từ Nguyễn Văn A",
            time: "5 phút trước",
            status: "pending",
        },
        {
            type: "content",
            message: "Video mới được tải lên",
            time: "15 phút trước",
            status: "published",
        },
        {
            type: "achievement",
            message: "Thành tựu 'Tuần đầu tiên' được tạo",
            time: "1 giờ trước",
            status: "active",
        },
        {
            type: "report",
            message: "Báo cáo nội dung không phù hợp",
            time: "2 giờ trước",
            status: "pending",
        },
    ]

    return (
        <div className="space-y-6">
            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {stats.map((stat, index) => (
                    <Card key={index} className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium text-slate-700 dark:text-slate-300">{stat.title}</CardTitle>
                            <stat.icon className={`h-4 w-4 ${stat.color}`} />
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold text-slate-900 dark:text-white">{stat.value}</div>
                            <p className="text-xs text-slate-600 dark:text-slate-400">{stat.change}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Recent Activity */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Hoạt Động Gần Đây</CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Các hoạt động mới nhất trong hệ thống quản lý nội dung
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
                                        className={`w-3 h-3 rounded-full ${activity.status === "pending"
                                            ? "bg-yellow-500"
                                            : activity.status === "published" || activity.status === "active"
                                                ? "bg-green-500"
                                                : "bg-blue-500"
                                            }`}
                                    />
                                    <div>
                                        <p className="font-medium text-slate-900 dark:text-white">{activity.message}</p>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">{activity.time}</p>
                                    </div>
                                </div>
                                <Badge
                                    variant={
                                        activity.status === "pending"
                                            ? "secondary"
                                            : activity.status === "published" || activity.status === "active"
                                                ? "default"
                                                : "outline"
                                    }
                                >
                                    {activity.status === "pending"
                                        ? "Chờ xử lý"
                                        : activity.status === "published"
                                            ? "Đã xuất bản"
                                            : activity.status === "active"
                                                ? "Hoạt động"
                                                : "Mới"}
                                </Badge>
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
                            <span>Xu Hướng Nội Dung</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Video</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">45%</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Bài viết</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">35%</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Hình ảnh</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">20%</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <Users className="w-5 h-5 text-blue-500" />
                            <span>Tương Tác Người Dùng</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Lượt thích</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">2,847</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Bình luận</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">1,234</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-slate-600 dark:text-slate-400">Chia sẻ</span>
                                <span className="text-sm font-medium text-slate-900 dark:text-white">567</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <Star className="w-5 h-5 text-yellow-500" />
                            <span>Đánh Giá Trung Bình</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-center">
                            <div className="text-3xl font-bold text-slate-900 dark:text-white mb-2">4.8</div>
                            <div className="flex justify-center space-x-1 mb-2">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <Star
                                        key={star}
                                        className={`w-4 h-4 ${star <= 4 ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`}
                                    />
                                ))}
                            </div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Từ 1,234 đánh giá</p>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}
