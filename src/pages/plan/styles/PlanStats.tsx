import type React from "react"
import { CalendarIcon, DollarSign } from "lucide-react"
import { StatCard } from "./ui/StatCard"
import type { PlanCalculations } from "@/pages/plan/styles/ui/types/plan"

export const PlanStats: React.FC<PlanCalculations> = ({ days, saved }) => {
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <StatCard
                icon={CalendarIcon}
                title="Ngày Không Hút Thuốc"
                value={days.toString()}
                subtitle="ngày"
                gradient="from-emerald-400 to-emerald-600"
            />
            <StatCard
                icon={DollarSign}
                title="Tiền Tiết Kiệm"
                value={saved.toLocaleString("vi-VN")}
                subtitle="VNĐ"
                gradient="from-green-400 to-green-600"
            />
        </div>
    )
}
