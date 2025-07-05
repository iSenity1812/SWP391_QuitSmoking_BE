"use client"

import { useState, useEffect, useCallback } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Trophy, Star, Users, Crown, Edit, Trash2, Plus, Target } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { achievementService } from '@/services/achievementService'
import { toast } from 'react-toastify';

interface Achievement {
    achievementId: number
    name: string
    description: string
    icon: string
    achievementType: 'DAYS_QUIT' | 'MONEY_SAVED' | 'CIGARETTES_NOT_SMOKED' | 'RESILIENCE' | 'HEALTH' | 'SOCIAL' | 'SPECIAL' | 'DAILY'
    requirements: string
    milestoneValue: number
    unlockedBy: number
    isActive: boolean
    createdAt: string
}

export function AchievementsManagement() {
    const [achievements, setAchievements] = useState<Achievement[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [selectedAchievement, setSelectedAchievement] = useState<Achievement | null>(null)
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        icon: "",
        achievementType: "DAYS_QUIT" as Achievement["achievementType"],
        requirements: "",
        milestoneValue: 1,
    })

    // Fetch achievements from backend
    const fetchAchievements = useCallback(async () => {
        setLoading(true)
        setError(null)
        try {
            const data = await achievementService.getAllAchievements()
            setAchievements(data.map(a => ({
                ...a,
                description: a.description ?? "",
                icon: a.icon ?? "",
                achievementType: a.achievementType ?? "DAYS_QUIT",
                requirements: a.requirements ?? "",
                milestoneValue: a.milestoneValue ?? 0,
                unlockedBy: a.unlockedBy ?? 0,
                isActive: a.isActive ?? true
            })))
        } catch (err: any) {
            setError(err.message || 'Lỗi khi tải danh sách thành tựu')
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchAchievements()
    }, [fetchAchievements])

    const handleCreateAchievement = async () => {
        if (!formData.name.trim()) {
            toast.error('Tên thành tựu không được để trống')
            return
        }
        if (!formData.milestoneValue || isNaN(Number(formData.milestoneValue))) {
            toast.error('Milestone (giá trị mốc) phải là số và không được để trống')
            return
        }
        setLoading(true)
        try {
            const payload: Partial<Achievement> = {
                achievementType: formData.achievementType,
                name: formData.name,
                icon: formData.icon,
                description: formData.description,
                milestoneValue: formData.milestoneValue,
                requirements: formData.requirements,
            };
            await achievementService.createAchievement(payload)
            toast.success('Tạo thành tựu thành công!')
            setIsCreateModalOpen(false)
            resetForm()
            fetchAchievements()
        } catch (err: any) {
            toast.error(err.message || 'Tạo thành tựu thất bại')
        } finally {
            setLoading(false)
        }
    }

    const handleEditAchievement = (achievement: Achievement) => {
        setSelectedAchievement(achievement)
        setFormData({
            name: achievement.name,
            description: achievement.description,
            icon: achievement.icon,
            achievementType: achievement.achievementType,
            requirements: achievement.requirements,
            milestoneValue: achievement.milestoneValue,
        })
        setIsEditModalOpen(true)
    }

    const handleUpdateAchievement = async () => {
        if (!selectedAchievement) return
        if (!formData.name.trim()) {
            toast.error('Tên thành tựu không được để trống')
            return
        }
        setLoading(true)
        try {
            await achievementService.updateAchievement(selectedAchievement.achievementId, formData)
            toast.success('Cập nhật thành tựu thành công!')
            setIsEditModalOpen(false)
            resetForm()
            fetchAchievements()
        } catch (err: any) {
            toast.error(err.message || 'Cập nhật thành tựu thất bại')
        } finally {
            setLoading(false)
        }
    }

    const resetForm = () => {
        setFormData({
            name: "",
            description: "",
            icon: "",
            achievementType: "DAYS_QUIT",
            requirements: "",
            milestoneValue: 1,
        })
        setSelectedAchievement(null)
    }

    const handleDeleteAchievement = async (achievementId: number) => {
        if (!window.confirm('Bạn có chắc chắn muốn xóa thành tựu này?')) return
        setLoading(true)
        try {
            await achievementService.deleteAchievement(achievementId)
            toast.success('Xóa thành tựu thành công!')
            fetchAchievements()
        } catch (err: any) {
            toast.error(err.message || 'Xóa thành tựu thất bại')
        } finally {
            setLoading(false)
        }
    }

    const toggleAchievementStatus = (achievementId: number) => {
        setAchievements((prev) =>
            prev.map((achievement) =>
                achievement.achievementId === achievementId ? { ...achievement, isActive: !achievement.isActive } : achievement,
            ),
        )
    }

    const getCategoryIcon = (category: string) => {
        switch (category) {
            case "streak":
                return <Target className="w-4 h-4" />
            case "milestone":
                return <Trophy className="w-4 h-4" />
            case "social":
                return <Users className="w-4 h-4" />
            case "premium":
                return <Crown className="w-4 h-4" />
            default:
                return <Star className="w-4 h-4" />
        }
    }

    const getCategoryColor = (category: string) => {
        switch (category) {
            case "streak":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "milestone":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "social":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "premium":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getRarityColor = (rarity: string) => {
        switch (rarity) {
            case "common":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            case "rare":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "epic":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            case "legendary":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getTypeLabel = (type: Achievement["achievementType"]) => {
        switch (type) {
            case "DAYS_QUIT": return "Chuỗi ngày";
            case "MONEY_SAVED": return "Tiết kiệm tiền";
            case "CIGARETTES_NOT_SMOKED": return "Điếu thuốc tránh được";
            case "RESILIENCE": return "Kiên trì";
            case "HEALTH": return "Sức khỏe";
            case "SOCIAL": return "Xã hội";
            case "SPECIAL": return "Đặc biệt";
            case "DAILY": return "Hàng ngày";
            default: return "Khác";
        }
    }

    return (
        <div className="space-y-6">
            {loading && <div>Đang tải dữ liệu...</div>}
            {error && <div className="text-red-500">{error}</div>}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex justify-between items-center">
                        <div>
                            <CardTitle className="text-slate-900 dark:text-white">Quản Lý Thành Tựu</CardTitle>
                            <CardDescription className="text-slate-600 dark:text-slate-400">
                                Tạo, chỉnh sửa và quản lý hệ thống thành tựu cho người dùng
                            </CardDescription>
                        </div>
                        <Button onClick={() => setIsCreateModalOpen(true)}>
                            <Plus className="w-4 h-4 mr-2" />
                            Tạo Thành Tựu Mới
                        </Button>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {achievements.map((achievement) => (
                            <Card
                                key={achievement.achievementId}
                                className={`bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600 ${!achievement.isActive ? "opacity-60" : ""
                                    }`}
                            >
                                <CardHeader className="pb-3">
                                    <div className="flex items-start justify-between">
                                        <div className="flex items-center space-x-2">
                                            <span className="text-2xl">{achievement.icon}</span>
                                            <div className="flex items-center space-x-1">
                                                {getCategoryIcon(achievement.achievementType)}
                                                <Badge className={getCategoryColor(achievement.achievementType)}>
                                                    {getTypeLabel(achievement.achievementType)}
                                                </Badge>
                                            </div>
                                        </div>
                                    </div>
                                    <CardTitle className="text-lg text-slate-900 dark:text-white">{achievement.name}</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-slate-600 dark:text-slate-400 mb-3">{achievement.description}</p>
                                    <div className="space-y-2 mb-4">
                                        <div className="flex justify-between text-sm">
                                            <span className="text-slate-500 dark:text-slate-400">Yêu cầu:</span>
                                            <span className="text-slate-700 dark:text-slate-300">{achievement.requirements}</span>
                                        </div>
                                        <div className="flex justify-between text-sm">
                                            <span className="text-slate-500 dark:text-slate-400">Đã mở khóa:</span>
                                            <span className="text-slate-700 dark:text-slate-300">{achievement.unlockedBy} người</span>
                                        </div>
                                    </div>
                                    <div className="flex items-center justify-between mb-4">
                                        <Badge variant={achievement.isActive ? "default" : "secondary"}>
                                            {achievement.isActive ? "Đang hoạt động" : "Tạm dừng"}
                                        </Badge>
                                        <span className="text-xs text-slate-500 dark:text-slate-400">
                                            {new Date(achievement.createdAt).toLocaleDateString("vi-VN")}
                                        </span>
                                    </div>
                                    <div className="flex space-x-2">
                                        <Button
                                            size="sm"
                                            variant="ghost"
                                            className="flex-1"
                                            onClick={() => handleEditAchievement(achievement)}
                                        >
                                            <Edit className="w-4 h-4 mr-1" />
                                            Sửa
                                        </Button>
                                        <Button
                                            size="sm"
                                            variant="ghost"
                                            onClick={() => toggleAchievementStatus(achievement.achievementId)}
                                            className="flex-1"
                                        >
                                            {achievement.isActive ? "Tạm dừng" : "Kích hoạt"}
                                        </Button>
                                        <Button
                                            size="sm"
                                            variant="ghost"
                                            onClick={() => handleDeleteAchievement(achievement.achievementId)}
                                            className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/20"
                                        >
                                            <Trash2 className="w-4 h-4" />
                                        </Button>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </CardContent>
            </Card>
            {/* Create Achievement Modal */}
            <Dialog open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen}>
                <DialogContent className="sm:max-w-[600px]">
                    <DialogHeader>
                        <DialogTitle>Tạo Thành Tựu Mới</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-name">Tên thành tựu</Label>
                            <Input
                                id="achievement-name"
                                value={formData.name}
                                onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                                placeholder="Ví dụ: Tuần Đầu Tiên"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-icon">Icon (Emoji)</Label>
                            <Input
                                id="achievement-icon"
                                value={formData.icon}
                                onChange={(e) => setFormData((prev) => ({ ...prev, icon: e.target.value }))}
                                placeholder="🎯"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-type">Danh mục</Label>
                            <select
                                id="achievement-type"
                                value={formData.achievementType}
                                onChange={(e) => setFormData((prev) => ({ ...prev, achievementType: e.target.value as Achievement["achievementType"] }))}
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="DAYS_QUIT">Chuỗi ngày không hút thuốc</option>
                                <option value="MONEY_SAVED">Tiết kiệm tiền</option>
                                <option value="CIGARETTES_NOT_SMOKED">Điếu thuốc tránh được</option>
                                <option value="RESILIENCE">Kiên trì/quay lại</option>
                                <option value="HEALTH">Sức khỏe</option>
                                <option value="SOCIAL">Xã hội</option>
                                <option value="SPECIAL">Đặc biệt</option>
                                <option value="DAILY">Hàng ngày</option>
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-requirements">Yêu cầu</Label>
                            <Input
                                id="achievement-requirements"
                                value={formData.requirements}
                                onChange={(e) => setFormData((prev) => ({ ...prev, requirements: e.target.value }))}
                                placeholder="Ví dụ: 7 ngày liên tiếp không hút thuốc"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-description">Mô tả</Label>
                            <Textarea
                                id="achievement-description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Mô tả chi tiết về thành tựu..."
                                className="min-h-[100px]"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="achievement-milestone">Giá trị mốc (Milestone)</Label>
                            <Input
                                id="achievement-milestone"
                                type="number"
                                value={formData.milestoneValue}
                                onChange={(e) => setFormData((prev) => ({ ...prev, milestoneValue: parseInt(e.target.value, 10) || 0 }))}
                                placeholder="Ví dụ: 7"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsCreateModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleCreateAchievement} disabled={!formData.name.trim() || !formData.description.trim()}>
                            Tạo Thành Tựu
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Edit Achievement Modal */}
            <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
                <DialogContent className="sm:max-w-[600px]">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Thành Tựu</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="edit-achievement-name">Tên thành tựu</Label>
                            <Input
                                id="edit-achievement-name"
                                value={formData.name}
                                onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                                placeholder="Ví dụ: Tuần Đầu Tiên"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-achievement-icon">Icon (Emoji)</Label>
                            <Input
                                id="edit-achievement-icon"
                                value={formData.icon}
                                onChange={(e) => setFormData((prev) => ({ ...prev, icon: e.target.value }))}
                                placeholder="🎯"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-achievement-type">Danh mục</Label>
                            <select
                                id="edit-achievement-type"
                                value={formData.achievementType}
                                onChange={(e) => setFormData((prev) => ({ ...prev, achievementType: e.target.value as Achievement["achievementType"] }))}
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="DAYS_QUIT">Chuỗi ngày không hút thuốc</option>
                                <option value="MONEY_SAVED">Tiết kiệm tiền</option>
                                <option value="CIGARETTES_NOT_SMOKED">Điếu thuốc tránh được</option>
                                <option value="RESILIENCE">Kiên trì/quay lại</option>
                                <option value="HEALTH">Sức khỏe</option>
                                <option value="SOCIAL">Xã hội</option>
                                <option value="SPECIAL">Đặc biệt</option>
                                <option value="DAILY">Hàng ngày</option>
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-achievement-requirements">Yêu cầu</Label>
                            <Input
                                id="edit-achievement-requirements"
                                value={formData.requirements}
                                onChange={(e) => setFormData((prev) => ({ ...prev, requirements: e.target.value }))}
                                placeholder="Ví dụ: 7 ngày liên tiếp không hút thuốc"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-achievement-description">Mô tả</Label>
                            <Textarea
                                id="edit-achievement-description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Mô tả chi tiết về thành tựu..."
                                className="min-h-[100px]"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleUpdateAchievement} disabled={!formData.name.trim() || !formData.description.trim()}>
                            Cập Nhật
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
