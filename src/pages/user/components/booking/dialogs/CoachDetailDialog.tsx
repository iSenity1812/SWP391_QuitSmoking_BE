"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Star, Award } from "lucide-react"
import type { Coach } from "../types/booking.types"

interface CoachDetailDialogProps {
    isOpen: boolean
    onClose: () => void
    selectedCoach: Coach | null
    onBooking: (coach: Coach) => void
}

export function CoachDetailDialog({ isOpen, onClose, selectedCoach, onBooking }: CoachDetailDialogProps) {
    if (!selectedCoach) return null

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Thông Tin Chuyên Gia</DialogTitle>
                </DialogHeader>
                <div className="space-y-6">
                    <div className="flex items-start space-x-6">
                        <Avatar className="h-24 w-24">
                            <AvatarImage src={selectedCoach.avatar || "/placeholder.svg"} />
                            <AvatarFallback className="text-2xl">{selectedCoach.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div className="flex-1">
                            <h2 className="text-2xl font-bold text-slate-900 dark:text-white">{selectedCoach.name}</h2>
                            <p className="text-lg text-blue-600 dark:text-blue-400 mb-2">{selectedCoach.title}</p>
                            <div className="flex items-center space-x-4 text-sm text-slate-600 dark:text-slate-400">
                                <div className="flex items-center space-x-1">
                                    <Star className="w-4 h-4 text-yellow-500" />
                                    <span>
                                        {selectedCoach.rating} ({selectedCoach.reviewCount} đánh giá)
                                    </span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Award className="w-4 h-4" />
                                    <span>{selectedCoach.experience} năm kinh nghiệm</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div className="text-center p-4 bg-slate-50 dark:bg-slate-800/50 rounded-lg">
                            <div className="text-2xl font-bold text-green-600 dark:text-green-400">{selectedCoach.successRate}%</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Tỷ lệ thành công</p>
                        </div>
                        <div className="text-center p-4 bg-slate-50 dark:bg-slate-800/50 rounded-lg">
                            <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">{selectedCoach.totalClients}</div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Khách hàng</p>
                        </div>
                        <div className="text-center p-4 bg-slate-50 dark:bg-slate-800/50 rounded-lg">
                            <div className="text-2xl font-bold text-purple-600 dark:text-purple-400">
                                {selectedCoach.languages.length}
                            </div>
                            <p className="text-sm text-slate-600 dark:text-slate-400">Ngôn ngữ</p>
                        </div>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-2 text-slate-900 dark:text-white">Giới thiệu</h4>
                        <p className="text-slate-600 dark:text-slate-400">{selectedCoach.bio}</p>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-2 text-slate-900 dark:text-white">Chuyên môn</h4>
                        <div className="flex flex-wrap gap-2">
                            {selectedCoach.specializations.map((spec, index) => (
                                <Badge key={index} className="bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400">
                                    {spec}
                                </Badge>
                            ))}
                        </div>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-2 text-slate-900 dark:text-white">Ngôn ngữ</h4>
                        <div className="flex flex-wrap gap-2">
                            {selectedCoach.languages.map((lang, index) => (
                                <Badge key={index} variant="outline">
                                    {lang}
                                </Badge>
                            ))}
                        </div>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-2 text-slate-900 dark:text-white">Bảng giá</h4>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg">
                                <p className="font-medium text-slate-900 dark:text-white">Tư vấn cá nhân</p>
                                <p className="text-lg font-bold text-blue-600 dark:text-blue-400">
                                    {selectedCoach.pricing.individual.toLocaleString("vi-VN")} VNĐ
                                </p>
                            </div>
                            <div className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg">
                                <p className="font-medium text-slate-900 dark:text-white">Tư vấn nhóm</p>
                                <p className="text-lg font-bold text-green-600 dark:text-green-400">
                                    {selectedCoach.pricing.group.toLocaleString("vi-VN")} VNĐ
                                </p>
                            </div>
                            <div className="p-3 border border-slate-200 dark:border-slate-700 rounded-lg">
                                <p className="font-medium text-slate-900 dark:text-white">Khẩn cấp</p>
                                <p className="text-lg font-bold text-red-600 dark:text-red-400">
                                    {selectedCoach.pricing.emergency.toLocaleString("vi-VN")} VNĐ
                                </p>
                            </div>
                        </div>
                    </div>

                    <div className="flex justify-end space-x-3">
                        <Button variant="outline" onClick={onClose}>
                            Đóng
                        </Button>
                        <Button
                            onClick={() => {
                                onClose()
                                onBooking(selectedCoach)
                            }}
                            className="bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600"
                        >
                            Đặt Lịch Ngay
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}
