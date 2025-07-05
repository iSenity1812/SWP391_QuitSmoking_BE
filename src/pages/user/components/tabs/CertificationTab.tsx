"use client"

import type React from "react"

import { useState } from "react"
import { motion } from "framer-motion"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Upload, FileText, Award, Clock, CheckCircle, XCircle, Eye, Download, Plus, AlertCircle } from "lucide-react"

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
    status: "draft" | "submitted" | "under_review" | "approved" | "rejected"
    submissionDate?: string
    motivation: string
    experience: string
    specialization: string[]
    certificates: Certificate[]
    adminFeedback?: string
    reviewDate?: string
}

const mockCertificates: Certificate[] = [
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
]

const mockApplication: CoachApplication = {
    id: 1,
    status: "draft",
    motivation: "",
    experience: "",
    specialization: [],
    certificates: mockCertificates,
}

export default function CertificationTab() {
    const [certificates, setCertificates] = useState<Certificate[]>(mockCertificates)
    const [application, setApplication] = useState<CoachApplication>(mockApplication)
    const [uploadDialogOpen, setUploadDialogOpen] = useState(false)
    const [applicationDialogOpen, setApplicationDialogOpen] = useState(false)
    const [viewCertDialogOpen, setViewCertDialogOpen] = useState(false)
    const [selectedCertificate, setSelectedCertificate] = useState<Certificate | null>(null)

    // Form states
    const [newCertificate, setNewCertificate] = useState({
        name: "",
        issuer: "",
        issueDate: "",
        expiryDate: "",
        type: "other" as Certificate["type"],
        file: null as File | null,
    })

    const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0]
        if (file) {
            setNewCertificate((prev) => ({ ...prev, file }))
        }
    }

    const handleUploadCertificate = () => {
        if (!newCertificate.file || !newCertificate.name || !newCertificate.issuer) {
            return
        }

        const certificate: Certificate = {
            id: Date.now(),
            name: newCertificate.name,
            issuer: newCertificate.issuer,
            issueDate: newCertificate.issueDate,
            expiryDate: newCertificate.expiryDate || undefined,
            type: newCertificate.type,
            status: "pending",
            fileUrl: URL.createObjectURL(newCertificate.file),
            fileName: newCertificate.file.name,
            uploadDate: new Date().toISOString().split("T")[0],
        }

        setCertificates((prev) => [...prev, certificate])
        setNewCertificate({
            name: "",
            issuer: "",
            issueDate: "",
            expiryDate: "",
            type: "other",
            file: null,
        })
        setUploadDialogOpen(false)
    }

    const handleSubmitApplication = () => {
        const updatedApplication: CoachApplication = {
            ...application,
            status: "submitted",
            submissionDate: new Date().toISOString().split("T")[0],
            certificates: certificates,
        }
        setApplication(updatedApplication)
        setApplicationDialogOpen(false)
    }

    const getStatusBadge = (status: Certificate["status"]) => {
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

    const getApplicationStatusBadge = (status: CoachApplication["status"]) => {
        switch (status) {
            case "draft":
                return <Badge variant="outline">Bản nháp</Badge>
            case "submitted":
                return <Badge className="bg-blue-50 text-blue-700">Đã nộp</Badge>
            case "under_review":
                return <Badge className="bg-yellow-50 text-yellow-700">Đang xem xét</Badge>
            case "approved":
                return <Badge className="bg-green-50 text-green-700">Đã duyệt</Badge>
            case "rejected":
                return <Badge className="bg-red-50 text-red-700">Bị từ chối</Badge>
        }
    }

    const canSubmitApplication = () => {
        return (
            certificates.some((cert) => cert.status === "verified") &&
            application.motivation.trim() &&
            application.experience.trim() &&
            application.specialization.length > 0
        )
    }

    return (
        <div className="space-y-6">
            {/* Application Status */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
                <Card>
                    <CardHeader>
                        <div className="flex items-center justify-between">
                            <div>
                                <CardTitle className="flex items-center space-x-2">
                                    <Award className="h-5 w-5 text-purple-600" />
                                    <span>Đơn đăng ký Coach</span>
                                </CardTitle>
                                <CardDescription>Trạng thái đơn đăng ký trở thành Coach của bạn</CardDescription>
                            </div>
                            {getApplicationStatusBadge(application.status)}
                        </div>
                    </CardHeader>
                    <CardContent>
                        {application.status === "draft" && (
                            <div className="flex items-center justify-between p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                                <div className="flex items-center space-x-3">
                                    <AlertCircle className="h-5 w-5 text-blue-600" />
                                    <div>
                                        <p className="font-medium text-blue-900 dark:text-blue-100">Sẵn sàng trở thành Coach?</p>
                                        <p className="text-sm text-blue-700 dark:text-blue-300">
                                            Hoàn thiện hồ sơ và nộp đơn đăng ký để trở thành Coach
                                        </p>
                                    </div>
                                </div>
                                <Button onClick={() => setApplicationDialogOpen(true)} className="bg-blue-600 hover:bg-blue-700">
                                    Nộp đơn đăng ký
                                </Button>
                            </div>
                        )}

                        {application.status === "submitted" && (
                            <div className="p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
                                <div className="flex items-center space-x-3">
                                    <Clock className="h-5 w-5 text-yellow-600" />
                                    <div>
                                        <p className="font-medium text-yellow-900 dark:text-yellow-100">Đơn đã được nộp</p>
                                        <p className="text-sm text-yellow-700 dark:text-yellow-300">
                                            Đơn đăng ký của bạn đang được admin xem xét. Chúng tôi sẽ thông báo kết quả sớm nhất.
                                        </p>
                                        {application.submissionDate && (
                                            <p className="text-xs text-yellow-600 mt-1">Nộp ngày: {application.submissionDate}</p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}

                        {application.status === "approved" && (
                            <div className="p-4 bg-green-50 dark:bg-green-900/20 rounded-lg">
                                <div className="flex items-center space-x-3">
                                    <CheckCircle className="h-5 w-5 text-green-600" />
                                    <div>
                                        <p className="font-medium text-green-900 dark:text-green-100">Chúc mừng! Bạn đã trở thành Coach</p>
                                        <p className="text-sm text-green-700 dark:text-green-300">
                                            Đơn đăng ký của bạn đã được phê duyệt. Bạn có thể truy cập bảng điều khiển Coach.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        )}

                        {application.status === "rejected" && application.adminFeedback && (
                            <div className="p-4 bg-red-50 dark:bg-red-900/20 rounded-lg">
                                <div className="flex items-center space-x-3">
                                    <XCircle className="h-5 w-5 text-red-600" />
                                    <div>
                                        <p className="font-medium text-red-900 dark:text-red-100">Đơn đăng ký bị từ chối</p>
                                        <p className="text-sm text-red-700 dark:text-red-300 mt-1">{application.adminFeedback}</p>
                                    </div>
                                </div>
                            </div>
                        )}
                    </CardContent>
                </Card>
            </motion.div>

            {/* Certificates */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                <Card>
                    <CardHeader>
                        <div className="flex items-center justify-between">
                            <div>
                                <CardTitle className="flex items-center space-x-2">
                                    <FileText className="h-5 w-5 text-blue-600" />
                                    <span>Chứng chỉ của tôi</span>
                                </CardTitle>
                                <CardDescription>Quản lý các chứng chỉ để đăng ký trở thành Coach</CardDescription>
                            </div>
                            <Button onClick={() => setUploadDialogOpen(true)} className="bg-blue-600 hover:bg-blue-700">
                                <Plus className="h-4 w-4 mr-2" />
                                Thêm chứng chỉ
                            </Button>
                        </div>
                    </CardHeader>
                    <CardContent>
                        {certificates.length === 0 ? (
                            <div className="text-center py-8">
                                <FileText className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                                <p className="text-gray-500">Chưa có chứng chỉ nào</p>
                                <p className="text-sm text-gray-400 mt-1">Thêm chứng chỉ để đăng ký trở thành Coach</p>
                            </div>
                        ) : (
                            <div className="space-y-4">
                                {certificates.map((cert, index) => (
                                    <motion.div
                                        key={cert.id}
                                        initial={{ opacity: 0, x: -20 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: index * 0.1 }}
                                        className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                                    >
                                        <div className="flex items-center space-x-4">
                                            <div className="p-2 bg-blue-100 dark:bg-blue-900 rounded-lg">
                                                <FileText className="h-5 w-5 text-blue-600" />
                                            </div>
                                            <div>
                                                <h4 className="font-semibold text-gray-900 dark:text-white">{cert.name}</h4>
                                                <div className="flex items-center space-x-4 text-sm text-gray-600 dark:text-gray-400">
                                                    <span>Cấp bởi: {cert.issuer}</span>
                                                    <span>Ngày cấp: {cert.issueDate}</span>
                                                    {cert.expiryDate && <span>Hết hạn: {cert.expiryDate}</span>}
                                                </div>
                                                <p className="text-xs text-gray-500 mt-1">Tải lên: {cert.uploadDate}</p>
                                            </div>
                                        </div>

                                        <div className="flex items-center space-x-3">
                                            {getStatusBadge(cert.status)}
                                            <div className="flex space-x-2">
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => {
                                                        setSelectedCertificate(cert)
                                                        setViewCertDialogOpen(true)
                                                    }}
                                                >
                                                    <Eye className="h-4 w-4" />
                                                </Button>
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => {
                                                        // Download certificate
                                                        const link = document.createElement("a")
                                                        link.href = cert.fileUrl
                                                        link.download = cert.fileName
                                                        link.click()
                                                    }}
                                                >
                                                    <Download className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>
                        )}
                    </CardContent>
                </Card>
            </motion.div>

            {/* Upload Certificate Dialog */}
            <Dialog open={uploadDialogOpen} onOpenChange={setUploadDialogOpen}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Thêm chứng chỉ mới</DialogTitle>
                        <DialogDescription>Tải lên chứng chỉ của bạn để đăng ký trở thành Coach</DialogDescription>
                    </DialogHeader>

                    <div className="space-y-4">
                        <div>
                            <Label htmlFor="cert-name">Tên chứng chỉ *</Label>
                            <Input
                                id="cert-name"
                                value={newCertificate.name}
                                onChange={(e) => setNewCertificate((prev) => ({ ...prev, name: e.target.value }))}
                                placeholder="VD: Chứng chỉ Tư vấn Sức khỏe"
                            />
                        </div>

                        <div>
                            <Label htmlFor="cert-issuer">Đơn vị cấp *</Label>
                            <Input
                                id="cert-issuer"
                                value={newCertificate.issuer}
                                onChange={(e) => setNewCertificate((prev) => ({ ...prev, issuer: e.target.value }))}
                                placeholder="VD: Bộ Y tế"
                            />
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <Label htmlFor="issue-date">Ngày cấp *</Label>
                                <Input
                                    id="issue-date"
                                    type="date"
                                    value={newCertificate.issueDate}
                                    onChange={(e) => setNewCertificate((prev) => ({ ...prev, issueDate: e.target.value }))}
                                />
                            </div>
                            <div>
                                <Label htmlFor="expiry-date">Ngày hết hạn</Label>
                                <Input
                                    id="expiry-date"
                                    type="date"
                                    value={newCertificate.expiryDate}
                                    onChange={(e) => setNewCertificate((prev) => ({ ...prev, expiryDate: e.target.value }))}
                                />
                            </div>
                        </div>

                        <div>
                            <Label htmlFor="cert-type">Loại chứng chỉ</Label>
                            <select
                                id="cert-type"
                                value={newCertificate.type}
                                onChange={(e) =>
                                    setNewCertificate((prev) => ({ ...prev, type: e.target.value as Certificate["type"] }))
                                }
                                className="w-full p-2 border rounded-md"
                            >
                                <option value="health">Sức khỏe</option>
                                <option value="psychology">Tâm lý học</option>
                                <option value="counseling">Tư vấn</option>
                                <option value="other">Khác</option>
                            </select>
                        </div>

                        <div>
                            <Label htmlFor="cert-file">File chứng chỉ *</Label>
                            <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md">
                                <div className="space-y-1 text-center">
                                    <Upload className="mx-auto h-12 w-12 text-gray-400" />
                                    <div className="flex text-sm text-gray-600">
                                        <label
                                            htmlFor="cert-file"
                                            className="relative cursor-pointer bg-white rounded-md font-medium text-blue-600 hover:text-blue-500 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-blue-500"
                                        >
                                            <span>Tải lên file</span>
                                            <input
                                                id="cert-file"
                                                type="file"
                                                className="sr-only"
                                                accept=".pdf,.jpg,.jpeg,.png"
                                                onChange={handleFileUpload}
                                            />
                                        </label>
                                        <p className="pl-1">hoặc kéo thả</p>
                                    </div>
                                    <p className="text-xs text-gray-500">PDF, PNG, JPG tối đa 10MB</p>
                                    {newCertificate.file && (
                                        <p className="text-sm text-green-600 mt-2">Đã chọn: {newCertificate.file.name}</p>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="flex justify-end space-x-2">
                        <Button variant="outline" onClick={() => setUploadDialogOpen(false)}>
                            Hủy
                        </Button>
                        <Button
                            onClick={handleUploadCertificate}
                            disabled={!newCertificate.file || !newCertificate.name || !newCertificate.issuer}
                            className="bg-blue-600 hover:bg-blue-700"
                        >
                            Tải lên
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Coach Application Dialog */}
            <Dialog open={applicationDialogOpen} onOpenChange={setApplicationDialogOpen}>
                <DialogContent className="sm:max-w-2xl">
                    <DialogHeader>
                        <DialogTitle>Đơn đăng ký trở thành Coach</DialogTitle>
                        <DialogDescription>Hoàn thiện thông tin để nộp đơn đăng ký trở thành Coach</DialogDescription>
                    </DialogHeader>

                    <div className="space-y-6">
                        <div>
                            <Label htmlFor="motivation">Động lực trở thành Coach *</Label>
                            <Textarea
                                id="motivation"
                                value={application.motivation}
                                onChange={(e) => setApplication((prev) => ({ ...prev, motivation: e.target.value }))}
                                placeholder="Chia sẻ lý do bạn muốn trở thành Coach..."
                                rows={4}
                            />
                        </div>

                        <div>
                            <Label htmlFor="experience">Kinh nghiệm liên quan *</Label>
                            <Textarea
                                id="experience"
                                value={application.experience}
                                onChange={(e) => setApplication((prev) => ({ ...prev, experience: e.target.value }))}
                                placeholder="Mô tả kinh nghiệm của bạn trong việc cai thuốc và hỗ trợ người khác..."
                                rows={4}
                            />
                        </div>

                        <div>
                            <Label>Chuyên môn *</Label>
                            <div className="mt-2 space-y-2">
                                {["Cai thuốc dần dần", "Phương pháp tự nhiên", "Hỗ trợ tâm lý", "Dinh dưỡng", "Thể dục"].map((spec) => (
                                    <label key={spec} className="flex items-center space-x-2">
                                        <input
                                            type="checkbox"
                                            checked={application.specialization.includes(spec)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setApplication((prev) => ({
                                                        ...prev,
                                                        specialization: [...prev.specialization, spec],
                                                    }))
                                                } else {
                                                    setApplication((prev) => ({
                                                        ...prev,
                                                        specialization: prev.specialization.filter((s) => s !== spec),
                                                    }))
                                                }
                                            }}
                                        />
                                        <span className="text-sm">{spec}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                            <h4 className="font-medium mb-2">Chứng chỉ đã xác minh</h4>
                            {certificates.filter((cert) => cert.status === "verified").length === 0 ? (
                                <p className="text-sm text-red-600">Bạn cần có ít nhất một chứng chỉ đã được xác minh để nộp đơn</p>
                            ) : (
                                <div className="space-y-2">
                                    {certificates
                                        .filter((cert) => cert.status === "verified")
                                        .map((cert) => (
                                            <div key={cert.id} className="flex items-center space-x-2">
                                                <CheckCircle className="h-4 w-4 text-green-600" />
                                                <span className="text-sm">{cert.name}</span>
                                            </div>
                                        ))}
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="flex justify-end space-x-2">
                        <Button variant="outline" onClick={() => setApplicationDialogOpen(false)}>
                            Hủy
                        </Button>
                        <Button
                            onClick={handleSubmitApplication}
                            disabled={!canSubmitApplication()}
                            className="bg-blue-600 hover:bg-blue-700"
                        >
                            Nộp đơn đăng ký
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* View Certificate Dialog */}
            <Dialog open={viewCertDialogOpen} onOpenChange={setViewCertDialogOpen}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle>Chi tiết chứng chỉ</DialogTitle>
                    </DialogHeader>

                    {selectedCertificate && (
                        <div className="space-y-4">
                            <div>
                                <Label>Tên chứng chỉ</Label>
                                <p className="text-sm text-gray-600 dark:text-gray-400">{selectedCertificate.name}</p>
                            </div>

                            <div>
                                <Label>Đơn vị cấp</Label>
                                <p className="text-sm text-gray-600 dark:text-gray-400">{selectedCertificate.issuer}</p>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <Label>Ngày cấp</Label>
                                    <p className="text-sm text-gray-600 dark:text-gray-400">{selectedCertificate.issueDate}</p>
                                </div>
                                {selectedCertificate.expiryDate && (
                                    <div>
                                        <Label>Ngày hết hạn</Label>
                                        <p className="text-sm text-gray-600 dark:text-gray-400">{selectedCertificate.expiryDate}</p>
                                    </div>
                                )}
                            </div>

                            <div>
                                <Label>Trạng thái</Label>
                                <div className="mt-1">{getStatusBadge(selectedCertificate.status)}</div>
                            </div>

                            {selectedCertificate.adminNotes && (
                                <div>
                                    <Label>Ghi chú từ Admin</Label>
                                    <p className="text-sm text-gray-600 dark:text-gray-400 p-2 bg-gray-50 dark:bg-gray-800 rounded">
                                        {selectedCertificate.adminNotes}
                                    </p>
                                </div>
                            )}
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}
