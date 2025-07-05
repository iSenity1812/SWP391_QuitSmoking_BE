import { Crown, Sparkles } from "lucide-react"

interface PremiumHeaderProps {
    userSubscription: "free" | "premium"
}

export function PremiumHeader({ userSubscription }: PremiumHeaderProps) {
    return (
        <div className="bg-gradient-to-r from-amber-500 to-orange-500 text-white p-6 rounded-lg shadow-lg">
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Crown className="w-8 h-8" />
                    <div>
                        <h2 className="text-2xl font-bold">Đặt Lịch Chuyên Gia</h2>
                        <p className="text-amber-100">Tư vấn 1:1 với các chuyên gia hàng đầu về cai thuốc lá</p>
                    </div>
                </div>
                <div className="flex items-center gap-2 bg-white/20 px-4 py-2 rounded-full">
                    <Sparkles className="w-5 h-5" />
                    <span className="font-medium">{userSubscription === "premium" ? "PREMIUM ACTIVE" : "PREMIUM"}</span>
                </div>
            </div>
        </div>
    )
}
