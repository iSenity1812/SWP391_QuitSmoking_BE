"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import {
    User,
    Edit,
    Save,
    Star,
    Award,
    Calendar,
    Users,
    TrendingUp,
    Mail,
    Phone,
    MapPin,
    Clock,
    CheckCircle,
} from "lucide-react"
import { getQualifications, createQualification, updateQualification, deleteQualification } from "@/services/qualificationService"
import type { Qualification } from "@/services/qualificationService"
import { useAuth } from "@/hooks/useAuth"
import type { CoachProfile as CoachProfileType } from "@/services/coachService"
import { getCoachProfile, updateCoachProfile } from "@/services/coachService"

export function CoachProfile() {
    const [isEditing, setIsEditing] = useState(false)
    const { user } = useAuth()
    const coachId = user?.userId || ""
    const isCoachOwner = (user?.role === 'COACH' && user?.userId === coachId) || user?.role === 'SUPER_ADMIN'
    const [profile, setProfile] = useState<CoachProfileType | null>(null)
    const [editForm, setEditForm] = useState<Partial<CoachProfileType>>({})
    const [qualifications, setQualifications] = useState<Qualification[]>([])
    const [newQualification, setNewQualification] = useState<Partial<Qualification>>({ qualificationName: "", issuingOrganization: "", qualificationURL: "" })
    const [editingIndex, setEditingIndex] = useState<number | null>(null)
    const [editQualification, setEditQualification] = useState<Partial<Qualification>>({})

    useEffect(() => {
        if (profile?.coachId) {
            getQualifications(profile.coachId).then(setQualifications)
        }
    }, [profile?.coachId])

    useEffect(() => {
        if (coachId) {
            getCoachProfile(coachId).then(data => {
                setProfile(data)
                setEditForm(data)
            })
        }
    }, [coachId])

    const handleSave = async () => {
        if (!profile) return
        const updated = await updateCoachProfile(coachId, editForm)
        setProfile(updated)
        setEditForm(updated)
        setIsEditing(false)
    }

    const handleCancel = () => {
        setEditForm(profile || {})
        setIsEditing(false)
    }

    const handleAddQualification = async () => {
        if (!newQualification.qualificationName || !profile?.coachId) return
        await createQualification(profile.coachId, newQualification)
        setNewQualification({ qualificationName: "", issuingOrganization: "", qualificationURL: "" })
        getQualifications(profile.coachId).then(setQualifications)
    }

    const handleDeleteQualification = async (q: Qualification) => {
        if (!profile?.coachId) return
        await deleteQualification(profile.coachId, q.qualificationName)
        getQualifications(profile.coachId).then(setQualifications)
    }

    const handleEditQualification = (q: Qualification, idx: number) => {
        setEditingIndex(idx)
        setEditQualification(q)
    }

    const handleSaveEdit = async () => {
        if (editingIndex === null || !editQualification.qualificationName || !profile?.coachId) return
        await updateQualification(profile.coachId, editQualification.qualificationName, editQualification)
        setEditingIndex(null)
        setEditQualification({})
        getQualifications(profile.coachId).then(setQualifications)
    }

    const handleCancelEdit = () => {
        setEditingIndex(null)
        setEditQualification({})
    }

    return (
        <div className="space-y-6">
            {/* Profile Header */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                <CardContent className="p-6">
                    <div className="flex flex-col md:flex-row items-start md:items-center space-y-4 md:space-y-0 md:space-x-6">
                        <Avatar className="h-24 w-24">
                            <AvatarImage src={profile?.avatar || "/placeholder.svg"} />
                            <AvatarFallback className="text-2xl">{profile?.fullName?.charAt(0) || "C"}</AvatarFallback>
                        </Avatar>
                        <div className="flex-1">
                            <div className="flex items-center justify-between mb-2">
                                <h1 className="text-2xl font-bold text-slate-900 dark:text-white">{profile?.fullName || "Coach"}</h1>
                                {isCoachOwner && (
                                    <Button onClick={() => setIsEditing(true)} variant="outline" size="sm">
                                        <Edit className="w-4 h-4 mr-2" />
                                        Chỉnh sửa
                                    </Button>
                                )}
                            </div>
                            <p className="text-lg text-blue-600 dark:text-blue-400 mb-2">{profile?.title || ""}</p>
                            <div className="flex items-center space-x-4 text-sm text-slate-600 dark:text-slate-400">
                                <div className="flex items-center space-x-1">
                                    <MapPin className="w-4 h-4" />
                                    <span>{profile?.location || ""}</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Calendar className="w-4 h-4" />
                                    <span>{profile?.experience || 0} năm kinh nghiệm</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Star className="w-4 h-4 text-yellow-500" />
                                    <span>
                                        {profile?.stats?.rating || 0} ({profile?.stats?.reviewCount || 0} đánh giá)
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/20 rounded-lg flex items-center justify-center">
                                <Users className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">{profile?.stats?.totalClients || 0}</p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Tổng khách hàng</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-green-100 dark:bg-green-900/20 rounded-lg flex items-center justify-center">
                                <TrendingUp className="w-5 h-5 text-green-600 dark:text-green-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">{profile?.stats?.successRate || 0}%</p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Tỷ lệ thành công</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-purple-100 dark:bg-purple-900/20 rounded-lg flex items-center justify-center">
                                <Calendar className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">{profile?.stats?.totalSessions || 0}</p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Buổi tư vấn</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-yellow-100 dark:bg-yellow-900/20 rounded-lg flex items-center justify-center">
                                <Star className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">{profile?.stats?.rating || 0}</p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Đánh giá trung bình</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Profile Information */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2">
                            <User className="w-5 h-5 text-blue-500" />
                            <span>Thông Tin Cá Nhân</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Giới thiệu</label>
                            <p className="text-sm text-slate-600 dark:text-slate-400 mt-1">{profile?.coachBio || ""}</p>
                        </div>
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Email</label>
                            <div className="flex items-center space-x-2 mt-1">
                                <Mail className="w-4 h-4 text-slate-500" />
                                <span className="text-sm">{profile?.email || ""}</span>
                            </div>
                        </div>
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Điện thoại</label>
                            <div className="flex items-center space-x-2 mt-1">
                                <Phone className="w-4 h-4 text-slate-500" />
                                <span className="text-sm">{profile?.phone || ""}</span>
                            </div>
                        </div>
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Ngôn ngữ</label>
                            <div className="flex flex-wrap gap-2 mt-1">
                                {profile?.languages?.map((lang, index) => (
                                    <Badge key={index} variant="outline">
                                        {lang}
                                    </Badge>
                                ))}
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Specializations & Certifications */}
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <CardTitle className="flex items-center space-x-2">
                            <Award className="w-5 h-5 text-green-500" />
                            <span>Chuyên Môn & Chứng Chỉ</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Lĩnh vực chuyên môn</label>
                            <div className="flex flex-wrap gap-2 mt-1">
                                {profile?.specializations?.map((spec, index) => (
                                    <Badge key={index} className="bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400">
                                        {spec}
                                    </Badge>
                                ))}
                            </div>
                        </div>
                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300">Bằng cấp/Chứng chỉ</label>
                            <div className="space-y-2 mt-1">
                                {qualifications.map((q, index) => (
                                    <div key={index} className="flex flex-col md:flex-row md:items-center md:space-x-2 border-b pb-2">
                                        {editingIndex === index ? (
                                            <>
                                                <input className="border rounded px-2 py-1 mr-2" value={editQualification.qualificationName || ""} onChange={e => setEditQualification({ ...editQualification, qualificationName: e.target.value })} placeholder="Tên bằng cấp" />
                                                <input className="border rounded px-2 py-1 mr-2" value={editQualification.issuingOrganization || ""} onChange={e => setEditQualification({ ...editQualification, issuingOrganization: e.target.value })} placeholder="Nơi cấp" />
                                                <input className="border rounded px-2 py-1 mr-2" value={editQualification.qualificationURL || ""} onChange={e => setEditQualification({ ...editQualification, qualificationURL: e.target.value })} placeholder="Link chứng chỉ" />
                                                <button className="text-green-600 mr-2" onClick={handleSaveEdit}>Lưu</button>
                                                <button className="text-gray-500" onClick={handleCancelEdit}>Hủy</button>
                                            </>
                                        ) : (
                                            <>
                                                <div className="flex-1">
                                                    <span className="font-semibold">{q.qualificationName}</span>
                                                    {q.issuingOrganization && <span className="ml-2 text-xs text-slate-500">({q.issuingOrganization})</span>}
                                                    {q.qualificationURL && <a href={q.qualificationURL} target="_blank" rel="noopener noreferrer" className="ml-2 text-blue-500 underline">Xem chứng chỉ</a>}
                                                    <span className={`ml-2 text-xs ${q.isApproved ? "text-green-600" : "text-yellow-600"}`}>{q.isApproved ? "Đã duyệt" : "Chờ duyệt"}</span>
                                                    {q.approveBy && <span className="ml-2 text-xs text-slate-400">(Duyệt bởi: {q.approveBy})</span>}
                                                </div>
                                                {isCoachOwner && (
                                                    <div className="flex space-x-2 mt-2 md:mt-0">
                                                        <button className="text-blue-600" onClick={() => handleEditQualification(q, index)}>Sửa</button>
                                                        <button className="text-red-600" onClick={() => handleDeleteQualification(q)}>Xóa</button>
                                                    </div>
                                                )}
                                            </>
                                        )}
                                    </div>
                                ))}
                                {isCoachOwner && (
                                    <div className="flex flex-col md:flex-row md:items-center md:space-x-2 mt-2">
                                        <input className="border rounded px-2 py-1 mr-2" value={newQualification.qualificationName || ""} onChange={e => setNewQualification({ ...newQualification, qualificationName: e.target.value })} placeholder="Tên bằng cấp" />
                                        <input className="border rounded px-2 py-1 mr-2" value={newQualification.issuingOrganization || ""} onChange={e => setNewQualification({ ...newQualification, issuingOrganization: e.target.value })} placeholder="Nơi cấp" />
                                        <input className="border rounded px-2 py-1 mr-2" value={newQualification.qualificationURL || ""} onChange={e => setNewQualification({ ...newQualification, qualificationURL: e.target.value })} placeholder="Link chứng chỉ" />
                                        <button className="text-green-600" onClick={handleAddQualification}>Thêm</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Availability Schedule */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                        <Clock className="w-5 h-5 text-purple-500" />
                        <span>Lịch Làm Việc</span>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-7 gap-4">
                        {Object.entries(profile?.availability || {}).map(([day, hours]) => (
                            <div key={day} className="text-center p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                                <p className="font-medium text-sm capitalize mb-1">
                                    {day === "monday"
                                        ? "Thứ 2"
                                        : day === "tuesday"
                                            ? "Thứ 3"
                                            : day === "wednesday"
                                                ? "Thứ 4"
                                                : day === "thursday"
                                                    ? "Thứ 5"
                                                    : day === "friday"
                                                        ? "Thứ 6"
                                                        : day === "saturday"
                                                            ? "Thứ 7"
                                                            : "Chủ nhật"}
                                </p>
                                <p className="text-xs text-slate-600 dark:text-slate-400">{hours}</p>
                            </div>
                        ))}
                    </div>
                </CardContent>
            </Card>

            {/* Edit Profile Dialog */}
            <Dialog open={isEditing} onOpenChange={setIsEditing}>
                <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Hồ Sơ</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="text-sm font-medium">Họ tên</label>
                                <Input value={editForm.fullName || ""} onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })} />
                            </div>
                            <div>
                                <label className="text-sm font-medium">Chức danh</label>
                                <Input value={editForm.title || ""} onChange={(e) => setEditForm({ ...editForm, title: e.target.value })} />
                            </div>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="text-sm font-medium">Email</label>
                                <Input value={editForm.email || ""} onChange={(e) => setEditForm({ ...editForm, email: e.target.value })} />
                            </div>
                            <div>
                                <label className="text-sm font-medium">Điện thoại</label>
                                <Input value={editForm.phone || ""} onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} />
                            </div>
                        </div>
                        <div>
                            <label className="text-sm font-medium">Giới thiệu</label>
                            <Textarea
                                value={editForm.coachBio || ""}
                                onChange={(e) => setEditForm({ ...editForm, coachBio: e.target.value })}
                                rows={4}
                            />
                        </div>
                        <div>
                            <label className="text-sm font-medium">Địa điểm</label>
                            <Input
                                value={editForm.location || ""}
                                onChange={(e) => setEditForm({ ...editForm, location: e.target.value })}
                            />
                        </div>
                        <div className="flex space-x-2">
                            <Button onClick={handleSave} className="flex-1">
                                <Save className="w-4 h-4 mr-2" />
                                Lưu Thay Đổi
                            </Button>
                            <Button onClick={handleCancel} variant="outline" className="flex-1">
                                Hủy
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
