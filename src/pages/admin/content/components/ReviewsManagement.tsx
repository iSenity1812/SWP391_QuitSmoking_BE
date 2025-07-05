"use client"

import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Star, MessageSquare, CheckCircle, XCircle, Eye, Reply, Filter, Search, Calendar, User } from "lucide-react"

interface Review {
    id: number
    userId: string
    userName: string
    userAvatar?: string
    rating: number
    title: string
    content: string
    createdAt: string
    status: "pending" | "approved" | "rejected"
    category: "app" | "coach" | "program" | "general"
    isVerified: boolean
    helpfulCount: number
    adminResponse?: string
    adminResponseDate?: string
}

export function ReviewsManagement() {
    const [reviews, setReviews] = useState<Review[]>([
        {
            id: 1,
            userId: "user1",
            userName: "Nguyễn Văn An",
            userAvatar: "/placeholder.svg?height=40&width=40",
            rating: 5,
            title: "Ứng dụng tuyệt vời!",
            content: "Đã giúp tôi cai thuốc thành công sau 15 năm hút thuốc. Cảm ơn team rất nhiều!",
            createdAt: "2024-01-15T10:30:00Z",
            status: "approved",
            category: "app",
            isVerified: true,
            helpfulCount: 23,
            adminResponse: "Cảm ơn bạn đã chia sẻ! Chúng tôi rất vui khi biết ứng dụng đã giúp ích cho bạn.",
            adminResponseDate: "2024-01-15T14:20:00Z",
        },
        {
            id: 2,
            userId: "user2",
            userName: "Trần Thị Bình",
            userAvatar: "/placeholder.svg?height=40&width=40",
            rating: 4,
            title: "Coach rất nhiệt tình",
            content: "Coach Minh đã hỗ trợ tôi rất tận tình trong quá trình cai thuốc.",
            createdAt: "2024-01-16T09:15:00Z",
            status: "pending",
            category: "coach",
            isVerified: false,
            helpfulCount: 8,
        },
        {
            id: 3,
            userId: "user3",
            userName: "Lê Văn Cường",
            rating: 2,
            title: "Gói premium hơi đắt",
            content: "Tính năng tốt nhưng giá cả chưa phù hợp với túi tiền.",
            createdAt: "2024-01-17T16:45:00Z",
            status: "pending",
            category: "program",
            isVerified: false,
            helpfulCount: 3,
        },
    ])

    const [isResponseModalOpen, setIsResponseModalOpen] = useState(false)
    const [selectedReview, setSelectedReview] = useState<Review | null>(null)
    const [responseText, setResponseText] = useState("")

    const handleApproveReview = (reviewId: number) => {
        setReviews((prev) =>
            prev.map((review) => (review.id === reviewId ? { ...review, status: "approved" as const } : review)),
        )
    }

    const handleRejectReview = (reviewId: number) => {
        setReviews((prev) =>
            prev.map((review) => (review.id === reviewId ? { ...review, status: "rejected" as const } : review)),
        )
    }

    const handleOpenResponseModal = (review: Review) => {
        setSelectedReview(review)
        setResponseText(review.adminResponse || "")
        setIsResponseModalOpen(true)
    }

    const handleSubmitResponse = () => {
        if (!selectedReview) return
        setReviews((prev) =>
            prev.map((review) =>
                review.id === selectedReview.id
                    ? {
                        ...review,
                        adminResponse: responseText,
                        adminResponseDate: new Date().toISOString(),
                        status: "approved" as const,
                    }
                    : review,
            ),
        )
        setIsResponseModalOpen(false)
        setResponseText("")
        setSelectedReview(null)
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "approved":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "pending":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "rejected":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getCategoryColor = (category: string) => {
        switch (category) {
            case "app":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "coach":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            case "program":
                return "bg-orange-100 text-orange-800 dark:bg-orange-900/20 dark:text-orange-400"
            case "general":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const renderStars = (rating: number) => {
        return Array.from({ length: 5 }, (_, i) => (
            <Star key={i} className={`w-4 h-4 ${i < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`} />
        ))
    }

    const pendingReviews = reviews.filter((review) => review.status === "pending")
    const approvedReviews = reviews.filter((review) => review.status === "approved")
    const rejectedReviews = reviews.filter((review) => review.status === "rejected")

    const cardVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: (i: number) => ({
            opacity: 1,
            y: 0,
            transition: {
                delay: i * 0.1,
                duration: 0.4,
                ease: "easeOut",
            },
        }),
    }

    return (
        <motion.div className="space-y-6" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Quản Lý Đánh Giá</CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Duyệt và phản hồi đánh giá từ người dùng
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <Tabs defaultValue="pending" className="space-y-6">
                        <TabsList className="grid w-full grid-cols-3">
                            <TabsTrigger value="pending" className="flex items-center space-x-2">
                                <MessageSquare className="w-4 h-4" />
                                <span>Chờ Duyệt ({pendingReviews.length})</span>
                            </TabsTrigger>
                            <TabsTrigger value="approved" className="flex items-center space-x-2">
                                <CheckCircle className="w-4 h-4" />
                                <span>Đã Duyệt ({approvedReviews.length})</span>
                            </TabsTrigger>
                            <TabsTrigger value="rejected" className="flex items-center space-x-2">
                                <XCircle className="w-4 h-4" />
                                <span>Từ Chối ({rejectedReviews.length})</span>
                            </TabsTrigger>
                        </TabsList>

                        <TabsContent value="pending" className="space-y-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Đánh Giá Chờ Duyệt</h3>
                                <div className="flex space-x-2">
                                    <Button variant="outline" size="sm">
                                        <Filter className="w-4 h-4 mr-2" />
                                        Lọc
                                    </Button>
                                    <Button variant="outline" size="sm">
                                        <Search className="w-4 h-4 mr-2" />
                                        Tìm kiếm
                                    </Button>
                                </div>
                            </div>

                            <AnimatePresence>
                                <div className="space-y-4">
                                    {pendingReviews.map((review, index) => (
                                        <motion.div
                                            key={review.id}
                                            custom={index}
                                            variants={cardVariants}
                                            initial="hidden"
                                            animate="visible"
                                            whileHover={{ scale: 1.01, boxShadow: "0 10px 25px rgba(0,0,0,0.1)" }}
                                            className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                                        >
                                            <div className="flex items-start justify-between">
                                                <div className="flex items-start space-x-4 flex-1">
                                                    <Avatar>
                                                        <AvatarImage src={review.userAvatar || "/placeholder.svg"} />
                                                        <AvatarFallback>{review.userName.charAt(0)}</AvatarFallback>
                                                    </Avatar>
                                                    <div className="flex-1">
                                                        <div className="flex items-center space-x-2 mb-2">
                                                            <h4 className="font-medium text-slate-900 dark:text-white">{review.title}</h4>
                                                            <div className="flex items-center space-x-1">{renderStars(review.rating)}</div>
                                                            <Badge className={getCategoryColor(review.category)}>
                                                                {review.category === "app"
                                                                    ? "Ứng dụng"
                                                                    : review.category === "coach"
                                                                        ? "Coach"
                                                                        : review.category === "program"
                                                                            ? "Chương trình"
                                                                            : "Chung"}
                                                            </Badge>
                                                            {review.isVerified && (
                                                                <Badge variant="outline" className="text-blue-600">
                                                                    <CheckCircle className="w-3 h-3 mr-1" />
                                                                    Đã xác thực
                                                                </Badge>
                                                            )}
                                                        </div>
                                                        <p className="text-slate-700 dark:text-slate-300 mb-2">{review.content}</p>
                                                        <div className="flex items-center space-x-4 text-sm text-slate-500 dark:text-slate-400">
                                                            <span className="flex items-center">
                                                                <User className="w-3 h-3 mr-1" />
                                                                {review.userName}
                                                            </span>
                                                            <span className="flex items-center">
                                                                <Calendar className="w-3 h-3 mr-1" />
                                                                {new Date(review.createdAt).toLocaleDateString("vi-VN")}
                                                            </span>
                                                            <span>{review.helpfulCount} người thấy hữu ích</span>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="flex space-x-2">
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                                        <Button size="sm" variant="ghost">
                                                            <Eye className="w-4 h-4" />
                                                        </Button>
                                                    </motion.div>
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                                        <Button size="sm" variant="ghost" onClick={() => handleOpenResponseModal(review)}>
                                                            <Reply className="w-4 h-4" />
                                                        </Button>
                                                    </motion.div>
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                                        <Button
                                                            size="sm"
                                                            onClick={() => handleApproveReview(review.id)}
                                                            className="bg-green-600 hover:bg-green-700 text-white"
                                                        >
                                                            <CheckCircle className="w-4 h-4 mr-1" />
                                                            Duyệt
                                                        </Button>
                                                    </motion.div>
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                                        <Button size="sm" variant="destructive" onClick={() => handleRejectReview(review.id)}>
                                                            <XCircle className="w-4 h-4 mr-1" />
                                                            Từ chối
                                                        </Button>
                                                    </motion.div>
                                                </div>
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            </AnimatePresence>
                        </TabsContent>

                        <TabsContent value="approved" className="space-y-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Đánh Giá Đã Duyệt</h3>
                            </div>

                            <div className="space-y-4">
                                {approvedReviews.map((review, index) => (
                                    <motion.div
                                        key={review.id}
                                        custom={index}
                                        variants={cardVariants}
                                        initial="hidden"
                                        animate="visible"
                                        whileHover={{ scale: 1.01 }}
                                        className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex items-start space-x-4 flex-1">
                                                <Avatar>
                                                    <AvatarImage src={review.userAvatar || "/placeholder.svg"} />
                                                    <AvatarFallback>{review.userName.charAt(0)}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center space-x-2 mb-2">
                                                        <h4 className="font-medium text-slate-900 dark:text-white">{review.title}</h4>
                                                        <div className="flex items-center space-x-1">{renderStars(review.rating)}</div>
                                                        <Badge className={getStatusColor(review.status)}>Đã duyệt</Badge>
                                                        <Badge className={getCategoryColor(review.category)}>
                                                            {review.category === "app"
                                                                ? "Ứng dụng"
                                                                : review.category === "coach"
                                                                    ? "Coach"
                                                                    : review.category === "program"
                                                                        ? "Chương trình"
                                                                        : "Chung"}
                                                        </Badge>
                                                    </div>
                                                    <p className="text-slate-700 dark:text-slate-300 mb-2">{review.content}</p>
                                                    {review.adminResponse && (
                                                        <div className="mt-3 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg border-l-4 border-blue-500">
                                                            <div className="flex items-center space-x-2 mb-1">
                                                                <Badge variant="outline" className="text-blue-600">
                                                                    Phản hồi từ Admin
                                                                </Badge>
                                                                <span className="text-xs text-slate-500">
                                                                    {new Date(review.adminResponseDate!).toLocaleDateString("vi-VN")}
                                                                </span>
                                                            </div>
                                                            <p className="text-sm text-slate-700 dark:text-slate-300">{review.adminResponse}</p>
                                                        </div>
                                                    )}
                                                    <div className="flex items-center space-x-4 text-sm text-slate-500 dark:text-slate-400 mt-2">
                                                        <span className="flex items-center">
                                                            <User className="w-3 h-3 mr-1" />
                                                            {review.userName}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <Calendar className="w-3 h-3 mr-1" />
                                                            {new Date(review.createdAt).toLocaleDateString("vi-VN")}
                                                        </span>
                                                        <span>{review.helpfulCount} người thấy hữu ích</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="ghost">
                                                    <Eye className="w-4 h-4" />
                                                </Button>
                                                <Button size="sm" variant="ghost" onClick={() => handleOpenResponseModal(review)}>
                                                    <Reply className="w-4 h-4" />
                                                </Button>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>
                        </TabsContent>

                        <TabsContent value="rejected" className="space-y-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Đánh Giá Bị Từ Chối</h3>
                            </div>

                            <div className="space-y-4">
                                {rejectedReviews.map((review, index) => (
                                    <motion.div
                                        key={review.id}
                                        custom={index}
                                        variants={cardVariants}
                                        initial="hidden"
                                        animate="visible"
                                        whileHover={{ scale: 1.01 }}
                                        className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600 opacity-75"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex items-start space-x-4 flex-1">
                                                <Avatar>
                                                    <AvatarImage src={review.userAvatar || "/placeholder.svg"} />
                                                    <AvatarFallback>{review.userName.charAt(0)}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center space-x-2 mb-2">
                                                        <h4 className="font-medium text-slate-900 dark:text-white">{review.title}</h4>
                                                        <div className="flex items-center space-x-1">{renderStars(review.rating)}</div>
                                                        <Badge className={getStatusColor(review.status)}>Từ chối</Badge>
                                                    </div>
                                                    <p className="text-slate-700 dark:text-slate-300 mb-2">{review.content}</p>
                                                    <div className="flex items-center space-x-4 text-sm text-slate-500 dark:text-slate-400">
                                                        <span className="flex items-center">
                                                            <User className="w-3 h-3 mr-1" />
                                                            {review.userName}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <Calendar className="w-3 h-3 mr-1" />
                                                            {new Date(review.createdAt).toLocaleDateString("vi-VN")}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="outline" onClick={() => handleApproveReview(review.id)}>
                                                    <CheckCircle className="w-4 h-4 mr-1" />
                                                    Khôi phục
                                                </Button>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>
                        </TabsContent>
                    </Tabs>
                </CardContent>
            </Card>

            {/* Response Modal */}
            <Dialog open={isResponseModalOpen} onOpenChange={setIsResponseModalOpen}>
                <DialogContent className="sm:max-w-[600px]">
                    <DialogHeader>
                        <DialogTitle>Phản Hồi Đánh Giá</DialogTitle>
                    </DialogHeader>
                    {selectedReview && (
                        <div className="space-y-4">
                            <div className="p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                <div className="flex items-center space-x-2 mb-2">
                                    <h4 className="font-medium text-slate-900 dark:text-white">{selectedReview.title}</h4>
                                    <div className="flex items-center space-x-1">{renderStars(selectedReview.rating)}</div>
                                </div>
                                <p className="text-slate-700 dark:text-slate-300 mb-2">{selectedReview.content}</p>
                                <p className="text-sm text-slate-500 dark:text-slate-400">
                                    Bởi {selectedReview.userName} • {new Date(selectedReview.createdAt).toLocaleDateString("vi-VN")}
                                </p>
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="response">Phản hồi của bạn</Label>
                                <Textarea
                                    id="response"
                                    value={responseText}
                                    onChange={(e) => setResponseText(e.target.value)}
                                    placeholder="Nhập phản hồi cho đánh giá này..."
                                    className="min-h-[120px]"
                                />
                            </div>
                            <div className="flex justify-end gap-4">
                                <Button variant="outline" onClick={() => setIsResponseModalOpen(false)}>
                                    Hủy
                                </Button>
                                <Button onClick={handleSubmitResponse} disabled={!responseText.trim()}>
                                    Gửi Phản Hồi
                                </Button>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </motion.div>
    )
}
