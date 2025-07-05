import { Card, CardContent } from "@/components/ui/card"
import { Target, Coins } from "lucide-react"
import type { User } from "../types/user-types"

interface StatsCardsProps {
    user: User
}

export function StatsCards({ user }: StatsCardsProps) {
    // Đảm bảo hiển thị đúng định dạng số cho tiền tiết kiệm và điếu đã tránh
    const moneySavedDisplay = typeof user.moneySaved === 'string' ? user.moneySaved : Number(user.moneySaved).toLocaleString('vi-VN')
    const cigarettesAvoidedDisplay = typeof user.cigarettesAvoided === 'string' ? user.cigarettesAvoided : Number(user.cigarettesAvoided).toLocaleString('vi-VN')

    return (
        <div className="space-y-8">
            {/* Top Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white border-0 shadow-lg overflow-hidden relative">
                    <CardContent className="p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-blue-100 text-sm font-medium">Điếu thuốc đã tránh</p>
                                <p className="text-5xl font-bold mt-2">{cigarettesAvoidedDisplay}</p>
                                <p className="text-blue-100 text-sm mt-2">~10 điếu/ngày</p>
                            </div>
                            <div className="bg-white/20 rounded-full p-4">
                                <Target className="h-12 w-12" />
                            </div>
                        </div>
                        {/* Decorative elements */}
                        <div className="absolute -bottom-4 -right-4 w-24 h-24 bg-white/10 rounded-full"></div>
                        <div className="absolute -top-2 -left-2 w-16 h-16 bg-white/5 rounded-full"></div>
                    </CardContent>
                </Card>

                <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white border-0 shadow-lg overflow-hidden relative">
                    <CardContent className="p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-purple-100 text-sm font-medium">Tiền tiết kiệm</p>
                                <p className="text-5xl font-bold mt-2">{moneySavedDisplay}đ</p>
                                <p className="text-purple-100 text-sm mt-2">~50,000đ/ngày</p>
                            </div>
                            <div className="bg-white/20 rounded-full p-4">
                                <Coins className="h-12 w-12" />
                            </div>
                        </div>
                        {/* Decorative elements */}
                        <div className="absolute -bottom-4 -right-4 w-24 h-24 bg-white/10 rounded-full"></div>
                        <div className="absolute -top-2 -left-2 w-16 h-16 bg-white/5 rounded-full"></div>
                    </CardContent>
                </Card>
            </div>


        </div>
    )
}
