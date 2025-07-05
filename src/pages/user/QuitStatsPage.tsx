"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Calendar, Cigarette, DollarSign, TrendingUp, ArrowLeft, Home } from "lucide-react"
import { userService } from "@/services/userService"
import type { UserQuitStatsResponse } from "@/services/userService"
import { toast } from "react-toastify"
import { Link } from "react-router-dom"
import { useAuth } from "@/hooks/useAuth"

export default function QuitStatsPage() {
    const { user } = useAuth()
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

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900">
            {/* Header */}
            <div className="bg-white dark:bg-slate-800 border-b border-slate-200 dark:border-slate-700">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        <div className="flex items-center space-x-4">
                            <Link to="/user">
                                <Button variant="ghost" size="sm">
                                    <ArrowLeft className="h-4 w-4 mr-2" />
                                    Quay lại
                                </Button>
                            </Link>
                            <div>
                                <h1 className="text-xl font-semibold text-slate-900 dark:text-white">
                                    Thống Kê Cai Thuốc
                                </h1>
                                <p className="text-sm text-slate-500 dark:text-slate-400">
                                    Theo dõi tiến trình cai thuốc của bạn
                                </p>
                            </div>
                        </div>
                        <Link to="/">
                            <Button variant="ghost" size="sm">
                                <Home className="h-4 w-4 mr-2" />
                                Trang chủ
                            </Button>
                        </Link>
                    </div>
                </div>
            </div>

            {/* Content */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {loading ? (
                    <Card className="w-full">
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <TrendingUp className="h-5 w-5 text-green-600" />
                                Thống Kê Cai Thuốc
                            </CardTitle>
                            <CardDescription>
                                Đang tải dữ liệu...
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                {[1, 2, 3].map((i) => (
                                    <div key={i} className="animate-pulse">
                                        <div className="h-32 bg-gray-200 rounded-lg"></div>
                                    </div>
                                ))}
                            </div>
                        </CardContent>
                    </Card>
                ) : !stats ? (
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
                            <div className="text-center py-12">
                                <div className="mx-auto w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                                    <TrendingUp className="h-12 w-12 text-gray-400" />
                                </div>
                                <h3 className="text-lg font-medium text-gray-900 mb-2">
                                    Chưa có dữ liệu thống kê
                                </h3>
                                <p className="text-gray-500 mb-6">
                                    Bắt đầu kế hoạch cai thuốc để xem thống kê chi tiết
                                </p>
                                <Link to="/plan">
                                    <Button>
                                        Bắt đầu kế hoạch cai thuốc
                                    </Button>
                                </Link>
                            </div>
                        </CardContent>
                    </Card>
                ) : (
                    <div className="space-y-6">
                        {/* Main Stats Cards */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                            {/* Số ngày không hút */}
                            <Card className="bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
                                <CardContent className="p-6">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <p className="text-sm font-medium text-blue-700">Ngày Không Hút</p>
                                            <p className="text-4xl font-bold text-blue-900 mt-2">
                                                {stats.daysWithoutSmoking}
                                            </p>
                                            <p className="text-sm text-blue-600 mt-1">ngày</p>
                                        </div>
                                        <div className="bg-blue-500 p-4 rounded-full">
                                            <Calendar className="h-8 w-8 text-white" />
                                        </div>
                                    </div>
                                    <Badge variant="secondary" className="mt-4 bg-blue-200 text-blue-800">
                                        Tiếp tục phấn đấu!
                                    </Badge>
                                </CardContent>
                            </Card>

                            {/* Số điếu đã tránh */}
                            <Card className="bg-gradient-to-br from-green-50 to-green-100 border-green-200">
                                <CardContent className="p-6">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <p className="text-sm font-medium text-green-700">Điếu Đã Tránh</p>
                                            <p className="text-4xl font-bold text-green-900 mt-2">
                                                {stats.cigarettesAvoided.toLocaleString()}
                                            </p>
                                            <p className="text-sm text-green-600 mt-1">điếu</p>
                                        </div>
                                        <div className="bg-green-500 p-4 rounded-full">
                                            <Cigarette className="h-8 w-8 text-white" />
                                        </div>
                                    </div>
                                    <Badge variant="secondary" className="mt-4 bg-green-200 text-green-800">
                                        Tuyệt vời!
                                    </Badge>
                                </CardContent>
                            </Card>

                            {/* Tiền tiết kiệm */}
                            <Card className="bg-gradient-to-br from-yellow-50 to-yellow-100 border-yellow-200">
                                <CardContent className="p-6">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <p className="text-sm font-medium text-yellow-700">Tiền Tiết Kiệm</p>
                                            <p className="text-4xl font-bold text-yellow-900 mt-2">
                                                {formatMoney(stats.moneySaved)}
                                            </p>
                                            <p className="text-sm text-yellow-600 mt-1">VND</p>
                                        </div>
                                        <div className="bg-yellow-500 p-4 rounded-full">
                                            <DollarSign className="h-8 w-8 text-white" />
                                        </div>
                                    </div>
                                    <Badge variant="secondary" className="mt-4 bg-yellow-200 text-yellow-800">
                                        Tiết kiệm tốt!
                                    </Badge>
                                </CardContent>
                            </Card>
                        </div>

                        {/* Detailed Stats - chỉ hiển thị nếu KHÔNG phải user thường hoặc premium */}
                        {user && user.role !== 'NORMAL_MEMBER' && user.role !== 'PREMIUM_MEMBER' && (
                            <Card>
                                <CardHeader>
                                    <CardTitle>Thông Tin Chi Tiết</CardTitle>
                                    <CardDescription>
                                        Phân tích chi tiết về tiến trình cai thuốc của bạn
                                    </CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                                        <div className="text-center p-4 bg-gray-50 rounded-lg">
                                            <p className="text-2xl font-bold text-gray-900">
                                                {formatMoney(stats.daysWithoutSmoking > 0 ? Math.round(stats.moneySaved / stats.daysWithoutSmoking) : 0)}
                                            </p>
                                            <p className="text-sm text-gray-600">Trung bình tiết kiệm/ngày</p>
                                        </div>
                                        <div className="text-center p-4 bg-gray-50 rounded-lg">
                                            <p className="text-2xl font-bold text-green-600">
                                                {(stats.cigarettesAvoided / (stats.daysWithoutSmoking * 20) * 100).toFixed(1)}%
                                            </p>
                                            <p className="text-sm text-gray-600">Tỷ lệ thành công</p>
                                        </div>
                                        <div className="text-center p-4 bg-gray-50 rounded-lg">
                                            <p className="text-2xl font-bold text-blue-600">
                                                {stats.daysWithoutSmoking > 0 ? Math.round(stats.cigarettesAvoided / stats.daysWithoutSmoking) : 0}
                                            </p>
                                            <p className="text-sm text-gray-600">Điếu tránh/ngày</p>
                                        </div>
                                        <div className="text-center p-4 bg-gray-50 rounded-lg">
                                            <p className="text-2xl font-bold text-purple-600">
                                                {formatMoney(stats.moneySaved * 30 / stats.daysWithoutSmoking)}
                                            </p>
                                            <p className="text-sm text-gray-600">Ước tính tiết kiệm/tháng</p>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        )}

                        {/* Motivation Section */}
                        <Card className="bg-gradient-to-r from-green-500 to-blue-500 text-white">
                            <CardContent className="p-8 text-center">
                                <h3 className="text-2xl font-bold mb-4">
                                    🎉 Chúc mừng bạn!
                                </h3>
                                <p className="text-lg mb-6">
                                    Bạn đã tiết kiệm được <strong>{formatMoney(stats.moneySaved)}</strong> và tránh được <strong>{stats.cigarettesAvoided.toLocaleString()} điếu thuốc</strong>!
                                </p>
                                <p className="text-green-100">
                                    Hãy tiếp tục duy trì thành tích này và tận hưởng cuộc sống khỏe mạnh hơn!
                                </p>
                            </CardContent>
                        </Card>
                    </div>
                )}
            </div>
        </div>
    )
} 