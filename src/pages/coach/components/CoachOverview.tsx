"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { TrendingUp, Activity } from "lucide-react"

export function CoachOverview() {
    // const upcomingAppointments = [
    // ]

    const recentActivities = [
        {
            id: 1,
            client: "Nguyễn Văn An",
            action: "Hoàn thành mốc 7 ngày không hút thuốc",
            time: "2 giờ trước",
            type: "success",
        },
        {
            id: 2,
            client: "Trần Thị Bình",
            action: "Yêu cầu hỗ trợ khẩn cấp",
            time: "4 giờ trước",
            type: "urgent",
        },
        {
            id: 3,
            client: "Lê Văn Cường",
            action: "Cập nhật nhật ký hàng ngày",
            time: "6 giờ trước",
            type: "info",
        },
        {
            id: 4,
            client: "Phạm Thị Dung",
            action: "Bỏ lỡ cuộc hẹn tư vấn",
            time: "1 ngày trước",
            type: "warning",
        },
    ]

    return (
        <div className="space-y-6">
            <div className="max-w-2xl mx-auto">
                {/* Recent Activities */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                            <Activity className="w-5 h-5 text-green-500" />
                            <span>Hoạt Động Gần Đây</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {recentActivities.map((activity) => (
                                <div key={activity.id} className="flex items-start space-x-3">
                                    <div
                                        className={`w-2 h-2 rounded-full mt-2 ${activity.type === "success"
                                            ? "bg-green-500"
                                            : activity.type === "urgent"
                                                ? "bg-red-500"
                                                : activity.type === "warning"
                                                    ? "bg-yellow-500"
                                                    : "bg-blue-500"
                                            }`}
                                    />
                                    <div className="flex-1">
                                        <p className="text-sm font-medium text-slate-900 dark:text-white">{activity.client}</p>
                                        <p className="text-sm text-slate-600 dark:text-slate-400">{activity.action}</p>
                                        <p className="text-xs text-slate-500 dark:text-slate-500">{activity.time}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <Button className="w-full mt-4" variant="outline">
                            Xem Tất Cả Hoạt Động
                        </Button>
                    </CardContent>
                </Card>
            </div>

            {/* Performance Summary */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-slate-900 dark:text-white">
                        <TrendingUp className="w-5 h-5 text-purple-500" />
                        <span>Tóm Tắt Hiệu Suất Tuần Này</span>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                        <div className="text-center">
                            <div className="text-2xl font-bold text-green-600 dark:text-green-400">23</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Cuộc tư vấn</p>
                        </div>
                        <div className="text-center">
                            <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">89%</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Tỷ lệ tham gia</p>
                        </div>
                        <div className="text-center">
                            <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">15</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Khách hàng thành công</p>
                        </div>
                        <div className="text-center">
                            <div className="text-2xl font-bold text-orange-600 dark:text-orange-400">4.8</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Đánh giá trung bình</p>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
