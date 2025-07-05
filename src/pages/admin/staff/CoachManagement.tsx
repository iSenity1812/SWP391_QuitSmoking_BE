"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import {
    UserCheck,
    Search,
    Filter,
    Edit,
    Trash2,
    Star,
    MessageSquare,
    FileText,
    Eye,
    CheckCircle,
    XCircle,
    Award,
    Users,
    Download,
} from "lucide-react"

interface Certificate {
    id: number
    name: string
    issuer: string
    issueDate: string
    expiryDate?: string
    type: "health" | "psychology" | "counseling" | "other"
    status: "verified" | "pending" | "rejected"
    fileUrl: string
    fileName: string
    uploadDate: string
    adminNotes?: string
}

interface CoachApplication {
    id: number
    userId: number
    userName: string
    userEmail: string
    userAvatar?: string
    status: "pending" | "approved" | "rejected"
    submissionDate: string
    motivation: string
    experience: string
    specialization: string[]
    certificates: Certificate[]
    adminFeedback?: string
    reviewDate?: string
    reviewedBy?: string
}

interface Coach {
    id: number
    name: string
    email: string
    avatar?: string
    specialization: string
    experience: number
    rating: number
    totalSessions: number
    activeClients: number
    status: "active" | "inactive" | "on-leave"
    joinDate: string
    lastActive: string
    certifications: Certificate[]
    applicationId: number
}

