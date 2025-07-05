import type React from "react"
import { Badge } from "@/components/ui/badge"
import { Sparkles } from "lucide-react"
import type { UserSubscription } from "@/pages/plan/styles/ui/types/plan"

interface PremiumBannerProps {
    subscription: UserSubscription
}

export const PremiumBanner: React.FC<PremiumBannerProps> = ({ subscription }) => {
    return (
        <div className="mb-6 bg-gradient-to-r from-emerald-500 to-emerald-600 text-white p-4 rounded-lg shadow-lg">
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <Sparkles className="w-5 h-5" />
                    <span className="font-medium">
                        Gói Premium {subscription.duration} - Hết hạn: {subscription.expiryDate?.toLocaleDateString("vi-VN")}
                    </span>
                </div>
                <Badge className="bg-white/20 text-white">PREMIUM</Badge>
            </div>
        </div>
    )
}
