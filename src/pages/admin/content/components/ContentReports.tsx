"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { CheckCircle, XCircle, Flag, MessageSquare, FileText, User, Calendar } from "lucide-react"

interface ContentReport {
    id: number
    reporterId: string
    reporterName: string
    reporterAvatar?: string
    contentType: "review" | "blog" | "comment" | "user"
    contentId: string
    contentTitle: string
    contentAuthor: string
    reportReason: "inappropriate" | "spam" | "harassment" | "misinformation" | "other"
    reportDescription: string
    status: "pending" | "resolved" | "dismissed"
    createdAt: string
    resolvedAt?: string
    adminNote?: string
}

export function ContentReports() {
    const [reports, setReports] = useState<ContentReport[]>([
        {
            id: 1,
            reporterId: "user1",
            reporterName: "Nguyễn Văn A",
            reporterAvatar: "/placeholder.svg?height=40&width=40",
            contentType: "review",
            contentId: "review_123",
            contentTitle: "Đánh giá về ứng dụng",
            contentAuthor: "Trần Thị B",
            reportReason: "inappropriate",
            reportDescription: "Nội dung có từ ngữ không phù hợp và thiếu tôn trọng",
            status: "pending",
            createdAt: "2024-01-15T10:30:00Z",
        },
        {
            id: 2,
            reporterId: "user2",
            reporterName: "Lê Văn C",
            contentType: "blog",
            contentId: "blog_456",
            contentTitle: "Cách cai thuốc hiệu quả",
            contentAuthor: "Coach Nguyễn D",
            reportReason: "misinformation",
            reportDescription: "Thông tin y tế không chính xác, có thể gây hiểu lầm",
            status: "pending",
            createdAt: "2024-01-14T15:20:00Z",
        },
    ])

    const handleResolveReport = (reportId: number) => {
        setReports((prev) =>
            prev.map((report) =>
                report.id === reportId
                    ? {
                        ...report,
                        status: "resolved" as const,
                        resolvedAt: new Date().toISOString(),
                        adminNote: "Đã xử lý và gỡ bỏ nội dung vi phạm",
                    }
                    : report,
            ),
        )
    }

    const handleDismissReport = (reportId: number) => {
        setReports((prev) =>
            prev.map((report) =>
                report.id === reportId
                    ? {
                        ...report,
                        status: "dismissed" as const,
                        resolvedAt: new Date().toISOString(),
                        adminNote: "Báo cáo không có căn cứ, nội dung phù hợp",
                    }
                    : report,
            ),
        )
    }

    const getContentTypeIcon = (type: string) => {
        switch (type) {
            case "review":
                return <MessageSquare className="w-4 h-4" />
            case "blog":
                return <FileText className="w-4 h-4" />
            case "comment":
                return <MessageSquare className="w-4 h-4" />
            case "user":
                return <User className="w-4 h-4" />
            default:
                return <Flag className="w-4 h-4" />
        }
    }

    const getReasonColor = (reason: string) => {
        switch (reason) {
            case "inappropriate":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "spam":
                return "bg-orange-100 text-orange-800 dark:bg-orange-900/20 dark:text-orange-400"
            case "harassment":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            case "misinformation":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "resolved":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "pending":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "dismissed":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getReasonText = (reason: string) => {
        switch (reason) {
            case "inappropriate":
                return "Nội dung không phù hợp"
            case "spam":
                return "Spam"
            case "harassment":
                return "Quấy rối"
            case "misinformation":
                return "Thông tin sai lệch"
            default:
                return "Khác"
        }
    }

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Báo Cáo Nội Dung</CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Xem xét và xử lý các báo cáo về nội dung vi phạm từ người dùng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {reports.map((report) => (
                            <div
                                key={report.id}
                                className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                            >
                                <div className="flex items-start justify-between">
                                    <div className="flex items-start space-x-4 flex-1">
                                        <Avatar>
                                            <AvatarImage src={report.reporterAvatar || "/placeholder.svg"} />
                                            <AvatarFallback>{report.reporterName.charAt(0)}</AvatarFallback>
                                        </Avatar>
                                        <div className="flex-1">
                                            <div className="flex items-center space-x-2 mb-2">
                                                <h4 className="font-medium text-slate-900 dark:text-white">Báo cáo từ {report.reporterName}</h4>
                                                <Badge className={getStatusColor(report.status)}>
                                                    {report.status === "resolved"
                                                        ? "Đã xử lý"
                                                        : report.status === "pending"
                                                            ? "Chờ xử lý"
                                                            : "Đã bỏ qua"}
                                                </Badge>
                                            </div>

                                            <div className="space-y-2 mb-3">
                                                <div className="flex items-center space-x-2">
                                                    {getContentTypeIcon(report.contentType)}
                                                    <span className="text-sm text-slate-600 dark:text-slate-400">
                                                        {report.contentType === "review"
                                                            ? "Đánh giá"
                                                            : report.contentType === "blog"
                                                                ? "Blog"
                                                                : report.contentType === "comment"
                                                                    ? "Bình luận"
                                                                    : "Người dùng"}
                                                        : "{report.contentTitle}" bởi {report.contentAuthor}
                                                    </span>
                                                </div>
                                                <div className="flex items-center space-x-2">
                                                    <Badge className={getReasonColor(report.reportReason)}>
                                                        {getReasonText(report.reportReason)}
                                                    </Badge>
                                                </div>
                                            </div>

                                            <p className="text-slate-700 dark:text-slate-300 mb-2">{report.reportDescription}</p>

                                            {report.adminNote && (
                                                <div className="p-2 bg-blue-50 dark:bg-blue-900/20 rounded border-l-4 border-blue-500 mb-2">
                                                    <p className="text-sm text-blue-800 dark:text-blue-300">
                                                        <strong>Ghi chú admin:</strong> {report.adminNote}
                                                    </p>
                                                </div>
                                            )}

                                            <div className="flex items-center space-x-4 text-xs text-slate-500 dark:text-slate-400">
                                                <span className="flex items-center">
                                                    <Calendar className="w-3 h-3 mr-1" />
                                                    {new Date(report.createdAt).toLocaleDateString("vi-VN")}
                                                </span>
                                                {report.resolvedAt && (
                                                    <span>Xử lý: {new Date(report.resolvedAt).toLocaleDateString("vi-VN")}</span>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                    {report.status === "pending" && (
                                        <div className="flex space-x-2">
                                            <Button
                                                size="sm"
                                                onClick={() => handleResolveReport(report.id)}
                                                className="bg-green-600 hover:bg-green-700"
                                            >
                                                <CheckCircle className="w-4 h-4 mr-1" />
                                                Xử lý
                                            </Button>
                                            <Button size="sm" variant="outline" onClick={() => handleDismissReport(report.id)}>
                                                <XCircle className="w-4 h-4 mr-1" />
                                                Bỏ qua
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
