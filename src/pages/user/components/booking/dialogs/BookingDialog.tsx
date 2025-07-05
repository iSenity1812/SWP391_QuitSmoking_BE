"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Calendar, Star, Award, CheckCircle, Video, Zap } from "lucide-react"
import type { Coach, BookingForm } from "../types/booking.types"

interface BookingDialogProps {
    isOpen: boolean
    onClose: () => void
    selectedCoach: Coach | null
    bookingForm: BookingForm
    setBookingForm: (form: BookingForm) => void
    onBookAppointment: () => void
}

export function BookingDialog({
    isOpen,
    onClose,
    selectedCoach,
    bookingForm,
    setBookingForm,
    onBookAppointment,
}: BookingDialogProps) {
    if (!selectedCoach) return null

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-lg">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Calendar className="w-5 h-5" />
                        Đặt Lịch Tư Vấn
                    </DialogTitle>
                </DialogHeader>
                <div className="space-y-6">
                    {/* Selected Coach Info */}
                    <div className="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-4 rounded-lg border border-blue-200 dark:border-blue-800">
                        <div className="flex items-center space-x-4">
                            <Avatar className="h-16 w-16 border-2 border-white shadow-md">
                                <AvatarImage src={selectedCoach.avatar || "/placeholder.svg"} />
                                <AvatarFallback className="text-lg font-semibold">{selectedCoach.name.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <div className="flex-1">
                                <h3 className="font-semibold text-lg text-slate-900 dark:text-white">{selectedCoach.name}</h3>
                                <p className="text-sm text-blue-600 dark:text-blue-400 mb-1">{selectedCoach.title}</p>
                                <div className="flex items-center space-x-3 text-sm text-slate-600 dark:text-slate-400">
                                    <div className="flex items-center space-x-1">
                                        <Star className="w-4 h-4 text-yellow-500" />
                                        <span>{selectedCoach.rating}</span>
                                    </div>
                                    <div className="flex items-center space-x-1">
                                        <Award className="w-4 h-4" />
                                        <span>{selectedCoach.experience} năm</span>
                                    </div>
                                    <div className="flex items-center space-x-1">
                                        <CheckCircle className="w-4 h-4 text-green-500" />
                                        <span>{selectedCoach.successRate}%</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Booking Form */}
                    <div className="space-y-4">
                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">Ngày tư vấn</label>
                                <Input
                                    type="date"
                                    value={bookingForm.date}
                                    onChange={(e) => setBookingForm({ ...bookingForm, date: e.target.value })}
                                    min={new Date().toISOString().split("T")[0]}
                                    className="w-full"
                                />
                            </div>
                            <div>
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">Giờ tư vấn</label>
                                <select
                                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                    value={bookingForm.time}
                                    onChange={(e) => setBookingForm({ ...bookingForm, time: e.target.value })}
                                >
                                    <option value="">Chọn giờ</option>
                                    {selectedCoach.availability[bookingForm.date]?.map((time) => (
                                        <option key={time} value={time}>
                                            {time}
                                        </option>
                                    )) || <option disabled>Vui lòng chọn ngày trước</option>}
                                </select>
                            </div>
                        </div>

                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">Loại tư vấn</label>
                                <select
                                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                    value={bookingForm.type}
                                    onChange={(e) =>
                                        setBookingForm({ ...bookingForm, type: e.target.value as "individual" | "group" | "emergency" })
                                    }
                                >
                                    <option value="individual">
                                        Cá nhân ({selectedCoach.pricing.individual.toLocaleString("vi-VN")} VNĐ)
                                    </option>
                                    <option value="group">Nhóm ({selectedCoach.pricing.group.toLocaleString("vi-VN")} VNĐ)</option>
                                    <option value="emergency">
                                        Khẩn cấp ({selectedCoach.pricing.emergency.toLocaleString("vi-VN")} VNĐ)
                                    </option>
                                </select>
                            </div>
                            <div>
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">Phương thức</label>
                                <select
                                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-md bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
                                    value={bookingForm.method}
                                    onChange={(e) =>
                                        setBookingForm({ ...bookingForm, method: e.target.value as "phone" | "video" | "in-person" })
                                    }
                                >
                                    <option value="video">
                                        <Video className="w-4 h-4 inline mr-2" />
                                        Video call
                                    </option>
                                    <option value="phone">Điện thoại</option>
                                    <option value="in-person">Trực tiếp</option>
                                </select>
                            </div>
                        </div>

                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">
                                Thời gian (phút)
                            </label>
                            <Input
                                type="number"
                                placeholder="60"
                                value={bookingForm.duration}
                                onChange={(e) => setBookingForm({ ...bookingForm, duration: Number.parseInt(e.target.value) || 60 })}
                                min="30"
                                max="120"
                                step="15"
                            />
                        </div>

                        {bookingForm.method === "in-person" && (
                            <div>
                                <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">
                                    Địa điểm mong muốn
                                </label>
                                <Input
                                    placeholder="Nhập địa điểm bạn muốn gặp mặt..."
                                    value={bookingForm.location}
                                    onChange={(e) => setBookingForm({ ...bookingForm, location: e.target.value })}
                                />
                            </div>
                        )}

                        <div>
                            <label className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-1 block">
                                Mô tả vấn đề hoặc mục tiêu
                            </label>
                            <Textarea
                                placeholder="Chia sẻ về tình trạng hiện tại, mục tiêu cai thuốc, hoặc những khó khăn bạn đang gặp phải..."
                                value={bookingForm.notes}
                                onChange={(e) => setBookingForm({ ...bookingForm, notes: e.target.value })}
                                rows={3}
                            />
                        </div>
                    </div>

                    {/* Pricing Summary */}
                    <div className="bg-gradient-to-r from-amber-50 to-orange-50 dark:from-amber-900/20 dark:to-orange-900/20 p-4 rounded-lg border border-amber-200 dark:border-amber-800">
                        <div className="flex items-center justify-between mb-2">
                            <span className="font-medium text-slate-900 dark:text-white">Tổng chi phí:</span>
                            <span className="text-xl font-bold text-amber-600 dark:text-amber-400">
                                {selectedCoach.pricing[bookingForm.type].toLocaleString("vi-VN")} VNĐ
                            </span>
                        </div>
                        <div className="flex items-center gap-2 text-sm text-amber-700 dark:text-amber-300">
                            <Zap className="w-4 h-4" />
                            <span>Thanh toán sau khi tư vấn hoàn thành</span>
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex gap-3">
                        <Button variant="outline" onClick={onClose} className="flex-1">
                            Hủy
                        </Button>
                        <Button
                            onClick={onBookAppointment}
                            className="flex-1 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600"
                            disabled={!bookingForm.date || !bookingForm.time}
                        >
                            <CheckCircle className="w-4 h-4 mr-2" />
                            Xác Nhận Đặt Lịch
                        </Button>
                    </div>

                    <p className="text-xs text-center text-slate-500 dark:text-slate-400">
                        Chúng tôi sẽ liên hệ xác nhận lịch hẹn trong vòng 24 giờ
                    </p>
                </div>
            </DialogContent>
        </Dialog>
    )
}
