import type React from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Award, CheckCircle2, Heart, TrendingUp } from "lucide-react"
import { MilestoneItem } from "./ui/MilestoneItem"

const MILESTONES = [
    { days: 1, title: "Ngày Đầu Tiên", description: "Cơ thể bắt đầu hồi phục", icon: CheckCircle2 },
    { days: 7, title: "Một Tuần", description: "Vị giác và khứu giác cải thiện", icon: Heart },
    { days: 30, title: "Một Tháng", description: "Tuần hoàn máu được cải thiện", icon: TrendingUp },
    { days: 90, title: "Ba Tháng", description: "Chức năng phổi tăng cường", icon: Award },
]

interface PlanMilestonesProps {
    smokeFreeDays: number
}

export const PlanMilestones: React.FC<PlanMilestonesProps> = ({ smokeFreeDays }) => {
    return (
        <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-xl border-2 border-emerald-100 dark:border-slate-700 shadow-xl">
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <Award className="w-5 h-5 text-emerald-500" />
                    Cột Mốc Quan Trọng
                </CardTitle>
                <CardDescription>Ăn mừng những thành tựu của bạn</CardDescription>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {MILESTONES.map((milestone, index) => (
                        <MilestoneItem key={milestone.days} milestone={milestone} smokeFreeDays={smokeFreeDays} index={index} />
                    ))}
                </div>
            </CardContent>
        </Card>
    )
}
