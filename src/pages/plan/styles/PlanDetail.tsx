"use client"

import type React from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Edit3, Trash2, Cigarette } from "lucide-react"

interface Plan {
    id: number
    title: string
    description: string
    startDate: Date
    targetDate: Date
    dailyCigarettes: number
    motivation: string
    cigaretteType: string
}

interface PlanDetailsProps {
    plan: Plan
    onEdit: () => void
    onDelete: () => void
}

export const PlanDetails: React.FC<PlanDetailsProps> = ({ plan, onEdit, onDelete }) => {
    return (
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 shadow-xl">
            <CardHeader>
                <div className="flex justify-between items-center">
                    <div>
                        <CardTitle>{plan.title}</CardTitle>
                        <CardDescription>Kế hoạch cai thuốc của bạn</CardDescription>
                    </div>
                    <div className="flex gap-2">
                        <Button variant="outline" size="sm" onClick={onEdit}>
                            <Edit3 className="w-4 h-4 mr-2" />
                            Sửa
                        </Button>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={onDelete}
                            className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:text-red-400 dark:hover:text-red-300 dark:hover:bg-red-900/20"
                        >
                            <Trash2 className="w-4 h-4 mr-2" />
                            Xóa
                        </Button>
                    </div>
                </div>
            </CardHeader>
            <CardContent className="space-y-4">
                <div>
                    <h4 className="font-medium text-sm text-muted-foreground mb-1">Mô Tả</h4>
                    <p className="text-sm">{plan.description}</p>
                </div>
                <div>
                    <h4 className="font-medium text-sm text-muted-foreground mb-1">Động Lực</h4>
                    <p className="text-sm italic">{plan.motivation}</p>
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <h4 className="font-medium text-sm text-muted-foreground mb-1">Ngày Bắt Đầu</h4>
                        <p className="text-sm">{plan.startDate.toLocaleDateString("vi-VN")}</p>
                    </div>
                    <div>
                        <h4 className="font-medium text-sm text-muted-foreground mb-1">Ngày Mục Tiêu</h4>
                        <p className="text-sm">{plan.targetDate.toLocaleDateString("vi-VN")}</p>
                    </div>
                </div>
                <div>
                    <h4 className="font-medium text-sm text-muted-foreground mb-1">Thói Quen Trước Đây</h4>
                    <div className="flex items-center gap-2">
                        <Cigarette className="w-4 h-4 text-muted-foreground" />
                        <span className="text-sm">{plan.dailyCigarettes} điếu/ngày</span>
                    </div>
                    <p className="text-xs text-muted-foreground mt-1">{plan.cigaretteType}</p>
                </div>
            </CardContent>
        </Card>
    )
}