export function CoachManagement() {
    const [activeTab, setActiveTab] = useState("coaches")
    const [selectedApplication, setSelectedApplication] = useState<CoachApplication | null>(null)
    const [selectedCoach, setSelectedCoach] = useState<Coach | null>(null)
    const [applicationDialogOpen, setApplicationDialogOpen] = useState(false)
    const [coachDetailDialogOpen, setCoachDetailDialogOpen] = useState(false)
    const [reviewDialogOpen, setReviewDialogOpen] = useState(false)
    const [reviewFeedback, setReviewFeedback] = useState("")

    // Mock data for coach applications
    const [applications, setApplications] = useState<CoachApplication[]>([
        {
            id: 1,
            userId: 101,
            userName: "Nguyễn Văn Minh",
            userEmail: "minh.nguyen@email.com",
            userAvatar: "/placeholder.svg?height=40&width=40",
            status: "pending",
            submissionDate: "2024-01-15",
            motivation:
                "Tôi đã thành công cai thuốc lá sau 15 năm hút thuốc và muốn chia sẻ kinh nghiệm để giúp đỡ những người khác.",
            experience: "Đã cai thuốc thành công 2 năm, từng hỗ trợ 5 người bạn cai thuốc thành công.",
            specialization: ["Cai thuốc dần dần", "Hỗ trợ tâm lý"],
            certificates: [
                {
                    id: 1,
                    name: "Chứng chỉ Tư vấn Sức khỏe",
                    issuer: "Bộ Y tế",
                    issueDate: "2023-06-15",
                    expiryDate: "2025-06-15",
                    type: "health",
                    status: "verified",
                    fileUrl: "/certificates/health-cert.pdf",
                    fileName: "health-counseling-cert.pdf",
                    uploadDate: "2024-01-10",
                },
                {
                    id: 2,
                    name: "Chứng chỉ Tâm lý học Ứng dụng",
                    issuer: "Đại học Tâm lý",
                    issueDate: "2022-12-20",
                    type: "psychology",
                    status: "pending",
                    fileUrl: "/certificates/psychology-cert.pdf",
                    fileName: "applied-psychology-cert.pdf",
                    uploadDate: "2024-01-15",
                },
            ],
        },
        {
            id: 2,
            userId: 102,
            userName: "Trần Thị Lan",
            userEmail: "lan.tran@email.com",
            status: "pending",
            submissionDate: "2024-01-18",
            motivation:
                "Là một y tá với 10 năm kinh nghiệm, tôi muốn sử dụng kiến thức chuyên môn để hỗ trợ cộng đồng cai thuốc.",
            experience: "Y tá tại bệnh viện, đã tư vấn cho nhiều bệnh nhân về tác hại của thuốc lá.",
            specialization: ["Y học", "Dinh dưỡng", "Hỗ trợ tâm lý"],
            certificates: [
                {
                    id: 3,
                    name: "Bằng Y tá",
                    issuer: "Trường Cao đẳng Y tế",
                    issueDate: "2014-07-20",
                    type: "health",
                    status: "verified",
                    fileUrl: "/certificates/nurse-cert.pdf",
                    fileName: "nurse-certificate.pdf",
                    uploadDate: "2024-01-18",
                },
            ],
        },
    ])

    // Mock data for active coaches
    const [coaches, setCoaches] = useState<Coach[]>([
        {
            id: 1,
            name: "Dr. Nguyễn Văn A",
            email: "dr.nguyenvana@email.com",
            avatar: "/placeholder.svg?height=40&width=40",
            specialization: "Tâm lý học lâm sàng",
            experience: 8,
            rating: 4.9,
            totalSessions: 234,
            activeClients: 15,
            status: "active",
            joinDate: "2023-06-15",
            lastActive: "2024-01-20T10:30:00Z",
            applicationId: 100,
            certifications: [
                {
                    id: 10,
                    name: "Chứng chỉ tâm lý học",
                    issuer: "Đại học Y Hà Nội",
                    issueDate: "2020-05-15",
                    expiryDate: "2025-05-15",
                    status: "verified",
                    type: "psychology",
                    fileUrl: "/certificates/psychology-cert.pdf",
                    fileName: "psychology-certificate.pdf",
                    uploadDate: "2023-06-10",
                },
            ],
        },
    ])

    const handleApproveApplication = (applicationId: number) => {
        const application = applications.find((app) => app.id === applicationId)
        if (!application) return

        // Create new coach from application
        const newCoach: Coach = {
            id: Date.now(),
            name: application.userName,
            email: application.userEmail,
            avatar: application.userAvatar,
            specialization: application.specialization.join(", "),
            experience: 0, // New coach
            rating: 0,
            totalSessions: 0,
            activeClients: 0,
            status: "active",
            joinDate: new Date().toISOString(),
            lastActive: new Date().toISOString(),
            applicationId: application.id,
            certifications: application.certificates,
        }

        // Update application status
        setApplications((prev) =>
            prev.map((app) =>
                app.id === applicationId
                    ? { ...app, status: "approved", reviewDate: new Date().toISOString(), adminFeedback: reviewFeedback }
                    : app,
            ),
        )

        // Add to coaches list
        setCoaches((prev) => [...prev, newCoach])
        setReviewDialogOpen(false)
        setReviewFeedback("")
    }

    const handleRejectApplication = (applicationId: number) => {
        setApplications((prev) =>
            prev.map((app) =>
                app.id === applicationId
                    ? { ...app, status: "rejected", reviewDate: new Date().toISOString(), adminFeedback: reviewFeedback }
                    : app,
            ),
        )
        setReviewDialogOpen(false)
        setReviewFeedback("")
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "active":
            case "approved":
            case "verified":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "inactive":
            case "rejected":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "pending":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "on-leave":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getRatingStars = (rating: number) => {
        return Array.from({ length: 5 }, (_, i) => (
            <Star
                key={i}
                className={`w-3 h-3 ${i < Math.floor(rating) ? "fill-yellow-400 text-yellow-400" : "text-gray-300 dark:text-gray-600"}`}
            />
        ))
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex justify-between items-center">
                        <div>
                            <CardTitle className="text-slate-900 dark:text-white flex items-center space-x-2">
                                <UserCheck className="w-5 h-5 text-blue-600" />
                                <span>Quản Lý Coach</span>
                            </CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Quản lý đội ngũ coach, đơn đăng ký và chứng chỉ
                            </CardDescription>
                        </div>
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
                </CardHeader>
            </Card>

            {/* Tabs */}
            <Tabs value={activeTab} onValueChange={setActiveTab}>
                <TabsList className="grid w-full grid-cols-2">
                    <TabsTrigger value="applications" className="flex items-center space-x-2">
                        <Award className="w-4 h-4" />
                        <span>Đơn đăng ký ({applications.filter((app) => app.status === "pending").length})</span>
                    </TabsTrigger>
                    <TabsTrigger value="coaches" className="flex items-center space-x-2">
                        <Users className="w-4 h-4" />
                        <span>Coach hoạt động ({coaches.length})</span>
                    </TabsTrigger>
                </TabsList>

                {/* Applications Tab */}
                <TabsContent value="applications" className="space-y-4">
                    {applications.length === 0 ? (
                        <Card>
                            <CardContent className="text-center py-8">
                                <Award className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                                <p className="text-gray-500">Chưa có đơn đăng ký nào</p>
                            </CardContent>
                        </Card>
                    ) : (
                        applications.map((application) => (
                            <Card key={application.id} className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                                <CardContent className="p-6">
                                    <div className="flex items-start justify-between mb-4">
                                        <div className="flex items-start space-x-4 flex-1">
                                            <Avatar className="h-12 w-12">
                                                <AvatarImage src={application.userAvatar || "/placeholder.svg"} />
                                                <AvatarFallback>{application.userName.split(" ").pop()?.charAt(0)}</AvatarFallback>
                                            </Avatar>
                                            <div className="flex-1">
                                                <div className="flex items-center space-x-3 mb-2">
                                                    <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
                                                        {application.userName}
                                                    </h3>
                                                    <Badge className={getStatusColor(application.status)}>
                                                        {application.status === "pending"
                                                            ? "Chờ duyệt"
                                                            : application.status === "approved"
                                                                ? "Đã duyệt"
                                                                : "Bị từ chối"}
                                                    </Badge>
                                                </div>
                                                <p className="text-slate-600 dark:text-slate-400 mb-2">{application.userEmail}</p>
                                                <p className="text-sm text-slate-500 dark:text-slate-400 mb-3">
                                                    Nộp đơn: {new Date(application.submissionDate).toLocaleDateString("vi-VN")}
                                                </p>

                                                <div className="mb-3">
                                                    <h4 className="font-medium text-slate-900 dark:text-white mb-1">Chuyên môn:</h4>
                                                    <div className="flex flex-wrap gap-2">
                                                        {application.specialization.map((spec, index) => (
                                                            <Badge key={index} variant="outline" className="text-xs">
                                                                {spec}
                                                            </Badge>
                                                        ))}
                                                    </div>
                                                </div>

                                                <div className="mb-3">
                                                    <h4 className="font-medium text-slate-900 dark:text-white mb-1">
                                                        Chứng chỉ ({application.certificates.length}):
                                                    </h4>
                                                    <div className="space-y-1">
                                                        {application.certificates.map((cert) => (
                                                            <div key={cert.id} className="flex items-center justify-between text-sm">
                                                                <span className="text-slate-700 dark:text-slate-300">{cert.name}</span>
                                                                <Badge className={getStatusColor(cert.status)} variant="outline">
                                                                    {cert.status === "verified"
                                                                        ? "Đã xác minh"
                                                                        : cert.status === "pending"
                                                                            ? "Chờ xác minh"
                                                                            : "Bị từ chối"}
                                                                </Badge>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div className="flex space-x-2">
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => {
                                                    setSelectedApplication(application)
                                                    setApplicationDialogOpen(true)
                                                }}
                                            >
                                                <Eye className="w-4 h-4 mr-1" />
                                                Xem chi tiết
                                            </Button>
                                            {application.status === "pending" && (
                                                <Button
                                                    size="sm"
                                                    onClick={() => {
                                                        setSelectedApplication(application)
                                                        setReviewDialogOpen(true)
                                                    }}
                                                    className="bg-blue-600 hover:bg-blue-700"
                                                >
                                                    <CheckCircle className="w-4 h-4 mr-1" />
                                                    Duyệt đơn
                                                </Button>
                                            )}
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))
                    )}
                </TabsContent>

                {/* Coaches Tab */}
                <TabsContent value="coaches" className="space-y-4">
                    {coaches.length === 0 ? (
                        <Card>
                            <CardContent className="text-center py-8">
                                <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                                <p className="text-gray-500">Chưa có coach nào</p>
                            </CardContent>
                        </Card>
                    ) : (
                        coaches.map((coach) => (
                            <Card key={coach.id} className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                                <CardContent className="p-6">
                                    <div className="flex items-start justify-between mb-6">
                                        <div className="flex items-start space-x-4 flex-1">
                                            <Avatar className="h-16 w-16">
                                                <AvatarImage src={coach.avatar || "/placeholder.svg"} />
                                                <AvatarFallback className="text-lg font-semibold">
                                                    {coach.name.split(" ").pop()?.charAt(0)}
                                                </AvatarFallback>
                                            </Avatar>
                                            <div className="flex-1">
                                                <div className="flex items-center space-x-3 mb-2">
                                                    <h3 className="text-xl font-semibold text-slate-900 dark:text-white">{coach.name}</h3>
                                                    <Badge className={getStatusColor(coach.status)}>
                                                        {coach.status === "active"
                                                            ? "Hoạt động"
                                                            : coach.status === "inactive"
                                                                ? "Không hoạt động"
                                                                : "Nghỉ phép"}
                                                    </Badge>
                                                </div>
                                                <p className="text-slate-600 dark:text-slate-400 mb-2">{coach.email}</p>
                                                <p className="text-slate-700 dark:text-slate-300 mb-4">
                                                    <strong>Chuyên môn:</strong> {coach.specialization}
                                                </p>

                                                {/* Stats */}
                                                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                                                    <div className="text-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                                        <div className="flex items-center justify-center space-x-1 mb-1">
                                                            {getRatingStars(coach.rating)}
                                                        </div>
                                                        <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                            {coach.rating || "N/A"}
                                                        </div>
                                                        <div className="text-xs text-slate-500 dark:text-slate-400">Đánh giá</div>
                                                    </div>
                                                    <div className="text-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                                        <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                            {coach.totalSessions}
                                                        </div>
                                                        <div className="text-xs text-slate-500 dark:text-slate-400">Buổi tư vấn</div>
                                                    </div>
                                                    <div className="text-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                                        <div className="text-lg font-semibold text-slate-900 dark:text-white">
                                                            {coach.activeClients}
                                                        </div>
                                                        <div className="text-xs text-slate-500 dark:text-slate-400">Khách hàng</div>
                                                    </div>
                                                    <div className="text-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                                        <div className="text-xs text-slate-500 dark:text-slate-400">Tham gia</div>
                                                        <div className="text-sm font-medium text-slate-900 dark:text-white">
                                                            {new Date(coach.joinDate).toLocaleDateString("vi-VN")}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="flex space-x-2">
                                            <Button
                                                size="sm"
                                                variant="outline"
                                                onClick={() => {
                                                    setSelectedCoach(coach)
                                                    setCoachDetailDialogOpen(true)
                                                }}
                                            >
                                                <Eye className="w-4 h-4" />
                                            </Button>
                                            <Button size="sm" variant="ghost">
                                                <MessageSquare className="w-4 h-4" />
                                            </Button>
                                            <Button size="sm" variant="ghost">
                                                <Edit className="w-4 h-4" />
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="ghost"
                                                className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/20"
                                            >
                                                <Trash2 className="w-4 h-4" />
                                            </Button>
                                        </div>
                                    </div>

                                    {/* Certifications */}
                                    <div className="border-t border-slate-200 dark:border-slate-700 pt-4">
                                        <h4 className="text-lg font-semibold text-slate-900 dark:text-white flex items-center space-x-2 mb-3">
                                            <FileText className="w-5 h-5 text-blue-600" />
                                            <span>Chứng chỉ ({coach.certifications.length})</span>
                                        </h4>
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {coach.certifications.map((cert) => (
                                                <div
                                                    key={cert.id}
                                                    className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-200 dark:border-slate-600"
                                                >
                                                    <div className="flex items-start justify-between mb-2">
                                                        <h5 className="font-medium text-slate-900 dark:text-white text-sm">{cert.name}</h5>
                                                        <Badge className={getStatusColor(cert.status)} variant="outline">
                                                            {cert.status === "verified"
                                                                ? "Đã xác minh"
                                                                : cert.status === "pending"
                                                                    ? "Chờ xác minh"
                                                                    : "Bị từ chối"}
                                                        </Badge>
                                                    </div>
                                                    <p className="text-xs text-slate-600 dark:text-slate-400 mb-1">Cấp bởi: {cert.issuer}</p>
                                                    <p className="text-xs text-slate-500 dark:text-slate-400">
                                                        Ngày cấp: {new Date(cert.issueDate).toLocaleDateString("vi-VN")}
                                                    </p>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))
                    )}
                </TabsContent>
            </Tabs>

            {/* Application Detail Dialog */}
            <Dialog open={applicationDialogOpen} onOpenChange={setApplicationDialogOpen}>
                <DialogContent className="sm:max-w-2xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chi tiết đơn đăng ký Coach</DialogTitle>
                        <DialogDescription>Thông tin chi tiết về đơn đăng ký trở thành Coach</DialogDescription>
                    </DialogHeader>

                    {selectedApplication && (
                        <div className="space-y-6">
                            {/* User Info */}
                            <div className="flex items-center space-x-4">
                                <Avatar className="h-16 w-16">
                                    <AvatarImage src={selectedApplication.userAvatar || "/placeholder.svg"} />
                                    <AvatarFallback>{selectedApplication.userName.split(" ").pop()?.charAt(0)}</AvatarFallback>
                                </Avatar>
                                <div>
                                    <h3 className="text-lg font-semibold">{selectedApplication.userName}</h3>
                                    <p className="text-slate-600 dark:text-slate-400">{selectedApplication.userEmail}</p>
                                    <Badge className={getStatusColor(selectedApplication.status)}>
                                        {selectedApplication.status === "pending"
                                            ? "Chờ duyệt"
                                            : selectedApplication.status === "approved"
                                                ? "Đã duyệt"
                                                : "Bị từ chối"}
                                    </Badge>
                                </div>
                            </div>

                            {/* Motivation */}
                            <div>
                                <Label className="text-base font-medium">Động lực trở thành Coach</Label>
                                <p className="mt-2 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg text-sm">
                                    {selectedApplication.motivation}
                                </p>
                            </div>

                            {/* Experience */}
                            <div>
                                <Label className="text-base font-medium">Kinh nghiệm</Label>
                                <p className="mt-2 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg text-sm">
                                    {selectedApplication.experience}
                                </p>
                            </div>

                            {/* Specialization */}
                            <div>
                                <Label className="text-base font-medium">Chuyên môn</Label>
                                <div className="mt-2 flex flex-wrap gap-2">
                                    {selectedApplication.specialization.map((spec, index) => (
                                        <Badge key={index} variant="outline">
                                            {spec}
                                        </Badge>
                                    ))}
                                </div>
                            </div>

                            {/* Certificates */}
                            <div>
                                <Label className="text-base font-medium">Chứng chỉ</Label>
                                <div className="mt-2 space-y-3">
                                    {selectedApplication.certificates.map((cert) => (
                                        <div key={cert.id} className="p-4 border rounded-lg">
                                            <div className="flex items-start justify-between mb-2">
                                                <div>
                                                    <h4 className="font-medium">{cert.name}</h4>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400">Cấp bởi: {cert.issuer}</p>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400">
                                                        Ngày cấp: {new Date(cert.issueDate).toLocaleDateString("vi-VN")}
                                                    </p>
                                                    {cert.expiryDate && (
                                                        <p className="text-sm text-slate-600 dark:text-slate-400">
                                                            Hết hạn: {new Date(cert.expiryDate).toLocaleDateString("vi-VN")}
                                                        </p>
                                                    )}
                                                </div>
                                                <div className="flex items-center space-x-2">
                                                    <Badge className={getStatusColor(cert.status)}>
                                                        {cert.status === "verified"
                                                            ? "Đã xác minh"
                                                            : cert.status === "pending"
                                                                ? "Chờ xác minh"
                                                                : "Bị từ chối"}
                                                    </Badge>
                                                    <Button variant="outline" size="sm">
                                                        <Download className="w-3 h-3" />
                                                    </Button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Admin Feedback */}
                            {selectedApplication.adminFeedback && (
                                <div>
                                    <Label className="text-base font-medium">Phản hồi từ Admin</Label>
                                    <p className="mt-2 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg text-sm">
                                        {selectedApplication.adminFeedback}
                                    </p>
                                </div>
                            )}
                        </div>
                    )}
                </DialogContent>
            </Dialog>

            {/* Review Application Dialog */}
            <Dialog open={reviewDialogOpen} onOpenChange={setReviewDialogOpen}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Duyệt đơn đăng ký</DialogTitle>
                        <DialogDescription>Phê duyệt hoặc từ chối đơn đăng ký trở thành Coach</DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4">
                        <div>
                            <Label htmlFor="feedback">Phản hồi (tùy chọn)</Label>
                            <Textarea
                                id="feedback"
                                value={reviewFeedback}
                                onChange={(e) => setReviewFeedback(e.target.value)}
                                placeholder="Nhập phản hồi cho ứng viên..."
                                rows={3}
                            />
                        </div>
                    </div>

                    <div className="flex justify-end space-x-2">
                        <Button
                            variant="outline"
                            onClick={() => selectedApplication && handleRejectApplication(selectedApplication.id)}
                            className="text-red-600 hover:text-red-700"
                        >
                            <XCircle className="w-4 h-4 mr-1" />
                            Từ chối
                        </Button>
                        <Button
                            onClick={() => selectedApplication && handleApproveApplication(selectedApplication.id)}
                            className="bg-green-600 hover:bg-green-700"
                        >
                            <CheckCircle className="w-4 h-4 mr-1" />
                            Phê duyệt
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Coach Detail Dialog */}
            <Dialog open={coachDetailDialogOpen} onOpenChange={setCoachDetailDialogOpen}>
                <DialogContent className="sm:max-w-2xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chi tiết Coach</DialogTitle>
                        <DialogDescription>Thông tin chi tiết về Coach và chứng chỉ</DialogDescription>
                    </DialogHeader>

                    {selectedCoach && (
                        <div className="space-y-6">
                            {/* Coach Info */}
                            <div className="flex items-center space-x-4">
                                <Avatar className="h-16 w-16">
                                    <AvatarImage src={selectedCoach.avatar || "/placeholder.svg"} />
                                    <AvatarFallback>{selectedCoach.name.split(" ").pop()?.charAt(0)}</AvatarFallback>
                                </Avatar>
                                <div>
                                    <h3 className="text-lg font-semibold">{selectedCoach.name}</h3>
                                    <p className="text-slate-600 dark:text-slate-400">{selectedCoach.email}</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">
                                        Chuyên môn: {selectedCoach.specialization}
                                    </p>
                                </div>
                            </div>

                            {/* Performance Stats */}
                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                <div className="text-center p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                    <div className="text-2xl font-bold text-slate-900 dark:text-white">
                                        {selectedCoach.rating || "N/A"}
                                    </div>
                                    <div className="text-sm text-slate-500 dark:text-slate-400">Đánh giá</div>
                                </div>
                                <div className="text-center p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                    <div className="text-2xl font-bold text-slate-900 dark:text-white">{selectedCoach.totalSessions}</div>
                                    <div className="text-sm text-slate-500 dark:text-slate-400">Buổi tư vấn</div>
                                </div>
                                <div className="text-center p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                    <div className="text-2xl font-bold text-slate-900 dark:text-white">{selectedCoach.activeClients}</div>
                                    <div className="text-sm text-slate-500 dark:text-slate-400">Khách hàng</div>
                                </div>
                                <div className="text-center p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                    <div className="text-sm text-slate-500 dark:text-slate-400">Tham gia</div>
                                    <div className="text-sm font-medium text-slate-900 dark:text-white">
                                        {new Date(selectedCoach.joinDate).toLocaleDateString("vi-VN")}
                                    </div>
                                </div>
                            </div>

                            {/* Certifications */}
                            <div>
                                <Label className="text-base font-medium">Chứng chỉ</Label>
                                <div className="mt-2 space-y-3">
                                    {selectedCoach.certifications.map((cert) => (
                                        <div key={cert.id} className="p-4 border rounded-lg">
                                            <div className="flex items-start justify-between mb-2">
                                                <div>
                                                    <h4 className="font-medium">{cert.name}</h4>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400">Cấp bởi: {cert.issuer}</p>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400">
                                                        Ngày cấp: {new Date(cert.issueDate).toLocaleDateString("vi-VN")}
                                                    </p>
                                                    {cert.expiryDate && (
                                                        <p className="text-sm text-slate-600 dark:text-slate-400">
                                                            Hết hạn: {new Date(cert.expiryDate).toLocaleDateString("vi-VN")}
                                                        </p>
                                                    )}
                                                </div>
                                                <div className="flex items-center space-x-2">
                                                    <Badge className={getStatusColor(cert.status)}>
                                                        {cert.status === "verified"
                                                            ? "Đã xác minh"
                                                            : cert.status === "pending"
                                                                ? "Chờ xác minh"
                                                                : "Bị từ chối"}
                                                    </Badge>
                                                    <Button variant="outline" size="sm">
                                                        <Download className="w-3 h-3" />
                                                    </Button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}
