"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { FileText, Eye, Heart, MessageSquare, Share2, Edit, Trash2, Plus, BarChart3, TrendingUp } from "lucide-react"
import { ChartContainer, LineChart } from "@/components/ui/Chart"
import { useTheme } from "@/context/ThemeContext"

interface ContentItem {
    id: number
    title: string
    type: "article" | "video" | "tip" | "guide"
    status: "published" | "draft" | "review"
    views: number
    likes: number
    comments: number
    shares: number
    publishDate: string
    author: string
}

export function ContentManagement() {
    const { theme } = useTheme()
    const [contents] = useState<ContentItem[]>([
        {
            id: 1,
            title: "10 Mẹo Cho Tuần Đầu Tiên Cai Thuốc",
            type: "article",
            status: "published",
            views: 15420,
            likes: 892,
            comments: 156,
            shares: 234,
            publishDate: "2024-01-15",
            author: "Dr. Nguyễn Văn A",
        },
        {
            id: 2,
            title: "Video Hướng Dẫn Thở Sâu Khi Thèm Thuốc",
            type: "video",
            status: "published",
            views: 8930,
            likes: 567,
            comments: 89,
            shares: 123,
            publishDate: "2024-01-12",
            author: "Chuyên gia B",
        },
        {
            id: 3,
            title: "Lợi Ích Sức Khỏe Sau Khi Cai Thuốc",
            type: "guide",
            status: "review",
            views: 0,
            likes: 0,
            comments: 0,
            shares: 0,
            publishDate: "",
            author: "Dr. Trần Thị C",
        },
        {
            id: 4,
            title: "Mẹo Nhỏ: Thay Thế Thói Quen Hút Thuốc",
            type: "tip",
            status: "draft",
            views: 0,
            likes: 0,
            comments: 0,
            shares: 0,
            publishDate: "",
            author: "Chuyên gia D",
        },
        {
            id: 5,
            title: "Câu Chuyện Thành Công: Từ 2 Bao/Ngày Đến Hoàn Toàn Sạch",
            type: "article",
            status: "published",
            views: 12350,
            likes: 1024,
            comments: 203,
            shares: 456,
            publishDate: "2024-01-10",
            author: "Người dùng E",
        },
    ])

    const chartColors = {
        primary: theme === "dark" ? "#60a5fa" : "#3b82f6",
        success: theme === "dark" ? "#34d399" : "#10b981",
        warning: theme === "dark" ? "#fbbf24" : "#f59e0b",
        purple: theme === "dark" ? "#a78bfa" : "#8b5cf6",
        red: theme === "dark" ? "#f87171" : "#ef4444",
    }

    // Generate chart data
    const contentPerformanceData = contents
        .filter((c) => c.status === "published")
        .map((content) => ({
            title: content.title.length > 20 ? content.title.substring(0, 20) + "..." : content.title,
            views: content.views,
            likes: content.likes,
            engagement: Math.round(((content.likes + content.comments + content.shares) / content.views) * 100),
        }))

    const contentTypeData = [
        { type: "Bài viết", count: contents.filter((c) => c.type === "article").length },
        { type: "Video", count: contents.filter((c) => c.type === "video").length },
        { type: "Hướng dẫn", count: contents.filter((c) => c.type === "guide").length },
        { type: "Mẹo", count: contents.filter((c) => c.type === "tip").length },
    ]

    const monthlyEngagementData = Array.from({ length: 6 }, (_, i) => ({
        month: `T${i + 1}`,
        views: Math.floor(Math.random() * 50000) + 20000,
        likes: Math.floor(Math.random() * 5000) + 2000,
        comments: Math.floor(Math.random() * 1000) + 500,
        shares: Math.floor(Math.random() * 2000) + 800,
    }))

    const topPerformingData = contents
        .filter((c) => c.status === "published")
        .sort((a, b) => b.views - a.views)
        .slice(0, 5)
        .map((content) => ({
            title: content.title.length > 25 ? content.title.substring(0, 25) + "..." : content.title,
            views: content.views,
            engagement: content.likes + content.comments + content.shares,
        }))

    const getTypeIcon = (type: string) => {
        switch (type) {
            case "article":
                return <FileText className="w-4 h-4" />
            case "video":
                return <Eye className="w-4 h-4" />
            case "guide":
                return <FileText className="w-4 h-4" />
            case "tip":
                return <MessageSquare className="w-4 h-4" />
            default:
                return <FileText className="w-4 h-4" />
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "published":
                return "default"
            case "draft":
                return "secondary"
            case "review":
                return "outline"
            default:
                return "secondary"
        }
    }

    const getStatusText = (status: string) => {
        switch (status) {
            case "published":
                return "Đã xuất bản"
            case "draft":
                return "Bản nháp"
            case "review":
                return "Đang xem xét"
            default:
                return status
        }
    }

    return (
        <div className="space-y-6">
            {/* Charts Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Content Performance */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <BarChart3 className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            <span>Hiệu Suất Nội Dung</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Lượt xem và tương tác theo bài viết
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={contentPerformanceData}
                                xAxis={[{ scaleType: "point", dataKey: "title" }]}
                                series={[
                                    { dataKey: "views", label: "Lượt xem", color: chartColors.primary },
                                    { dataKey: "likes", label: "Lượt thích", color: chartColors.success },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Content Type Distribution */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <FileText className="w-5 h-5 text-green-600 dark:text-green-400" />
                            <span>Phân Bố Loại Nội Dung</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">Số lượng nội dung theo loại</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={contentTypeData}
                                xAxis={[{ scaleType: "point", dataKey: "type" }]}
                                series={[{ dataKey: "count", label: "Số lượng", color: chartColors.success }]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>
            </div>

            {/* Monthly Engagement Trends */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                        <TrendingUp className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                        <span>Xu Hướng Tương Tác Hàng Tháng</span>
                    </CardTitle>
                    <CardDescription className="text-gray-600 dark:text-gray-400">
                        Lượt xem, thích, bình luận và chia sẻ theo tháng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <ChartContainer className="h-[400px]">
                        <LineChart
                            dataset={monthlyEngagementData}
                            xAxis={[{ scaleType: "point", dataKey: "month" }]}
                            series={[
                                { dataKey: "views", label: "Lượt xem", color: chartColors.primary },
                                { dataKey: "likes", label: "Lượt thích", color: chartColors.success },
                                { dataKey: "comments", label: "Bình luận", color: chartColors.warning },
                                { dataKey: "shares", label: "Chia sẻ", color: chartColors.red },
                            ]}
                            width={800}
                            height={400}
                        />
                    </ChartContainer>
                </CardContent>
            </Card>

            {/* Top Performing Content */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2 text-gray-900 dark:text-gray-100">
                            <TrendingUp className="w-5 h-5 text-orange-600 dark:text-orange-400" />
                            <span>Nội Dung Hiệu Suất Cao</span>
                        </CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Top 5 nội dung có lượt xem cao nhất
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ChartContainer className="h-[300px]">
                            <LineChart
                                dataset={topPerformingData}
                                xAxis={[{ scaleType: "point", dataKey: "title" }]}
                                series={[
                                    { dataKey: "views", label: "Lượt xem", color: chartColors.warning },
                                    { dataKey: "engagement", label: "Tương tác", color: chartColors.purple },
                                ]}
                                width={500}
                                height={300}
                            />
                        </ChartContainer>
                    </CardContent>
                </Card>

                {/* Content Management Actions */}
                <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                    <CardHeader>
                        <CardTitle className="text-gray-900 dark:text-gray-100">Thao Tác Quản Lý</CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Các tác vụ quản lý nội dung thường dùng
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="grid grid-cols-2 gap-4">
                            <Button variant="outline" className="h-20 flex flex-col space-y-2">
                                <Plus className="w-6 h-6" />
                                <span>Tạo Nội Dung</span>
                            </Button>
                            <Button variant="outline" className="h-20 flex flex-col space-y-2">
                                <Edit className="w-6 h-6" />
                                <span>Chỉnh Sửa</span>
                            </Button>
                            <Button variant="outline" className="h-20 flex flex-col space-y-2">
                                <Eye className="w-6 h-6" />
                                <span>Xem Trước</span>
                            </Button>
                            <Button variant="outline" className="h-20 flex flex-col space-y-2">
                                <BarChart3 className="w-6 h-6" />
                                <span>Phân Tích</span>
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Content List */}
            <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm border-0 shadow-lg">
                <CardHeader>
                    <CardTitle className="flex items-center justify-between text-gray-900 dark:text-gray-100">
                        <span>Quản Lý Nội Dung</span>
                        <Button size="sm">
                            <Plus className="w-4 h-4 mr-2" />
                            Tạo Nội Dung Mới
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {contents.map((content) => (
                            <div
                                key={content.id}
                                className="flex items-center justify-between p-4 rounded-lg bg-gray-50 dark:bg-gray-700/50"
                            >
                                <div className="flex-1">
                                    <div className="flex items-center space-x-3">
                                        {getTypeIcon(content.type)}
                                        <div>
                                            <p className="font-medium text-gray-900 dark:text-gray-100">{content.title}</p>
                                            <p className="text-sm text-gray-600 dark:text-gray-400">Tác giả: {content.author}</p>
                                            {content.publishDate && (
                                                <p className="text-xs text-gray-500 dark:text-gray-300">Xuất bản: {content.publishDate}</p>
                                            )}
                                        </div>
                                    </div>
                                    {content.status === "published" && (
                                        <div className="mt-3 grid grid-cols-4 gap-4 text-sm">
                                            <div className="flex items-center space-x-1">
                                                <Eye className="w-4 h-4" />
                                                <span>{content.views.toLocaleString()}</span>
                                            </div>
                                            <div className="flex items-center space-x-1">
                                                <Heart className="w-4 h-4" />
                                                <span>{content.likes.toLocaleString()}</span>
                                            </div>
                                            <div className="flex items-center space-x-1">
                                                <MessageSquare className="w-4 h-4" />
                                                <span>{content.comments}</span>
                                            </div>
                                            <div className="flex items-center space-x-1">
                                                <Share2 className="w-4 h-4" />
                                                <span>{content.shares}</span>
                                            </div>
                                        </div>
                                    )}
                                </div>
                                <div className="flex items-center space-x-4">
                                    <Badge variant={getStatusColor(content.status)}>{getStatusText(content.status)}</Badge>
                                    <div className="flex space-x-2">
                                        <Button size="sm" variant="outline">
                                            <Edit className="w-4 h-4" />
                                        </Button>
                                        <Button size="sm" variant="outline">
                                            <Eye className="w-4 h-4" />
                                        </Button>
                                        <Button size="sm" variant="outline">
                                            <Trash2 className="w-4 h-4" />
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
