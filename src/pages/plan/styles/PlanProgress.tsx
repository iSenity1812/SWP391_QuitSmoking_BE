import type React from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { Target, CheckCircle2, Sparkles } from "lucide-react"

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

interface PlanProgressProps {
    plan: Plan
    smokeFreeDays: number
    moneySaved: number
    progress: number
    isSubscriptionActive: boolean
}

export const PlanProgress: React.FC<PlanProgressProps> = ({
    plan,
    smokeFreeDays,
    moneySaved,
    progress,
    isSubscriptionActive,
}) => {
    return (
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 shadow-xl">
            <CardHeader>
                <div className="flex items-center justify-between">
                    <div>
                        <CardTitle className="flex items-center gap-2">
                            <Target className="w-5 h-5 text-emerald-500" />
                            Tổng Quan Tiến Trình
                            {isSubscriptionActive && <Badge className="bg-emerald-500 text-white text-xs">PREMIUM</Badge>}
                        </CardTitle>
                        <CardDescription>Hành trình hướng tới tự do</CardDescription>
                    </div>
                    <Badge
                        variant="secondary"
                        className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300"
                    >
                        {Math.round(progress)}% Hoàn Thành
                    </Badge>
                </div>
            </CardHeader>
            <CardContent className="space-y-6">
                <div>
                    <div className="flex justify-between items-center mb-2">
                        <span className="text-sm font-medium">Tiến Trình Tổng Thể</span>
                        <span className="text-sm text-muted-foreground">
                            {smokeFreeDays} /{" "}
                            {Math.floor(
                                (new Date(plan.targetDate).getTime() - new Date(plan.startDate).getTime()) / (1000 * 3600 * 24),
                            )}{" "}
                            ngày
                        </span>
                    </div>
                    <Progress value={progress} className="h-3" />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div className="text-center p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-xl">
                        <div className="text-2xl font-bold text-emerald-600 dark:text-emerald-400">{smokeFreeDays}</div>
                        <div className="text-sm text-muted-foreground">Ngày Không Hút Thuốc</div>
                    </div>
                    <div className="text-center p-4 bg-green-50 dark:bg-green-900/20 rounded-xl">
                        <div className="text-2xl font-bold text-green-600 dark:text-green-400">
                            {moneySaved.toLocaleString("vi-VN")}
                        </div>
                        <div className="text-sm text-muted-foreground">VNĐ Tiết Kiệm</div>
                    </div>
                </div>

                {/* Premium Features */}
                {isSubscriptionActive && (
                    <div className="mt-4 p-4 bg-gradient-to-r from-emerald-50 to-blue-50 dark:from-emerald-900/10 dark:to-blue-900/10 rounded-lg border border-emerald-200 dark:border-emerald-700">
                        <h4 className="font-medium text-emerald-700 dark:text-emerald-300 mb-2 flex items-center gap-2">
                            <Sparkles className="w-4 h-4" />
                            Tính Năng Premium
                        </h4>
                        <div className="grid grid-cols-2 gap-2 text-sm">
                            <div className="flex items-center gap-2">
                                <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                                <span>Tư vấn chuyên gia</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                                <span>Báo cáo chi tiết</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                                <span>Hỗ trợ 24/7</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <CheckCircle2 className="w-3 h-3 text-emerald-500" />
                                <span>Kế hoạch cá nhân</span>
                            </div>
                        </div>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}
