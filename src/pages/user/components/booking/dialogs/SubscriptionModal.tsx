"use client"

import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Crown, Star, CheckCircle, CreditCard } from "lucide-react"
import type { Coach } from "../types/booking.types"

interface SubscriptionModalProps {
    isOpen: boolean
    onClose: () => void
    selectedCoach: Coach | null
}

export function SubscriptionModal({ isOpen, onClose, selectedCoach }: SubscriptionModalProps) {
    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle className="text-center">
                        <div className="flex items-center justify-center gap-2 mb-2">
                            <Crown className="w-6 h-6 text-amber-500" />
                            <span>Nâng cấp Premium</span>
                        </div>
                    </DialogTitle>
                </DialogHeader>
                <div className="space-y-6">
                    {selectedCoach && (
                        <div className="text-center">
                            <Avatar className="h-20 w-20 mx-auto mb-4">
                                <AvatarImage src={selectedCoach.avatar || "/placeholder.svg"} />
                                <AvatarFallback className="text-xl">{selectedCoach.name.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <h3 className="font-semibold text-lg text-slate-900 dark:text-white">{selectedCoach.name}</h3>
                            <p className="text-sm text-blue-600 dark:text-blue-400">{selectedCoach.title}</p>
                            <div className="flex items-center justify-center space-x-2 mt-2">
                                <Star className="w-4 h-4 text-yellow-500" />
                                <span className="text-sm text-slate-600 dark:text-slate-400">
                                    {selectedCoach.rating} ({selectedCoach.reviewCount} đánh giá)
                                </span>
                            </div>
                        </div>
                    )}

                    <div className="bg-gradient-to-r from-amber-50 to-orange-50 dark:from-amber-900/20 dark:to-orange-900/20 p-4 rounded-lg">
                        <h4 className="font-semibold text-slate-900 dark:text-white mb-3">Tính năng Premium bao gồm:</h4>
                        <ul className="space-y-2 text-sm text-slate-600 dark:text-slate-400">
                            <li className="flex items-center gap-2">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                Tư vấn 1:1 với chuyên gia
                            </li>
                            <li className="flex items-center gap-2">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                Đặt lịch linh hoạt 24/7
                            </li>
                            <li className="flex items-center gap-2">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                Tư vấn khẩn cấp
                            </li>
                            <li className="flex items-center gap-2">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                Theo dõi tiến độ chi tiết
                            </li>
                            <li className="flex items-center gap-2">
                                <CheckCircle className="w-4 h-4 text-green-500" />
                                Hỗ trợ đa phương tiện
                            </li>
                        </ul>
                    </div>

                    <div className="space-y-3">
                        <div className="bg-white dark:bg-slate-800 border-2 border-amber-200 dark:border-amber-800 rounded-lg p-4">
                            <div className="flex items-center justify-between mb-2">
                                <h4 className="font-semibold text-slate-900 dark:text-white">Gói Premium</h4>
                                <Badge className="bg-amber-100 text-amber-800 dark:bg-amber-900/20 dark:text-amber-400">Phổ biến</Badge>
                            </div>
                            <div className="flex items-baseline gap-1 mb-2">
                                <span className="text-2xl font-bold text-slate-900 dark:text-white">299,000</span>
                                <span className="text-sm text-slate-600 dark:text-slate-400">VNĐ/tháng</span>
                            </div>
                            <p className="text-xs text-slate-500 dark:text-slate-400">Truy cập không giới hạn tất cả tính năng</p>
                        </div>

                        <div className="bg-slate-50 dark:bg-slate-800/50 rounded-lg p-4">
                            <div className="flex items-center justify-between mb-2">
                                <h4 className="font-semibold text-slate-900 dark:text-white">Gói Premium Năm</h4>
                                <Badge variant="outline" className="text-green-600 border-green-600">
                                    Tiết kiệm 20%
                                </Badge>
                            </div>
                            <div className="flex items-baseline gap-1 mb-2">
                                <span className="text-2xl font-bold text-slate-900 dark:text-white">2,390,000</span>
                                <span className="text-sm text-slate-600 dark:text-slate-400">VNĐ/năm</span>
                            </div>
                            <p className="text-xs text-slate-500 dark:text-slate-400">Chỉ 199,000 VNĐ/tháng khi thanh toán năm</p>
                        </div>
                    </div>

                    <div className="flex gap-2">
                        <Button variant="outline" onClick={onClose} className="flex-1">
                            Để sau
                        </Button>
                        <Button className="flex-1 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600">
                            <CreditCard className="w-4 h-4 mr-2" />
                            Nâng cấp ngay
                        </Button>
                    </div>

                    <p className="text-xs text-center text-slate-500 dark:text-slate-400">
                        Hủy bất cứ lúc nào. Không ràng buộc hợp đồng.
                    </p>
                </div>
            </DialogContent>
        </Dialog>
    )
}
