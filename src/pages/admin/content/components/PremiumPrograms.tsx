"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Crown, Users, Calendar, DollarSign, Edit, Trash2, Plus, UserCheck } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"

interface PremiumProgram {
    id: number
    name: string
    description: string
    features: string[]
    price: number
    duration: number // in days
    maxParticipants: number
    currentParticipants: number
    coachAssigned?: string
    status: "active" | "inactive" | "full"
    createdAt: string
    startDate: string
    endDate: string
}

export function PremiumPrograms() {
    const [programs, setPrograms] = useState<PremiumProgram[]>([
        {
            id: 1,
            name: "Gói Premium 30 Ngày",
            description: "Chương trình cai thuốc chuyên sâu với sự hỗ trợ của coach cá nhân",
            features: ["Coach cá nhân 1-1", "Kế hoạch tùy chỉnh", "Hỗ trợ 24/7", "Báo cáo tiến độ chi tiết"],
            price: 299000,
            duration: 30,
            maxParticipants: 50,
            currentParticipants: 35,
            coachAssigned: "Dr. Nguyễn Văn A",
            status: "active",
            createdAt: "2024-01-01T00:00:00Z",
            startDate: "2024-01-15T00:00:00Z",
            endDate: "2024-02-14T23:59:59Z",
        },
        {
            id: 2,
            name: "Gói Premium 90 Ngày",
            description: "Chương trình cai thuốc toàn diện với theo dõi dài hạn",
            features: [
                "Coach cá nhân 1-1",
                "Kế hoạch tùy chỉnh",
                "Hỗ trợ 24/7",
                "Báo cáo tiến độ chi tiết",
                "Tư vấn dinh dưỡng",
                "Hỗ trợ tâm lý",
            ],
            price: 799000,
            duration: 90,
            maxParticipants: 30,
            currentParticipants: 30,
            coachAssigned: "Dr. Trần Thị B",
            status: "full",
            createdAt: "2024-01-01T00:00:00Z",
            startDate: "2024-01-01T00:00:00Z",
            endDate: "2024-03-31T23:59:59Z",
        },
    ])

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [selectedProgram, setSelectedProgram] = useState<PremiumProgram | null>(null)
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        features: [""],
        price: 0,
        duration: 30,
        maxParticipants: 50,
        coachAssigned: "",
        startDate: "",
        endDate: "",
    })

    const handleCreateProgram = () => {
        const newProgram: PremiumProgram = {
            id: Math.max(...programs.map((p) => p.id)) + 1,
            name: formData.name,
            description: formData.description,
            features: formData.features.filter((f) => f.trim() !== ""),
            price: formData.price,
            duration: formData.duration,
            maxParticipants: formData.maxParticipants,
            currentParticipants: 0,
            coachAssigned: formData.coachAssigned || undefined,
            status: "active",
            createdAt: new Date().toISOString(),
            startDate: formData.startDate,
            endDate: formData.endDate,
        }
        setPrograms((prev) => [newProgram, ...prev])
        setIsCreateModalOpen(false)
        resetForm()
    }

    const handleEditProgram = (program: PremiumProgram) => {
        setSelectedProgram(program)
        setFormData({
            name: program.name,
            description: program.description,
            features: [...program.features, ""],
            price: program.price,
            duration: program.duration,
            maxParticipants: program.maxParticipants,
            coachAssigned: program.coachAssigned || "",
            startDate: program.startDate.split("T")[0],
            endDate: program.endDate.split("T")[0],
        })
        setIsEditModalOpen(true)
    }

    const handleUpdateProgram = () => {
        if (!selectedProgram) return
        setPrograms((prev) =>
            prev.map((program) =>
                program.id === selectedProgram.id
                    ? {
                        ...program,
                        ...formData,
                        features: formData.features.filter((f) => f.trim() !== ""),
                        coachAssigned: formData.coachAssigned || undefined,
                        startDate: formData.startDate + "T00:00:00Z",
                        endDate: formData.endDate + "T23:59:59Z",
                    }
                    : program,
            ),
        )
        setIsEditModalOpen(false)
        resetForm()
    }

    const resetForm = () => {
        setFormData({
            name: "",
            description: "",
            features: [""],
            price: 0,
            duration: 30,
            maxParticipants: 50,
            coachAssigned: "",
            startDate: "",
            endDate: "",
        })
        setSelectedProgram(null)
    }

    const addFeature = () => {
        setFormData((prev) => ({ ...prev, features: [...prev.features, ""] }))
    }

    const removeFeature = (index: number) => {
        setFormData((prev) => ({
            ...prev,
            features: prev.features.filter((_, i) => i !== index),
        }))
    }

    const updateFeature = (index: number, value: string) => {
        setFormData((prev) => ({
            ...prev,
            features: prev.features.map((f, i) => (i === index ? value : f)),
        }))
    }

    const handleDeleteProgram = (programId: number) => {
        setPrograms((prev) => prev.filter((program) => program.id !== programId))
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "active":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "inactive":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            case "full":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price)
    }

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex justify-between items-center">
                        <div>
                            <CardTitle className="text-slate-900 dark:text-white">Quản Lý Chương Trình Premium</CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Tạo và quản lý các gói premium cho người dùng
                            </CardDescription>
                        </div>
                        <Button onClick={() => setIsCreateModalOpen(true)}>
                            <Plus className="w-4 h-4 mr-2" />
                            Tạo Chương Trình Mới
                        </Button>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {programs.map((program) => (
                            <Card
                                key={program.id}
                                className="bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                            >
                                <CardHeader className="pb-3">
                                    <div className="flex items-start justify-between">
                                        <div className="flex items-center space-x-2">
                                            <Crown className="w-5 h-5 text-yellow-500" />
                                            <CardTitle className="text-lg text-slate-900 dark:text-white">{program.name}</CardTitle>
                                        </div>
                                        <Badge className={getStatusColor(program.status)}>
                                            {program.status === "active"
                                                ? "Đang hoạt động"
                                                : program.status === "full"
                                                    ? "Đã đầy"
                                                    : "Tạm dừng"}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-slate-600 dark:text-slate-400">{program.description}</p>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-4">
                                        {/* Price and Duration */}
                                        <div className="flex justify-between items-center p-3 bg-white dark:bg-slate-600/50 rounded-lg">
                                            <div className="flex items-center space-x-2">
                                                <DollarSign className="w-4 h-4 text-green-500" />
                                                <span className="font-semibold text-slate-900 dark:text-white">
                                                    {formatPrice(program.price)}
                                                </span>
                                            </div>
                                            <div className="flex items-center space-x-2">
                                                <Calendar className="w-4 h-4 text-blue-500" />
                                                <span className="text-slate-700 dark:text-slate-300">{program.duration} ngày</span>
                                            </div>
                                        </div>

                                        {/* Participants */}
                                        <div className="flex justify-between items-center p-3 bg-white dark:bg-slate-600/50 rounded-lg">
                                            <div className="flex items-center space-x-2">
                                                <Users className="w-4 h-4 text-purple-500" />
                                                <span className="text-slate-700 dark:text-slate-300">Học viên</span>
                                            </div>
                                            <span className="font-medium text-slate-900 dark:text-white">
                                                {program.currentParticipants}/{program.maxParticipants}
                                            </span>
                                        </div>

                                        {/* Coach */}
                                        {program.coachAssigned && (
                                            <div className="flex justify-between items-center p-3 bg-white dark:bg-slate-600/50 rounded-lg">
                                                <div className="flex items-center space-x-2">
                                                    <UserCheck className="w-4 h-4 text-blue-500" />
                                                    <span className="text-slate-700 dark:text-slate-300">Coach</span>
                                                </div>
                                                <span className="font-medium text-slate-900 dark:text-white">{program.coachAssigned}</span>
                                            </div>
                                        )}

                                        {/* Features */}
                                        <div>
                                            <h4 className="font-medium text-slate-900 dark:text-white mb-2">Tính năng bao gồm:</h4>
                                            <ul className="space-y-1">
                                                {program.features.map((feature, index) => (
                                                    <li key={index} className="text-sm text-slate-600 dark:text-slate-400 flex items-center">
                                                        <span className="w-1.5 h-1.5 bg-green-500 rounded-full mr-2"></span>
                                                        {feature}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>

                                        {/* Dates */}
                                        <div className="text-xs text-slate-500 dark:text-slate-400 space-y-1">
                                            <div>Bắt đầu: {new Date(program.startDate).toLocaleDateString("vi-VN")}</div>
                                            <div>Kết thúc: {new Date(program.endDate).toLocaleDateString("vi-VN")}</div>
                                        </div>

                                        {/* Actions */}
                                        <div className="flex space-x-2 pt-2">
                                            <Button size="sm" variant="ghost" className="flex-1" onClick={() => handleEditProgram(program)}>
                                                <Edit className="w-4 h-4 mr-1" />
                                                Chỉnh sửa
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="ghost"
                                                onClick={() => handleDeleteProgram(program.id)}
                                                className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/20"
                                            >
                                                <Trash2 className="w-4 h-4 mr-1" />
                                                Xóa
                                            </Button>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </CardContent>
            </Card>
            {/* Create Program Modal */}
            <Dialog open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen}>
                <DialogContent className="sm:max-w-[700px] max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Tạo Chương Trình Premium Mới</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="program-name">Tên chương trình</Label>
                            <Input
                                id="program-name"
                                value={formData.name}
                                onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                                placeholder="Ví dụ: Gói Premium 30 Ngày"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="program-description">Mô tả</Label>
                            <Textarea
                                id="program-description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Mô tả chi tiết về chương trình..."
                                className="min-h-[100px]"
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="grid gap-2">
                                <Label htmlFor="program-price">Giá (VND)</Label>
                                <Input
                                    id="program-price"
                                    type="number"
                                    value={formData.price}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, price: Number.parseInt(e.target.value) || 0 }))}
                                    placeholder="299000"
                                />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="program-duration">Thời gian (ngày)</Label>
                                <Input
                                    id="program-duration"
                                    type="number"
                                    value={formData.duration}
                                    onChange={(e) =>
                                        setFormData((prev) => ({ ...prev, duration: Number.parseInt(e.target.value) || 30 }))
                                    }
                                    placeholder="30"
                                />
                            </div>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="program-participants">Số lượng học viên tối đa</Label>
                            <Input
                                id="program-participants"
                                type="number"
                                value={formData.maxParticipants}
                                onChange={(e) =>
                                    setFormData((prev) => ({ ...prev, maxParticipants: Number.parseInt(e.target.value) || 50 }))
                                }
                                placeholder="50"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="program-coach">Coach phụ trách (tùy chọn)</Label>
                            <Input
                                id="program-coach"
                                value={formData.coachAssigned}
                                onChange={(e) => setFormData((prev) => ({ ...prev, coachAssigned: e.target.value }))}
                                placeholder="Dr. Nguyễn Văn A"
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="grid gap-2">
                                <Label htmlFor="program-start">Ngày bắt đầu</Label>
                                <Input
                                    id="program-start"
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, startDate: e.target.value }))}
                                />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="program-end">Ngày kết thúc</Label>
                                <Input
                                    id="program-end"
                                    type="date"
                                    value={formData.endDate}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, endDate: e.target.value }))}
                                />
                            </div>
                        </div>
                        <div className="grid gap-2">
                            <div className="flex justify-between items-center">
                                <Label>Tính năng bao gồm</Label>
                                <Button type="button" variant="outline" size="sm" onClick={addFeature}>
                                    <Plus className="w-4 h-4 mr-1" />
                                    Thêm tính năng
                                </Button>
                            </div>
                            <div className="space-y-2">
                                {formData.features.map((feature, index) => (
                                    <div key={index} className="flex gap-2">
                                        <Input
                                            value={feature}
                                            onChange={(e) => updateFeature(index, e.target.value)}
                                            placeholder="Ví dụ: Coach cá nhân 1-1"
                                        />
                                        {formData.features.length > 1 && (
                                            <Button type="button" variant="outline" size="sm" onClick={() => removeFeature(index)}>
                                                <Trash2 className="w-4 h-4" />
                                            </Button>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsCreateModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleCreateProgram} disabled={!formData.name.trim() || !formData.description.trim()}>
                            Tạo Chương Trình
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Edit Program Modal */}
            <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
                <DialogContent className="sm:max-w-[700px] max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Chương Trình Premium</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="edit-program-name">Tên chương trình</Label>
                            <Input
                                id="edit-program-name"
                                value={formData.name}
                                onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                                placeholder="Ví dụ: Gói Premium 30 Ngày"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-program-description">Mô tả</Label>
                            <Textarea
                                id="edit-program-description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Mô tả chi tiết về chương trình..."
                                className="min-h-[100px]"
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="grid gap-2">
                                <Label htmlFor="edit-program-price">Giá (VND)</Label>
                                <Input
                                    id="edit-program-price"
                                    type="number"
                                    value={formData.price}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, price: Number.parseInt(e.target.value) || 0 }))}
                                    placeholder="299000"
                                />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="edit-program-duration">Thời gian (ngày)</Label>
                                <Input
                                    id="edit-program-duration"
                                    type="number"
                                    value={formData.duration}
                                    onChange={(e) =>
                                        setFormData((prev) => ({ ...prev, duration: Number.parseInt(e.target.value) || 30 }))
                                    }
                                    placeholder="30"
                                />
                            </div>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-program-participants">Số lượng học viên tối đa</Label>
                            <Input
                                id="edit-program-participants"
                                type="number"
                                value={formData.maxParticipants}
                                onChange={(e) =>
                                    setFormData((prev) => ({ ...prev, maxParticipants: Number.parseInt(e.target.value) || 50 }))
                                }
                                placeholder="50"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-program-coach">Coach phụ trách (tùy chọn)</Label>
                            <Input
                                id="edit-program-coach"
                                value={formData.coachAssigned}
                                onChange={(e) => setFormData((prev) => ({ ...prev, coachAssigned: e.target.value }))}
                                placeholder="Dr. Nguyễn Văn A"
                            />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="grid gap-2">
                                <Label htmlFor="edit-program-start">Ngày bắt đầu</Label>
                                <Input
                                    id="edit-program-start"
                                    type="date"
                                    value={formData.startDate}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, startDate: e.target.value }))}
                                />
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="edit-program-end">Ngày kết thúc</Label>
                                <Input
                                    id="edit-program-end"
                                    type="date"
                                    value={formData.endDate}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, endDate: e.target.value }))}
                                />
                            </div>
                        </div>
                        <div className="grid gap-2">
                            <div className="flex justify-between items-center">
                                <Label>Tính năng bao gồm</Label>
                                <Button type="button" variant="outline" size="sm" onClick={addFeature}>
                                    <Plus className="w-4 h-4 mr-1" />
                                    Thêm tính năng
                                </Button>
                            </div>
                            <div className="space-y-2">
                                {formData.features.map((feature, index) => (
                                    <div key={index} className="flex gap-2">
                                        <Input
                                            value={feature}
                                            onChange={(e) => updateFeature(index, e.target.value)}
                                            placeholder="Ví dụ: Coach cá nhân 1-1"
                                        />
                                        {formData.features.length > 1 && (
                                            <Button type="button" variant="outline" size="sm" onClick={() => removeFeature(index)}>
                                                <Trash2 className="w-4 h-4" />
                                            </Button>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleUpdateProgram} disabled={!formData.name.trim() || !formData.description.trim()}>
                            Cập Nhật
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
