"use client"

import { useState } from "react"
import { motion } from "framer-motion"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import {
    UserPlus,
    Calendar,
    Award,
    CheckCircle,
    Clock,
    Mail,
    Phone,
    FileText,
    Eye,
    Download,
    XCircle,
    AlertTriangle,
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

interface EligibleUser {
    id: number
    name: string
    email: string
    phone: string
    joinDate: string
    successRate: number
    completedPlans: number
    helpedUsers: number
    avatar?: string
    status: "active" | "inactive"
}

interface CoachApplication {
    id: number
    user: EligibleUser
    applicationDate: string
    motivation: string
    experience: string
    specialization: string[]
    certificates: Certificate[]
    status: "pending" | "approved" | "rejected" | "under_review"
    adminFeedback?: string
    reviewDate?: string
    reviewedBy?: string
}

const mockCertificates: Certificate[] = [
    {
        id: 1,
        name: "Chứng chỉ Tư vấn Sức khỏe",
        issuer: "Bộ Y tế",
        issueDate: "2023-06-15",
        expiryDate: "2025-06-15",
        type: "health",
        status: "pending",
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
        status: "verified",
        fileUrl: "/certificates/psychology-cert.pdf",
        fileName: "applied-psychology-cert.pdf",
        uploadDate: "2024-01-15",
    },
]

const mockEligibleUsers: EligibleUser[] = [
    {
        id: 1,
        name: "Nguyễn Văn An",
        email: "an.nguyen@example.com",
        phone: "+84 901 234 567",
        joinDate: "2023-01-15",
        successRate: 95,
        completedPlans: 3,
        helpedUsers: 12,
        avatar: "/placeholder.svg?height=40&width=40",
        status: "active",
    },
    {
        id: 2,
        name: "Trần Thị Bình",
        email: "binh.tran@example.com",
        phone: "+84 902 345 678",
        joinDate: "2023-02-20",
        successRate: 88,
        completedPlans: 2,
        helpedUsers: 8,
        avatar: "/placeholder.svg?height=40&width=40",
        status: "active",
    },
]

const mockApplications: CoachApplication[] = [
    {
        id: 1,
        user: mockEligibleUsers[0],
        applicationDate: "2024-01-15",
        motivation:
            "Tôi muốn giúp đỡ những người khác vượt qua thói quen hút thuốc như tôi đã làm. Sau khi thành công cai thuốc được 2 năm, tôi nhận ra rằng việc có người đồng hành và hỗ trợ là rất quan trọng trong hành trình này.",
        experience:
            "Đã thành công cai thuốc được 2 năm, từng hỗ trợ bạn bè và gia đình cai thuốc. Có kinh nghiệm tư vấn sức khỏe tại phòng khám địa phương.",
        specialization: ["Cai thuốc dần dần", "Hỗ trợ tâm lý"],
        certificates: mockCertificates,
        status: "pending",
    },
    {
        id: 2,
        user: mockEligibleUsers[1],
        applicationDate: "2024-01-10",
        motivation: "Muốn chia sẻ kinh nghiệm và động lực để giúp cộng đồng.",
        experience: "Cai thuốc thành công, có kinh nghiệm tư vấn sức khỏe.",
        specialization: ["Phương pháp tự nhiên", "Dinh dưỡng"],
        certificates: [mockCertificates[1]],
        status: "approved",
        adminFeedback: "Hồ sơ tốt, chứng chỉ hợp lệ. Chấp thuận làm Coach.",
        reviewDate: "2024-01-20",
        reviewedBy: "Admin Nguyễn",
    },
]

export default function CoachPromotion() {
    const [applications, setApplications] = useState<CoachApplication[]>(mockApplications)
    const [selectedApplication, setSelectedApplication] = useState<CoachApplication | null>(null)
    const [applicationDialogOpen, setApplicationDialogOpen] = useState(false)
    const [certificateDialogOpen, setCertificateDialogOpen] = useState(false)
    const [selectedCertificate, setSelectedCertificate] = useState<Certificate | null>(null)
    const [adminFeedback, setAdminFeedback] = useState("")
    const [certificateReview, setCertificateReview] = useState({
        status: "verified" as Certificate["status"],
        notes: "",
    })

    const handleViewApplication = (application: CoachApplication) => {
        setSelectedApplication(application)
        setApplicationDialogOpen(true)
        setAdminFeedback(application.adminFeedback || "")
    }

    const handleApproveApplication = (applicationId: number) => {
        setApplications((prev) =>
            prev.map((app) =>
                app.id === applicationId
                    ? {
                        ...app,
                        status: "approved" as const,
                        adminFeedback,
                        reviewDate: new Date().toISOString().split("T")[0],
                        reviewedBy: "Admin hiện tại",
                    }
                    : app,
            ),
        )
        setApplicationDialogOpen(false)
        setAdminFeedback("")
    }

    const handleRejectApplication = (applicationId: number) => {
        setApplications((prev) =>
            prev.map((app) =>
                app.id === applicationId
                    ? {
                        ...app,
                        status: "rejected" as const,
                        adminFeedback,
                        reviewDate: new Date().toISOString().split("T")[0],
                        reviewedBy: "Admin hiện tại",
                    }
                    : app,
            ),
        )
        setApplicationDialogOpen(false)
        setAdminFeedback("")
    }

    const handleViewCertificate = (certificate: Certificate) => {
        setSelectedCertificate(certificate)
        setCertificateDialogOpen(true)
        setCertificateReview({
            status: certificate.status,
            notes: certificate.adminNotes || "",
        })
    }

    const handleCertificateReview = () => {
        if (!selectedCertificate) return

        // Update certificate status
        setApplications((prev) =>
            prev.map((app) => ({
                ...app,
                certificates: app.certificates.map((cert) =>
                    cert.id === selectedCertificate.id
                        ? {
                            ...cert,
                            status: certificateReview.status,
                            adminNotes: certificateReview.notes,
                        }
                        : cert,
                ),
            })),
        )

        setCertificateDialogOpen(false)
    }

    const getStatusBadge = (status: CoachApplication["status"]) => {
        switch (status) {
            case "pending":
                return (
                    <Badge variant="outline" className="bg-yellow-50 text-yellow-700 border-yellow-200">
                        <Clock className="h-3 w-3 mr-1" />
                        Chờ duyệt
                    </Badge>
                )
            case "under_review":
                return (
                    <Badge variant="outline" className="bg-blue-50 text-blue-700 border-blue-200">
                        <Eye className="h-3 w-3 mr-1" />
                        Đang xem xét
                    </Badge>
                )
            case "approved":
                return (
                    <Badge variant="outline" className="bg-green-50 text-green-700 border-green-200">
                        <CheckCircle className="h-3 w-3 mr-1" />
                        Đã duyệt
                    </Badge>
                )
            case "rejected":
                return (
                    <Badge variant="outline" className="bg-red-50 text-red-700 border-red-200">
                        <XCircle className="h-3 w-3 mr-1" />
                        Từ chối
                    </Badge>
                )
        }
    }

    const getCertificateStatusBadge = (status: Certificate["status"]) => {
        switch (status) {
            case "verified":
                return (
                    <Badge className="bg-green-50 text-green-700 border-green-200">
                        <CheckCircle className="h-3 w-3 mr-1" />
                        Đã xác minh
                    </Badge>
                )
            case "pending":
                return (
                    <Badge className="bg-yellow-50 text-yellow-700 border-yellow-200">
                        <Clock className="h-3 w-3 mr-1" />
                        Chờ xác minh
                    </Badge>
                )
            case "rejected":
                return (
                    <Badge className="bg-red-50 text-red-700 border-red-200">
                        <XCircle className="h-3 w-3 mr-1" />
                        Bị từ chối
                    </Badge>
                )
        }
    }

    const getPendingCertificates = () => {
        return applications.reduce(
            (total, app) => total + app.certificates.filter((cert) => cert.status === "pending").length,
            0,
        )
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex items-center justify-between"
            >
                <div>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Thăng cấp Coach</h2>
                    <p className="text-gray-600 dark:text-gray-400">Xem xét đơn đăng ký và chứng chỉ để thăng cấp Coach</p>
                </div>
            </motion.div>

            {/* Stats Cards */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="grid grid-cols-1 md:grid-cols-4 gap-4"
            >
                <Card>
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-2">
                            <UserPlus className="h-5 w-5 text-blue-600" />
                            <div>
                                <p className="text-sm text-gray-600 dark:text-gray-400">Đơn đăng ký</p>
                                <p className="text-2xl font-bold text-gray-900 dark:text-white">{applications.length}</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-2">
                            <Clock className="h-5 w-5 text-yellow-600" />
                            <div>
                                <p className="text-sm text-gray-600 dark:text-gray-400">Chờ duyệt</p>
                                <p className="text-2xl font-bold text-gray-900 dark:text-white">
                                    {applications.filter((app) => app.status === "pending").length}
                                </p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-2">
                            <FileText className="h-5 w-5 text-purple-600" />
                            <div>
                                <p className="text-sm text-gray-600 dark:text-gray-400">Chứng chỉ chờ xác minh</p>
                                <p className="text-2xl font-bold text-gray-900 dark:text-white">{getPendingCertificates()}</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-2">
                            <Award className="h-5 w-5 text-green-600" />
                            <div>
                                <p className="text-sm text-gray-600 dark:text-gray-400">Coach đã duyệt</p>
                                <p className="text-2xl font-bold text-gray-900 dark:text-white">
                                    {applications.filter((app) => app.status === "approved").length}
                                </p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </motion.div>

            {/* Coach Applications */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2">
                            <Award className="h-5 w-5 text-purple-500" />
                            <span>Đơn đăng ký Coach</span>
                        </CardTitle>
                        <CardDescription>Xem xét và phê duyệt các đơn đăng ký trở thành Coach</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {applications.map((application, index) => (
                                <motion.div
                                    key={application.id}
                                    initial={{ opacity: 0, x: -20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    transition={{ delay: index * 0.1 }}
                                    className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                                >
                                    <div className="flex items-center space-x-4">
                                        <Avatar>
                                            <AvatarImage src={application.user.avatar || "/placeholder.svg"} />
                                            <AvatarFallback>{application.user.name.charAt(0)}</AvatarFallback>
                                        </Avatar>
                                        <div>
                                            <h4 className="font-semibold text-gray-900 dark:text-white">{application.user.name}</h4>
                                            <div className="flex items-center space-x-4 text-sm text-gray-600 dark:text-gray-400">
                                                <span className="flex items-center space-x-1">
                                                    <Calendar className="h-3 w-3" />
                                                    <span>Nộp đơn: {application.applicationDate}</span>
                                                </span>
                                                <span>Chuyên môn: {application.specialization.join(", ")}</span>
                                                <span className="flex items-center space-x-1">
                                                    <FileText className="h-3 w-3" />
                                                    <span>{application.certificates.length} chứng chỉ</span>
                                                </span>
                                            </div>
                                            <div className="flex items-center space-x-2 mt-1">
                                                <Badge variant="outline" className="bg-green-50 text-green-700 text-xs">
                                                    {application.user.successRate}% thành công
                                                </Badge>
                                                <Badge variant="outline" className="bg-blue-50 text-blue-700 text-xs">
                                                    {application.certificates.filter((cert) => cert.status === "verified").length} chứng chỉ đã
                                                    xác minh
                                                </Badge>
                                                {application.certificates.some((cert) => cert.status === "pending") && (
                                                    <Badge variant="outline" className="bg-yellow-50 text-yellow-700 text-xs">
                                                        <AlertTriangle className="h-3 w-3 mr-1" />
                                                        Có chứng chỉ chờ xác minh
                                                    </Badge>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex items-center space-x-4">
                                        {getStatusBadge(application.status)}
                                        <Button onClick={() => handleViewApplication(application)} variant="outline" size="sm">
                                            Xem chi tiết
                                        </Button>
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </motion.div>

            {/* Application Review Dialog */}
            <Dialog open={applicationDialogOpen} onOpenChange={setApplicationDialogOpen}>
                <DialogContent className="sm:max-w-4xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chi tiết đơn đăng ký Coach</DialogTitle>
                        <DialogDescription>Xem xét thông tin và quyết định phê duyệt đơn đăng ký</DialogDescription>
                    </DialogHeader>

                    {selectedApplication && (
                        <div className="space-y-6">
                            {/* User Info */}
                            <div className="flex items-center space-x-4 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                <Avatar className="h-12 w-12">
                                    <AvatarImage src={selectedApplication.user.avatar || "/placeholder.svg"} />
                                    <AvatarFallback>{selectedApplication.user.name.charAt(0)}</AvatarFallback>
                                </Avatar>
                                <div>
                                    <h4 className="font-semibold text-gray-900 dark:text-white">{selectedApplication.user.name}</h4>
                                    <div className="flex items-center space-x-4 text-sm text-gray-600 dark:text-gray-400">
                                        <span className="flex items-center space-x-1">
                                            <Mail className="h-3 w-3" />
                                            <span>{selectedApplication.user.email}</span>
                                        </span>
                                        <span className="flex items-center space-x-1">
                                            <Phone className="h-3 w-3" />
                                            <span>{selectedApplication.user.phone}</span>
                                        </span>
                                    </div>
                                </div>
                            </div>

                            {/* Application Details */}
                            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                <div className="space-y-4">
                                    <div>
                                        <Label className="text-sm font-medium">Động lực</Label>
                                        <p className="mt-1 text-sm text-gray-600 dark:text-gray-400 p-3 bg-gray-50 dark:bg-gray-800 rounded">
                                            {selectedApplication.motivation}
                                        </p>
                                    </div>

                                    <div>
                                        <Label className="text-sm font-medium">Kinh nghiệm</Label>
                                        <p className="mt-1 text-sm text-gray-600 dark:text-gray-400 p-3 bg-gray-50 dark:bg-gray-800 rounded">
                                            {selectedApplication.experience}
                                        </p>
                                    </div>

                                    <div>
                                        <Label className="text-sm font-medium">Chuyên môn</Label>
                                        <div className="mt-1 flex flex-wrap gap-2">
                                            {selectedApplication.specialization.map((spec, index) => (
                                                <Badge key={index} variant="outline" className="bg-blue-50 text-blue-700">
                                                    {spec}
                                                </Badge>
                                            ))}
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-3 gap-4 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                        <div className="text-center">
                                            <p className="text-2xl font-bold text-green-600">{selectedApplication.user.successRate}%</p>
                                            <p className="text-xs text-gray-600 dark:text-gray-400">Tỷ lệ thành công</p>
                                        </div>
                                        <div className="text-center">
                                            <p className="text-2xl font-bold text-blue-600">{selectedApplication.user.completedPlans}</p>
                                            <p className="text-xs text-gray-600 dark:text-gray-400">Kế hoạch hoàn thành</p>
                                        </div>
                                        <div className="text-center">
                                            <p className="text-2xl font-bold text-purple-600">{selectedApplication.user.helpedUsers}</p>
                                            <p className="text-xs text-gray-600 dark:text-gray-400">Người đã hỗ trợ</p>
                                        </div>
                                    </div>
                                </div>

                                {/* Certificates */}
                                <div>
                                    <Label className="text-sm font-medium">Chứng chỉ ({selectedApplication.certificates.length})</Label>
                                    <div className="mt-2 space-y-3 max-h-96 overflow-y-auto">
                                        {selectedApplication.certificates.map((cert) => (
                                            <div key={cert.id} className="p-3 border rounded-lg">
                                                <div className="flex items-center justify-between mb-2">
                                                    <h5 className="font-medium text-sm">{cert.name}</h5>
                                                    {getCertificateStatusBadge(cert.status)}
                                                </div>
                                                <div className="text-xs text-gray-600 dark:text-gray-400 space-y-1">
                                                    <p>Cấp bởi: {cert.issuer}</p>
                                                    <p>Ngày cấp: {cert.issueDate}</p>
                                                    {cert.expiryDate && <p>Hết hạn: {cert.expiryDate}</p>}
                                                </div>
                                                <div className="flex space-x-2 mt-2">
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => handleViewCertificate(cert)}
                                                        className="text-xs"
                                                    >
                                                        <Eye className="h-3 w-3 mr-1" />
                                                        Xem xét
                                                    </Button>
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => {
                                                            const link = document.createElement("a")
                                                            link.href = cert.fileUrl
                                                            link.download = cert.fileName
                                                            link.click()
                                                        }}
                                                        className="text-xs"
                                                    >
                                                        <Download className="h-3 w-3 mr-1" />
                                                        Tải về
                                                    </Button>
                                                </div>
                                                {cert.adminNotes && (
                                                    <div className="mt-2 p-2 bg-yellow-50 dark:bg-yellow-900/20 rounded text-xs">
                                                        <p className="font-medium text-yellow-800 dark:text-yellow-200">Ghi chú Admin:</p>
                                                        <p className="text-yellow-700 dark:text-yellow-300">{cert.adminNotes}</p>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>

                            {/* Admin Feedback */}
                            <div>
                                <Label htmlFor="admin-feedback">Phản hồi của Admin</Label>
                                <Textarea
                                    id="admin-feedback"
                                    value={adminFeedback}
                                    onChange={(e) => setAdminFeedback(e.target.value)}
                                    placeholder="Nhập phản hồi cho ứng viên..."
                                    rows={3}
                                />
                            </div>

                            {/* Actions */}
                            {selectedApplication.status === "pending" && (
                                <div className="flex justify-end space-x-2">
                                    <Button
                                        variant="outline"
                                        onClick={() => handleRejectApplication(selectedApplication.id)}
                                        className="text-red-600 hover:text-red-700 hover:bg-red-50"
                                    >
                                        Từ chối
                                    </Button>
                                    <Button
                                        onClick={() => handleApproveApplication(selectedApplication.id)}
                                        className="bg-green-600 hover:bg-green-700"
                                        disabled={!selectedApplication.certificates.some((cert) => cert.status === "verified")}
                                    >
                                        Phê duyệt
                                    </Button>
                                </div>
                            )}

                            {selectedApplication.status !== "pending" && selectedApplication.adminFeedback && (
                                <div className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                    <Label className="text-sm font-medium">Phản hồi trước đó</Label>
                                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{selectedApplication.adminFeedback}</p>
                                    {selectedApplication.reviewDate && (
                                        <p className="text-xs text-gray-500 mt-2">
                                            Xem xét ngày {selectedApplication.reviewDate} bởi {selectedApplication.reviewedBy}
                                        </p>
                                    )}
                                </div>
                            )}
                        </div>
                    )}
                </DialogContent>
            </Dialog>

            {/* Certificate Review Dialog */}
            <Dialog open={certificateDialogOpen} onOpenChange={setCertificateDialogOpen}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Xác minh chứng chỉ</DialogTitle>
                        <DialogDescription>Xem xét và xác minh tính hợp lệ của chứng chỉ</DialogDescription>
                    </DialogHeader>

                    {selectedCertificate && (
                        <div className="space-y-4">
                            <div className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                <h4 className="font-medium">{selectedCertificate.name}</h4>
                                <div className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                                    <p>Cấp bởi: {selectedCertificate.issuer}</p>
                                    <p>Ngày cấp: {selectedCertificate.issueDate}</p>
                                    {selectedCertificate.expiryDate && <p>Hết hạn: {selectedCertificate.expiryDate}</p>}
                                </div>
                            </div>

                            <div>
                                <Label htmlFor="cert-status">Trạng thái xác minh</Label>
                                <select
                                    id="cert-status"
                                    value={certificateReview.status}
                                    onChange={(e) =>
                                        setCertificateReview((prev) => ({
                                            ...prev,
                                            status: e.target.value as Certificate["status"],
                                        }))
                                    }
                                    className="w-full p-2 border rounded-md mt-1"
                                >
                                    <option value="verified">Đã xác minh</option>
                                    <option value="pending">Chờ xác minh</option>
                                    <option value="rejected">Từ chối</option>
                                </select>
                            </div>

                            <div>
                                <Label htmlFor="cert-notes">Ghi chú</Label>
                                <Textarea
                                    id="cert-notes"
                                    value={certificateReview.notes}
                                    onChange={(e) =>
                                        setCertificateReview((prev) => ({
                                            ...prev,
                                            notes: e.target.value,
                                        }))
                                    }
                                    placeholder="Ghi chú về chứng chỉ..."
                                    rows={3}
                                />
                            </div>

                            <div className="flex justify-end space-x-2">
                                <Button variant="outline" onClick={() => setCertificateDialogOpen(false)}>
                                    Hủy
                                </Button>
                                <Button onClick={handleCertificateReview} className="bg-blue-600 hover:bg-blue-700">
                                    Lưu xác minh
                                </Button>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}
