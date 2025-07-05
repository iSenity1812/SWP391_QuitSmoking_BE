"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Calendar, Cigarette, DollarSign, TrendingUp } from "lucide-react"
import { userService } from "@/services/userService"
import type { UserQuitStatsResponse } from "@/services/userService"
import { toast } from "react-toastify"

export function QuitStatsSection() {
    const [stats, setStats] = useState<UserQuitStatsResponse | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        loadQuitStats()
    }, [])

    const loadQuitStats = async () => {
        try {
            setLoading(true)
            const response = await userService.getQuitStats()
            setStats(response.data)
        } catch (error: any) {
            console.error("Error loading quit stats:", error)
            const errorMessage = error?.response?.data?.message || "Không thể tải thống kê cai thuốc"
            toast.error(errorMessage)
        } finally {
            setLoading(false)
        }
    }

    const formatMoney = (amount: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount)
    }

    if (loading) {
        return (
            <Card className="w-full">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <TrendingUp className="h-5 w-5 text-green-600" />
                        Thống Kê Cai Thuốc
                    </CardTitle>
                    <CardDescription>
                        Theo dõi tiến trình cai thuốc của bạn
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="animate-pulse">
                                <div className="h-20 bg-gray-200 rounded-lg"></div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        )
    }

    if (!stats) {
        return (
            <Card className="w-full">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <TrendingUp className="h-5 w-5 text-green-600" />
                        Thống Kê Cai Thuốc
                    </CardTitle>
                    <CardDescription>
                        Theo dõi tiến trình cai thuốc của bạn
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="text-center py-8">
                        <p className="text-gray-500">Chưa có dữ liệu thống kê</p>
                        <p className="text-sm text-gray-400 mt-2">
                            Bắt đầu kế hoạch cai thuốc để xem thống kê
                        </p>
                    </div>
                </CardContent>
            </Card>
        )
    }

    return (
        <Card className="w-full">
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <TrendingUp className="h-5 w-5 text-green-600" />
                    Thống Kê Cai Thuốc
                </CardTitle>
                <CardDescription>
                    Theo dõi tiến trình cai thuốc của bạn
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Số ngày không hút */}
                    <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-4 rounded-lg border border-blue-200">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-blue-700">Ngày Không Hút</p>
                                <p className="text-2xl font-bold text-blue-900">
                                    {stats.daysWithoutSmoking}
                                </p>
                                <p className="text-xs text-blue-600 mt-1">ngày</p>
                            </div>
                            <div className="bg-blue-500 p-3 rounded-full">
                                <Calendar className="h-6 w-6 text-white" />
                            </div>
                        </div>
                        <Badge variant="secondary" className="mt-2 bg-blue-200 text-blue-800">
                            Tiếp tục phấn đấu!
                        </Badge>
                    </div>

                    {/* Số điếu đã tránh */}
                    <div className="bg-gradient-to-br from-green-50 to-green-100 p-4 rounded-lg border border-green-200">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-green-700">Điếu Đã Tránh</p>
                                <p className="text-2xl font-bold text-green-900">
                                    {stats.cigarettesAvoided.toLocaleString()}
                                </p>
                                <p className="text-xs text-green-600 mt-1">điếu</p>
                            </div>
                            <div className="bg-green-500 p-3 rounded-full">
                                <Cigarette className="h-6 w-6 text-white" />
                            </div>
                        </div>
                        <Badge variant="secondary" className="mt-2 bg-green-200 text-green-800">
                            Tuyệt vời!
                        </Badge>
                    </div>

                    {/* Tiền tiết kiệm */}
                    <div className="bg-gradient-to-br from-yellow-50 to-yellow-100 p-4 rounded-lg border border-yellow-200">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-yellow-700">Tiền Tiết Kiệm</p>
                                <p className="text-2xl font-bold text-yellow-900">
                                    {formatMoney(stats.moneySaved)}
                                </p>
                                <p className="text-xs text-yellow-600 mt-1">VND</p>
                            </div>
                            <div className="bg-yellow-500 p-3 rounded-full">
                                <DollarSign className="h-6 w-6 text-white" />
                            </div>
                        </div>
                        <Badge variant="secondary" className="mt-2 bg-yellow-200 text-yellow-800">
                            Tiết kiệm tốt!
                        </Badge>
                    </div>
                </div>

                {/* Thông tin bổ sung */}
                <div className="mt-6 p-4 bg-gray-50 rounded-lg">
                    <h4 className="font-medium text-gray-900 mb-2">Thông tin bổ sung:</h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-600">
                        <div>
                            <p>• Trung bình tiết kiệm: <span className="font-medium">{formatMoney(stats.daysWithoutSmoking > 0 ? Math.round(stats.moneySaved / stats.daysWithoutSmoking) : 0)}</span>/ngày</p>
                            <p>• Tỷ lệ thành công: <span className="font-medium text-green-600">{(stats.cigarettesAvoided / (stats.daysWithoutSmoking * 20) * 100).toFixed(1)}%</span></p>
                        </div>
                        <div>
                            <p>• Điếu tránh/ngày: <span className="font-medium">{stats.daysWithoutSmoking > 0 ? Math.round(stats.cigarettesAvoided / stats.daysWithoutSmoking) : 0}</span></p>
                            <p>• Ước tính tiết kiệm/tháng: <span className="font-medium">{formatMoney(stats.moneySaved * 30 / stats.daysWithoutSmoking)}</span></p>
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>
    )
} 